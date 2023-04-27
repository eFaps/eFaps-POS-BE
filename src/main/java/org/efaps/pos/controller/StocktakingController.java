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

import org.efaps.pos.config.IApi;
import org.efaps.pos.dto.PosStocktakingDto;
import org.efaps.pos.entity.User;
import org.efaps.pos.service.StocktakingService;
import org.efaps.pos.util.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(IApi.BASEPATH + "stocktaking")
public class StocktakingController
{

    private final StocktakingService stocktakingService;

    public StocktakingController(final StocktakingService stocktakingService)
    {
        this.stocktakingService = stocktakingService;
    }

    @GetMapping(path = "current", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PosStocktakingDto> getCurrentStocktaking(final Authentication _authentication)
    {
        final var stocktakingOpt = stocktakingService.getCurrent((User) _authentication.getPrincipal());

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
    public Page<PosStocktakingDto> getStocktakings(final Pageable pageable)
    {
        return stocktakingService.getStocktakings(pageable).map(stocktaking -> Converter.toDto(stocktaking));
    }
}
