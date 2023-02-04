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

import java.util.List;

import org.efaps.pos.ConfigProperties;
import org.efaps.pos.ConfigProperties.Extension;
import org.efaps.pos.config.IApi;
import org.efaps.pos.entity.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(IApi.BASEPATH + "config")
public class ConfigController
{
    private final MongoTemplate mongoTemplate;
    private final ConfigProperties configProperties;

    @Autowired
    public ConfigController(final MongoTemplate _mongoTemplate, final ConfigProperties _configProperties)
    {
        mongoTemplate = _mongoTemplate;
        configProperties = _configProperties;
    }

    @GetMapping(produces = { MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE}, path = "/system/{key}")
    public String getSystemConfig(@PathVariable(name = "key") final String _key)
    {
        final Config config = mongoTemplate.findById(Config.KEY, Config.class);
        return config.getProperties().get(_key);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/extensions")
    public List<Extension> getExtensions()
    {
        return configProperties.getExtensions();
    }
}
