/*
 * Copyright 2003 - 2019 The eFaps Team
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

import java.util.Optional;

import org.efaps.pos.config.IApi;
import org.efaps.pos.dto.BalanceDto;
import org.efaps.pos.dto.BalanceSummaryDto;
import org.efaps.pos.entity.Balance;
import org.efaps.pos.entity.User;
import org.efaps.pos.service.BalanceService;
import org.efaps.pos.util.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(IApi.BASEPATH + "balance")
public class BalanceController
{

    private final BalanceService balanceService;

    public BalanceController(final BalanceService _balanceService)
    {
        balanceService = _balanceService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BalanceDto> getCurrentBalance(final Authentication _authentication,
                                @RequestParam(name = "createNew", required = false) final boolean _createNew)
    {
        final Optional<Balance> balanceOpt = balanceService.getCurrent((User) _authentication.getPrincipal(),
                        _createNew);
        ResponseEntity<BalanceDto> ret;
        if (balanceOpt.isPresent()) {
            ret = new ResponseEntity<>(Converter.toDto(balanceOpt.get()), HttpStatus.OK);
        } else {
            ret = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ret;
    }

    @PutMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public BalanceDto updateBalance(@PathVariable("id") final String _id,
                                   @RequestBody final BalanceDto _balanceDto)
    {
        return Converter.toDto(balanceService.update(Converter.toEntity(_balanceDto)));
    }

    @GetMapping(path = "{id}/summary", produces = MediaType.APPLICATION_JSON_VALUE)
    public BalanceSummaryDto getSummary(@PathVariable("id") final String _balanceId) {
        return balanceService.getSummary(_balanceId);
    }

}
