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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.efaps.pos.dto.ProductRelationType;
import org.efaps.pos.dto.ProductType;
import org.efaps.pos.dto.ValidateStockDto;
import org.efaps.pos.dto.ValidateStockResponseDto;
import org.efaps.pos.dto.ValidateStockResponseEntryDto;
import org.efaps.pos.entity.AbstractDocument;
import org.efaps.pos.entity.InventoryEntry;
import org.efaps.pos.entity.Warehouse;
import org.efaps.pos.repository.InventoryRepository;
import org.efaps.pos.repository.ProductRepository;
import org.efaps.pos.repository.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventoryService
{

    private final WorkspaceService workspaceService;
    private final WarehouseRepository warehouseRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    @Autowired
    public InventoryService(final WorkspaceService _workspaceService,
                            final WarehouseRepository _warehouseRepository,
                            final InventoryRepository _inventoryRepository,
                            final ProductRepository _productRepository)
    {
        workspaceService = _workspaceService;
        warehouseRepository = _warehouseRepository;
        inventoryRepository = _inventoryRepository;
        productRepository = _productRepository;
    }

    public List<Warehouse> getWarehouses()
    {
        return warehouseRepository.findAll();
    }

    public Collection<InventoryEntry> getInventory4Warehouse(final String _warehouseOid)
    {
        return inventoryRepository.findByWarehouseOid(_warehouseOid);
    }

    public Collection<InventoryEntry> getInventory4Product(final String _warehouseOid)
    {
        return inventoryRepository.findByProductOid(_warehouseOid);
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
}
