package org.efaps.pos.service;

import java.util.List;

import org.efaps.pos.entity.Warehouse;
import org.efaps.pos.respository.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventoryService
{
    private final WarehouseRepository warehouseRepository;

    @Autowired
    public InventoryService(final WarehouseRepository _warehouseRepository)
    {
        this.warehouseRepository = _warehouseRepository;
    }

    public List<Warehouse> getWarehouses()
    {
        return this.warehouseRepository.findAll();
    }
}
