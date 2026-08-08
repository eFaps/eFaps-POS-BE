/*
 * Copyright © 2003 - 2024 The eFaps Team (-)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.efaps.pos.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.config.ConfigProperties;
import org.efaps.pos.dto.ProductRelationType;
import org.efaps.pos.dto.ProductType;
import org.efaps.pos.dto.ValidateStockDto;
import org.efaps.pos.dto.ValidateStockResponseDto;
import org.efaps.pos.dto.ValidateStockResponseEntryDto;
import org.efaps.pos.entity.AbstractDocument;
import org.efaps.pos.entity.InventoryEntry;
import org.efaps.pos.entity.SyncInfo;
import org.efaps.pos.entity.Warehouse;
import org.efaps.pos.repository.InventoryRepository;
import org.efaps.pos.repository.ProductRepository;
import org.efaps.pos.repository.WarehouseRepository;
import org.efaps.pos.util.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventoryService
{

    private static final Logger LOG = LoggerFactory.getLogger(InventoryService.class);

    private final ConfigProperties configProperties;
    private final WorkspaceService workspaceService;
    private final WarehouseRepository warehouseRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final EFapsClient eFapsClient;

    @Autowired
    public InventoryService(final ConfigProperties configProperties,
                            final EFapsClient eFapsClient,
                            final WorkspaceService workspaceService,
                            final WarehouseRepository warehouseRepository,
                            final InventoryRepository inventoryRepository,
                            final ProductRepository productRepository)
    {
        this.configProperties = configProperties;
        this.eFapsClient = eFapsClient;
        this.workspaceService = workspaceService;
        this.warehouseRepository = warehouseRepository;
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }

    public List<Warehouse> getWarehouses()
    {
        return warehouseRepository.findAll();
    }

    public Collection<InventoryEntry> getInventory4Warehouse(final String _warehouseOid)
    {
        return inventoryRepository.findByWarehouseOid(_warehouseOid);
    }

    public Collection<InventoryEntry> getInventory4Product(final String productOid)
    {
        Collection<InventoryEntry> entries = inventoryRepository.findByProductOid(productOid);

        if (configProperties.getBeInst().getInventory().isCloudBased()) {
            final var minutes = configProperties.getBeInst().getInventory().getMaxAge();
            final var evict =  entries.stream()
                            .anyMatch(entry -> entry.getUpdatedAt() == null
                            || Duration.between(entry.getUpdatedAt(), Instant.now()).toMinutes() > minutes);
            if (entries.isEmpty() || evict) {
                final var remoteInventory = eFapsClient.getInventory(productOid);
                LOG.debug("Got inventory from remote for {} - {}", productOid, remoteInventory);
                if (!entries.isEmpty()) {
                    inventoryRepository.deleteAll(entries);
                }
                entries = remoteInventory.stream()
                                .map(Converter::toEntity)
                                .collect(Collectors.toList());
                entries.forEach(en -> {
                    inventoryRepository.save(en);
                });
            }
        }
        return entries;
    }

    public Warehouse getWarehouse(final String _oid)
    {
        return warehouseRepository.findOneByOid(_oid).orElse(null);
    }

    public void removeFromInventory(final String _workspaceOid,
                                    final AbstractDocument<?> _document)
    {
        final var warehouseOidOpt = workspaceService.getWarehouseOid4Workspace(_workspaceOid);
        if (warehouseOidOpt.isPresent()) {
            final var warehouseOid = warehouseOidOpt.get();
            _document.getItems().forEach(item -> {
                removeFromInventory(warehouseOid, item.getProductOid(), item.getQuantity());
            });
        }
    }

    private void removeFromInventory(final String warehouseOid,
                                     final String productOid,
                                     final BigDecimal quantity)
    {
        final var entryOpt = inventoryRepository.findByWarehouseOidAndProductOid(warehouseOid, productOid);
        if (entryOpt.isPresent()) {
            final var entry = entryOpt.get();
            final var productOpt = productRepository.findById(productOid);
            if (productOpt.isPresent()) {
                final var product = productOpt.get();
                if (ProductType.PARTLIST.equals(product.getType())) {
                    product.getRelations().forEach(relation -> {
                        if (ProductRelationType.SALESBOM.equals(relation.getType())) {
                            removeFromInventory(warehouseOid, relation.getProductOid(),
                                            quantity.multiply(relation.getQuantity()));
                        }
                    });
                } else if (ProductType.STANDART.equals(product.getType())) {
                    entry.setQuantity(entry.getQuantity().subtract(quantity));
                    inventoryRepository.save(entry);
                }
            } else {
                inventoryRepository.delete(entry);
            }
        }
    }

    public ValidateStockResponseDto validateStock(final ValidateStockDto dto)
    {
        final List<ValidateStockResponseEntryDto> errorEntries = new ArrayList<>();
        for (final var stockEntry : dto.getEntries()) {
            final var prodVsQuantity = new HashMap<String, BigDecimal>();
            final var product = productRepository.findById(stockEntry.getProductOid()).orElseThrow();
            switch (product.getType()) {
                case STANDART:
                    prodVsQuantity.put(product.getOid(), stockEntry.getQuantity());
                    break;
                case PARTLIST:
                    product.getRelations().forEach(relation -> {
                        if (ProductRelationType.SALESBOM.equals(relation.getType())) {
                            final var relProd = productRepository.findById(relation.getProductOid()).orElseThrow();
                            if (relProd.getType().equals(ProductType.STANDART)) {
                                prodVsQuantity.put(relation.getProductOid(),
                                                stockEntry.getQuantity().multiply(relation.getQuantity()));
                            }
                        }
                    });
                    break;
                default:
                    // do nothing
            }
            for (final var entry : prodVsQuantity.entrySet()) {
                final var inventoryOpt = inventoryRepository.findByWarehouseOidAndProductOid(dto.getWarehouseOid(),
                                stockEntry.getProductOid());
                if (inventoryOpt.isEmpty()) {
                    errorEntries.add(ValidateStockResponseEntryDto.builder()
                                    .withQuantity(BigDecimal.ZERO)
                                    .withProductOid(entry.getKey())
                                    .build());
                } else {
                    final var inventory = inventoryOpt.get();
                    if (inventory.getQuantity().compareTo(entry.getValue()) < 0) {
                        errorEntries.add(ValidateStockResponseEntryDto.builder()
                                        .withQuantity(inventory.getQuantity())
                                        .withProductOid(entry.getKey())
                                        .build());
                    }
                }
            }
        }
        return ValidateStockResponseDto.builder().withStock(errorEntries.isEmpty()).withEntries(errorEntries).build();
    }

    public boolean syncInventory(final SyncInfo syncInfo)
    {
        LOG.info("Syncing Inventory");
        final List<InventoryEntry> entries = eFapsClient.getInventory().stream()
                        .map(Converter::toEntity)
                        .collect(Collectors.toList());
        if (!entries.isEmpty()) {
            inventoryRepository.deleteAll();
            entries.forEach(workspace -> inventoryRepository.save(workspace));
        }
        return true;
    }

    public boolean syncWarehouses(final SyncInfo syncInfo)
    {
        LOG.info("Syncing Warehouses");
        final List<Warehouse> warehouses = eFapsClient.getWarehouses().stream()
                        .map(Converter::toEntity)
                        .collect(Collectors.toList());
        if (!warehouses.isEmpty()) {
            warehouseRepository.deleteAll();
            warehouses.forEach(workspace -> warehouseRepository.save(workspace));
        }
        return true;
    }
}
