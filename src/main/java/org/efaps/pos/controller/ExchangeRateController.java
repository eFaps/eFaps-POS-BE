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
package org.efaps.pos.controller;

import org.efaps.pos.config.IApi;
import org.efaps.pos.dto.Currency;
import org.efaps.pos.dto.ExchangeRateDto;
import org.efaps.pos.service.ExchangeRateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(IApi.BASEPATH + "exchange-rate")
public class ExchangeRateController
{

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(final ExchangeRateService _exchangeRateService)
    {
        exchangeRateService = _exchangeRateService;
    }

    @GetMapping(path = "/{currency}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExchangeRateDto> getExchangeRate(final Authentication _authentication,
                                                           @PathVariable("currency") final Currency currency)
    {
        final var exchangeRate = exchangeRateService.getExchangeRate(currency);
        return new ResponseEntity<>(
                        ExchangeRateDto.builder().withCurrency(currency).withExchangeRate(exchangeRate).build(),
                        HttpStatus.OK);

    }
}
