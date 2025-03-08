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

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import org.bson.BsonRegularExpression;
import org.bson.Document;
import org.efaps.pos.context.MultiTenantMongoDbFactory;
import org.efaps.pos.dto.PromoDetailDto;
import org.efaps.pos.dto.PromoInfoDto;
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
        converterConfigurationAdapter.registerConverter(new PromoInfoDtoReadConverter());
        converterConfigurationAdapter.registerConverter(new OffsetTimeWriteConverter());
        converterConfigurationAdapter.registerConverter(new OffsetTimeReadConverter());
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

    public static class OffsetTimeWriteConverter
        implements Converter<OffsetTime, String>
    {

        @Override
        public String convert(OffsetTime source)
        {
            return source.toString();
        }
    }

    public static class OffsetTimeReadConverter
        implements Converter<String, OffsetTime>
    {

        @Override
        public OffsetTime convert(String source)
        {
            return OffsetTime.parse(source);
        }
    }

    public static class PromoInfoDtoReadConverter
        implements Converter<Document, PromoInfoDto>
    {

        @Override
        public PromoInfoDto convert(Document source)
        {
            final List<PromoDetailDto> details = new ArrayList<>();
            for (final var detailDoc : source.getList("details", Document.class)) {
                details.add(PromoDetailDto.builder()
                                .withPositionIndex(detailDoc.getInteger("positionIndex"))
                                .withNetUnitDiscount(detailDoc.getString("netUnitDiscount") == null ? null
                                                : new BigDecimal(detailDoc.getString("netUnitDiscount")))
                                .withNetDiscount(detailDoc.getString("netDiscount") == null ? null
                                                : new BigDecimal(detailDoc.getString("netDiscount")))
                                .withCrossUnitDiscount(detailDoc.getString("crossUnitDiscount") == null ? null
                                                : new BigDecimal(detailDoc.getString("crossUnitDiscount")))
                                .withCrossDiscount(detailDoc.getString("crossDiscount") == null ? null
                                                : new BigDecimal(detailDoc.getString("crossDiscount")))
                                .withPromotionOid(detailDoc.getString("promotionOid"))
                                .build());

            }
            return PromoInfoDto.builder()
                            .withNetTotalDiscount(source.getString("netTotalDiscount") == null ? null
                                            : new BigDecimal(source.getString("netTotalDiscount")))
                            .withCrossTotalDiscount(source.getString("crossTotalDiscount") == null ? null
                                            : new BigDecimal(source.getString("crossTotalDiscount")))
                            .withPromotionOids(new HashSet<>(source.getList("promotionOids", String.class)))
                            .withDetails(details)
                            .build();
        }
    }
}
