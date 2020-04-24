/*
 * Copyright 2003 - 2020 The eFaps Team
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
 *
 */
package org.efaps.pos.service;

import java.util.Collection;
import java.util.List;

import org.efaps.pos.entity.AbstractDocument;
import org.efaps.pos.entity.InventoryEntry;
import org.efaps.pos.entity.Warehouse;
import org.efaps.pos.repository.InventoryRepository;
import org.efaps.pos.repository.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventoryService
{
    private final WorkspaceService workspaceService;
    private final WarehouseRepository warehouseRepository;
    private final InventoryRepository inventoryRepository;

    @Autowired
    public InventoryService(final WorkspaceService _workspaceService,
                            final WarehouseRepository _warehouseRepository,
                            final InventoryRepository _inventoryRepository)
    {
        workspaceService = _workspaceService;
        warehouseRepository = _warehouseRepository;
        inventoryRepository = _inventoryRepository;
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

    public void removeFromInventory(final String _workspaceOid, final AbstractDocument<?> _document)
    {
        final var warehouseOidOpt = workspaceService.getWarehouseOid4Workspace(_workspaceOid);
        if (warehouseOidOpt.isPresent()) {
            final var warehouseOid = warehouseOidOpt.get();
            _document.getItems().forEach(item -> {
                final var entryOpt = inventoryRepository.findByWarehouseOidAndProductOid(warehouseOid,
                                item.getProductOid());
                if (entryOpt.isPresent()) {
                    final var entry = entryOpt.get();
                    entry.setQuantity(entry.getQuantity().subtract(item.getQuantity()));
                    inventoryRepository.save(entry);
                }
            });
        }
    }
}
