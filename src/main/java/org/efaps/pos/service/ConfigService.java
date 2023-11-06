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
package org.efaps.pos.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.EnumUtils;
import org.efaps.abacus.api.CrossTotalFlow;
import org.efaps.abacus.api.TaxCalcFlow;
import org.efaps.abacus.pojo.Configuration;
import org.efaps.pos.ConfigProperties;
import org.efaps.pos.ConfigProperties.Extension;
import org.efaps.pos.entity.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ConfigService
{
    public static String CALCULATOR_CONFIG = "org.efaps.sales.Calculator.Config";

    private static final Logger LOG = LoggerFactory.getLogger(ConfigService.class);

    private final MongoTemplate mongoTemplate;
    private final ConfigProperties configProperties;

    public ConfigService(final MongoTemplate mongoTemplate,
                         final ConfigProperties configProperties)
    {
        this.mongoTemplate = mongoTemplate;
        this.configProperties = configProperties;

    }

    public String getSystemConfig(final String key)
    {
        final Config config = mongoTemplate.findById(Config.KEY, Config.class);
        return config.getProperties().get(key);
    }

    public List<Extension> getExtensions()
    {
        return configProperties.getExtensions();
    }

    public Configuration getCalculatorConfig()
    {
        final var config = new Configuration();
        final var conf = getSystemConfig(CALCULATOR_CONFIG);
        if (conf != null) {
            final var objectMapper = new ObjectMapper();
            try {
                @SuppressWarnings("unchecked")
                final Map<String, String> map = objectMapper.readValue(conf, HashMap.class);
                map.entrySet().forEach(entry -> {
                    switch (entry.getKey()) {
                        case "NetPriceScale":
                            config.setNetPriceScale(Integer.valueOf(entry.getValue()));
                            break;
                        case "TaxScale":
                            config.setTaxScale(Integer.valueOf(entry.getValue()));
                            break;
                        case "CrossPriceScale":
                            config.setCrossPriceScale(Integer.valueOf(entry.getValue()));
                            break;
                        case "TaxCalcFlow":
                            config.setTaxCalcFlow(EnumUtils.getEnum(TaxCalcFlow.class, entry.getValue()));
                            break;
                        case "CrossTotalFlow":
                            config.setCrossTotalFlow(EnumUtils.getEnum(CrossTotalFlow.class, entry.getValue()));
                            break;
                        default:
                            throw new IllegalArgumentException("Unexpected value: " + entry.getKey());
                    }
                });
            } catch (final JsonProcessingException e) {
                LOG.error("Catched", e);
            }
        }
        return config;
    }

    public String getOrDefault(final String key,
                               final String defaultValue)
    {
        final Config config = mongoTemplate.findById(Config.KEY, Config.class);
        return config.getProperties().getOrDefault(key, defaultValue);
    }

    public Map<String, String> getProperties()
    {
        return mongoTemplate.findById(Config.KEY, Config.class).getProperties();
    }
}
