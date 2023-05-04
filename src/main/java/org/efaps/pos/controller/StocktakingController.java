/*
 * Copyright 2003 - 2023 The eFaps Team
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

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.efaps.pos.config.IApi;
import org.efaps.pos.dto.PosStocktakingDto;
import org.efaps.pos.dto.StockTakingEntryDto;
import org.efaps.pos.entity.User;
import org.efaps.pos.entity.Warehouse;
import org.efaps.pos.service.InventoryService;
import org.efaps.pos.service.StocktakingService;
import org.efaps.pos.service.UserService;
import org.efaps.pos.util.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(IApi.BASEPATH + "stocktakings")
public class StocktakingController
{

    private final StocktakingService stocktakingService;
    private final InventoryService inventoryService;
    private final UserService userService;

    public StocktakingController(final StocktakingService stocktakingService,
                                 final InventoryService inventoryService,
                                 final UserService userService)
    {
        this.stocktakingService = stocktakingService;
        this.inventoryService = inventoryService;
        this.userService = userService;
    }

    @GetMapping(path = "current", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PosStocktakingDto> getCurrentStocktaking(@RequestParam() String warehouseOid)
    {
        final var stocktakingOpt = stocktakingService.getCurrent(warehouseOid);

        ResponseEntity<PosStocktakingDto> ret;
        if (stocktakingOpt.isPresent()) {
            ret = new ResponseEntity<>(Converter.toDto(stocktakingOpt.get()), HttpStatus.OK);
        } else {
            ret = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ret;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public PosStocktakingDto create(final Authentication authentication,
                                    @RequestBody final String warehouseOid)
    {
        return Converter.toDto(
                        stocktakingService.createStocktaking((User) authentication.getPrincipal(), warehouseOid));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<PosStocktakingDto> getStocktakings(final Pageable pageable,
                                                   @RequestParam(required = false) boolean expand)
    {
        Page<PosStocktakingDto> result;
        if (expand) {
            final Map<String, Warehouse> warehouseMap = inventoryService.getWarehouses().stream()
                            .collect(Collectors.toMap(Warehouse::getOid, Function.identity()));
            final Map<String, User> userMap = userService.getUsers().stream()
                            .collect(Collectors.toMap(User::getOid, Function.identity()));
            result = stocktakingService.getStocktakings(pageable).map(stocktaking -> {
                return Converter.toBuilder(stocktaking)
                                .withWarehouse(Converter.toDto(warehouseMap.get(stocktaking.getWarehouseOid())))
                                .withUser(Converter.toDto(userMap.get(stocktaking.getUserOid())))
                                .build();
            });
        } else {
            result = stocktakingService.getStocktakings(pageable).map(stocktaking -> Converter.toDto(stocktaking));
        }
        return result;
    }

    @PostMapping(path = "{id}/entries", produces = MediaType.APPLICATION_JSON_VALUE)
    public String addEntry(final @PathVariable("id") String stocktakingId,
                           @RequestBody final StockTakingEntryDto entry)
    {
        return stocktakingService.addEntry(stocktakingId, entry).getId();
    }

    @GetMapping(path = "{id}/entries", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<StockTakingEntryDto> getEntries(final @PathVariable("id") String stocktakingId)
    {
        return null;
    }
}
