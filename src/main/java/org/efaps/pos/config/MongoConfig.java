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
package org.efaps.pos.config;

import org.efaps.pos.ConfigProperties;
import org.efaps.pos.context.MultiTenantMongoDbFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
@Profile({ "!test" })
public class MongoConfig
    extends AbstractMongoClientConfiguration
{

    private final ConfigProperties configProperties;

    public MongoConfig(final ConfigProperties _configProperties)
    {
        configProperties = _configProperties;
    }

    @Override
    @Bean
    public MappingMongoConverter mappingMongoConverter()
        throws Exception
    {
        final MappingMongoConverter ret = super.mappingMongoConverter();
        ret.setMapKeyDotReplacement("_xYz4P_");
        return ret;
    }

    @Override
    @Bean
    @Primary
    public MongoDbFactory mongoDbFactory()
    {
        final MongoClientURI mongoClientURI = new MongoClientURI(configProperties.getMongoClientURI());
        return new MultiTenantMongoDbFactory(mongoClientURI);
    }

    @Override
    @Bean
    @Primary
    public MongoTemplate mongoTemplate()
        throws Exception
    {
        return new MongoTemplate(mongoDbFactory(), mappingMongoConverter());
    }

    @Bean
    @Override
    public MongoClient mongoClient()
    {
        return MongoClients.create(configProperties.getMongoClientURI());
    }

    @Override
    protected String getDatabaseName()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
