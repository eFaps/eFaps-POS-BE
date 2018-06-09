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
package org.efaps.pos.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.efaps.pos.config.IApi;
import org.efaps.pos.dto.PosInventoryEntryDto;
import org.efaps.pos.dto.WarehouseDto;
import org.efaps.pos.service.InventoryService;
import org.efaps.pos.util.Converter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(IApi.BASEPATH + "inventory")
public class InventoryController
{
    private final InventoryService service;

    public InventoryController(final InventoryService _service) {
        this.service = _service;
    }

    @GetMapping(path = "/warehouses", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<WarehouseDto> getWarehouses() {
        return this.service.getWarehouses().stream()
                        .map(warehouse -> Converter.toDto(warehouse))
                        .collect(Collectors.toList());
    }

    @GetMapping(path = "/{warehouseOid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PosInventoryEntryDto> getInventory(@PathVariable(name = "warehouseOid") final String _warehouseOid) {
        return this.service.getInventory(_warehouseOid).stream()
                        .map(entry -> Converter.toDto(entry))
                        .collect(Collectors.toList());
    }
}
