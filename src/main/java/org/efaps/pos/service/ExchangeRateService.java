/*
 * Copyright 2003 - 2021 The eFaps Team
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

import java.math.BigDecimal;
import java.util.HashMap;

import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.dto.Currency;
import org.efaps.pos.entity.ExchangeRates;
import org.efaps.pos.entity.StashId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class ExchangeRateService
{

    private static final Logger LOG = LoggerFactory.getLogger(ExchangeRateService.class);
    private final MongoTemplate mongoTemplate;
    private final EFapsClient eFapsClient;

    public ExchangeRateService(final MongoTemplate mongoTemplate,
                               final EFapsClient eFapsClient)
    {
        this.mongoTemplate = mongoTemplate;
        this.eFapsClient = eFapsClient;
    }

    public BigDecimal getExchangeRate(final Currency currency)
    {
        var exchangeRates = mongoTemplate.findById(StashId.EXCHANGERATES.getKey(), ExchangeRates.class);
        if (exchangeRates == null) {
            LOG.warn("No ExchangeRates found in database");
            exchangeRates = new ExchangeRates();
            exchangeRates.setRates(new HashMap<>());
        }
        final var rates = exchangeRates.getRates();
        BigDecimal ret;
        if (rates != null && rates.containsKey(currency)) {
            ret = rates.get(currency);
        } else {
            ret = BigDecimal.ONE;
        }
        return ret;
    }

    public void loadExchangeRate() {
        final var rates = eFapsClient.getExchangeRates();
        var exchangeRates = mongoTemplate.findById(StashId.EXCHANGERATES.getKey(), ExchangeRates.class);
        if (exchangeRates == null) {
            LOG.warn("No ExchangeRates found in database");
            exchangeRates = new ExchangeRates();
            exchangeRates.setId(StashId.EXCHANGERATES.getKey());
            exchangeRates.setRates(new HashMap<>());
        }
        final var exchangeRates2 = exchangeRates;
        rates.forEach(rateDto -> {
            exchangeRates2.getRates().put(rateDto.getCurrency(), rateDto.getExchangeRate());
        });
        mongoTemplate.save(exchangeRates2);
    }

}
