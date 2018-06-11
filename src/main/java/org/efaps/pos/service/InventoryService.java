package org.efaps.pos.service;

import java.util.Collection;
import java.util.List;

import org.efaps.pos.entity.InventoryEntry;
import org.efaps.pos.entity.Warehouse;
import org.efaps.pos.respository.InventoryRepository;
import org.efaps.pos.respository.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventoryService
{
    private final WarehouseRepository warehouseRepository;
    private final InventoryRepository inventoryRepository;

    @Autowired
    public InventoryService(final WarehouseRepository _warehouseRepository,
                            final InventoryRepository _inventoryRepository)
    {
        this.warehouseRepository = _warehouseRepository;
        this.inventoryRepository = _inventoryRepository;
    }

    public List<Warehouse> getWarehouses()
    {
        return this.warehouseRepository.findAll();
    }

    public Collection<InventoryEntry> getInventory4Warehouse(final String _warehouseOid)
    {
        return this.inventoryRepository.findByWarehouseOid(_warehouseOid);
    }

    public Collection<InventoryEntry> getInventory4Product(final String _warehouseOid)
    {
        return this.inventoryRepository.findByProductOid(_warehouseOid);
    }

    public Warehouse getWarehouse(final String _oid)
    {
        return this.warehouseRepository.findOneByOid(_oid).orElse(null);
    }
}
