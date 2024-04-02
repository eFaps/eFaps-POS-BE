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
package org.efaps.pos.config;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.regex.Pattern;

import org.bson.BsonRegularExpression;
import org.efaps.pos.ConfigProperties;
import org.efaps.pos.context.MultiTenantMongoDbFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions.MongoConverterConfigurationAdapter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
@Profile({ "!test & !embedded" })
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
    public MappingMongoConverter mappingMongoConverter(final MongoDatabaseFactory _databaseFactory,
                                                       final MongoCustomConversions _customConversions,
                                                       final MongoMappingContext _mappingContext)
    {
        final MappingMongoConverter ret = super.mappingMongoConverter(_databaseFactory, _customConversions,
                        _mappingContext);
        ret.setMapKeyDotReplacement("_xYz4P_");
        return ret;
    }

    @Override
    @Bean
    @Primary
    public MongoDatabaseFactory mongoDbFactory()
    {
        return new MultiTenantMongoDbFactory(configProperties.getBeInst().getMongoClientURI());
    }

    @Bean
    @Override
    @Primary
    public MongoClient mongoClient()
    {
        return MongoClients.create(configProperties.getBeInst().getMongoClientURI());
    }

    @Bean
    public GridFsTemplate gridFsTemplate()
        throws ClassNotFoundException
    {
        return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter(mongoDbFactory(), customConversions(),
                        mongoMappingContext(customConversions(), mongoManagedTypes())));
    }

    @Override
    protected String getDatabaseName()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void configureConverters(final MongoConverterConfigurationAdapter converterConfigurationAdapter)
    {

        converterConfigurationAdapter.registerConverter(new RegexConverter());
        converterConfigurationAdapter.registerConverter(new OffsetDateTimeWriteConverter());
        converterConfigurationAdapter.registerConverter(new OffsetDateTimeReadConverter());
    }

    public static class RegexConverter
        implements Converter<BsonRegularExpression, Pattern>
    {

        @Override
        public Pattern convert(final BsonRegularExpression source)
        {
            return Pattern.compile(source.getPattern());
        }
    }

    public static class OffsetDateTimeWriteConverter
        implements Converter<OffsetDateTime, Date>
    {

        @Override
        public Date convert(OffsetDateTime source)
        {
            return Date.from(source.toInstant());
        }
    }

    public static class OffsetDateTimeReadConverter
        implements Converter<Date, OffsetDateTime>
    {

        @Override
        public OffsetDateTime convert(Date source)
        {
            return source.toInstant().atOffset(ZoneOffset.UTC);
        }
    }

}
