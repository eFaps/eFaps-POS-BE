/*
 * Copyright 2003 - 2018 The eFaps Team
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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;
import java.util.List;

import org.efaps.pos.entity.InventoryEntry;
import org.efaps.pos.entity.Warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureMockRestServiceServer
@ActiveProfiles(profiles = "test")
public class InventoryServiceTest
{

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private InventoryService inventoryService;

    @BeforeEach
    public void setup()
    {
        this.mongoTemplate.remove(new Query(), Warehouse.class);
        this.mongoTemplate.remove(new Query(), InventoryEntry.class);
    }

    @Test
    public void testGetWarehouses()
    {
        this.mongoTemplate.save(new Warehouse().setOid("1.1"));
        this.mongoTemplate.save(new Warehouse().setOid("1.2"));
        this.mongoTemplate.save(new Warehouse().setOid("1.3"));

        final List<Warehouse> warehouses = this.inventoryService.getWarehouses();
        assertEquals(3, warehouses.size());
    }

    @Test
    public void testGetWarehouse()
    {
        this.mongoTemplate.save(new Warehouse().setOid("1.1"));
        this.mongoTemplate.save(new Warehouse().setOid("1.2"));
        this.mongoTemplate.save(new Warehouse().setOid("1.3"));

        final Warehouse warehouse = this.inventoryService.getWarehouse("1.3");
        assertEquals("1.3", warehouse.getOid());
    }

    @Test
    public void testGetInventory4Warehouse()
    {
        this.mongoTemplate.save(new InventoryEntry().setOid("1.1").setWarehouseOid("55.44"));
        this.mongoTemplate.save(new InventoryEntry().setOid("1.2").setWarehouseOid("55.45"));
        this.mongoTemplate.save(new InventoryEntry().setOid("1.3").setWarehouseOid("55.44"));

        final Collection<InventoryEntry> entries = this.inventoryService.getInventory4Warehouse("55.44");
        assertEquals(2, entries.size());
    }

    @Test
    public void testGetInventory4Product()
    {
        this.mongoTemplate.save(new InventoryEntry().setOid("1.1").setProductOid("25.44"));
        this.mongoTemplate.save(new InventoryEntry().setOid("1.2").setProductOid("25.45"));
        this.mongoTemplate.save(new InventoryEntry().setOid("1.3").setProductOid("25.44"));

        final Collection<InventoryEntry> entries = this.inventoryService.getInventory4Product("25.44");
        assertEquals(2, entries.size());
    }
}
