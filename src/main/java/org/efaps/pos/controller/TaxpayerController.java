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
package org.efaps.pos.controller;

import org.efaps.pos.config.IApi;
import org.efaps.pos.dto.TaxpayerDto;
import org.efaps.pos.service.TaxpayerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(IApi.BASEPATH + "taxpayer")
public class TaxpayerController
{

    private final TaxpayerService taxpayerService;

    public TaxpayerController(final TaxpayerService _taxpayerService)
    {
        taxpayerService = _taxpayerService;
    }

    @GetMapping(path = "query", produces = MediaType.APPLICATION_JSON_VALUE, params = { "id" })
    public TaxpayerDto get(@RequestParam(name = "id") final String _id)
    {
        return taxpayerService.get(_id);
    }

    @GetMapping(path = "query", produces = MediaType.APPLICATION_JSON_VALUE, params = { "term" })
    public Page<TaxpayerDto> find(final Pageable _pageable, @RequestParam(name = "term") final String _term)
    {
        return taxpayerService.find(_pageable, _term);
    }
}
