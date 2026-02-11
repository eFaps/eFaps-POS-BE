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
package org.efaps.pos.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.EnumUtils;
import org.efaps.abacus.api.CrossTotalFlow;
import org.efaps.abacus.api.TaxCalcFlow;
import org.efaps.abacus.pojo.Configuration;
import org.efaps.pos.config.ConfigProperties;
import org.efaps.pos.config.ConfigProperties.BEInst;
import org.efaps.pos.config.ConfigProperties.Extension;
import org.efaps.pos.entity.Config;
import org.efaps.promotionengine.PromotionsConfiguration;
import org.efaps.promotionengine.process.EngineRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;


@Service
public class ConfigService
{

    public static String CALCULATOR_CONFIG = "org.efaps.sales.Calculator.Config";
    public static String PROMOTIONS_ACTIVATE = "org.efaps.promotions.Activate";
    public static String PROMOTIONS_CONFIG = "org.efaps.promotions.Engine.Config";
    public static String FILE_ACTIVATE = "org.efaps.pos.File.Activate";
    public static String EBILL_MAP = "org.efaps.electronicbilling.TaxMapping";
    public static String LOYALTY_ACTIVATE = "org.efaps.loyalty.Activate";
    public static String UPDATE_ACTIVATE = "org.efaps.pos.Update.Activate";


    private static final Logger LOG = LoggerFactory.getLogger(ConfigService.class);

    private final MongoTemplate mongoTemplate;
    private final ConfigProperties configProperties;
    private final ObjectMapper objectMapper;

    public ConfigService(final MongoTemplate mongoTemplate,
                         final ConfigProperties configProperties)
    {
        this.mongoTemplate = mongoTemplate;
        this.configProperties = configProperties;
        objectMapper = new ObjectMapper();
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
            final Map<String, String> map = toMap(conf);
            map.entrySet().forEach(entry -> {
                switch (entry.getKey()) {
                    case "NetPriceScale" -> config.setNetPriceScale(Integer.valueOf(entry.getValue()));
                    case "TaxScale" -> config.setTaxScale(Integer.valueOf(entry.getValue()));
                    case "CrossPriceScale" -> config.setCrossPriceScale(Integer.valueOf(entry.getValue()));
                    case "TaxCalcFlow" -> config.setTaxCalcFlow(EnumUtils.getEnum(TaxCalcFlow.class, entry.getValue()));
                    case "CrossTotalFlow" -> config.setCrossTotalFlow(EnumUtils.getEnum(CrossTotalFlow.class, entry.getValue()));
                    default -> throw new IllegalArgumentException("Unexpected value: " + entry.getKey());
                }

            });
        }
        return config;
    }

    public PromotionsConfiguration getPromotionsConfig()
    {
        final var conf = getSystemConfig(PROMOTIONS_CONFIG);
        final var config = new PromotionsConfiguration().setEngineRule(EngineRule.PRIORITY);
        if (conf != null) {
            final Map<String, String> map = toMap(conf);
            map.entrySet().forEach(entry -> {
                switch (entry.getKey()) {
                    case "EngineRule" -> config.setEngineRule(EnumUtils.getEnum(EngineRule.class, entry.getValue()));
                    default -> throw new IllegalArgumentException("Unexpected value: " + entry.getKey());
                }

            });
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

    public BEInst getInstProperties()
    {
        return configProperties.getBeInst();
    }

    public Map<String, String> getTaxMapping()
    {
        return toMap(getSystemConfig(EBILL_MAP));
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> toMap(final String content)
    {
        try {
            return objectMapper.readValue(content, HashMap.class);
        } catch (final JacksonException e) {
           LOG.error("Catched", e);
        }
        return null;
    }
}
