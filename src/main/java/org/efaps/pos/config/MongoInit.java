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

import org.efaps.pos.entity.Product;
import org.efaps.pos.entity.PromotionInfo;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.data.mongodb.core.index.TextIndexDefinition.TextIndexDefinitionBuilder;
import org.springframework.stereotype.Component;

@Component
public class MongoInit
    implements ApplicationListener<ApplicationReadyEvent>
{

    private final MongoTemplate mongoTemplate;

    public MongoInit(final MongoTemplate mongoTemplate)
    {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event)
    {
        final TextIndexDefinition textIndex = new TextIndexDefinitionBuilder()
                        .named("TextSearch")
                        .onField("description")
                        .onField("note")
                        .onField("sku")
                        .onField("barcodes.code")
                        .withDefaultLanguage("spanish")
                        .build();
        mongoTemplate.indexOps(Product.class).createIndex(textIndex);
        mongoTemplate.indexOps(PromotionInfo.class).createIndex(new Index().on("documentId", Direction.ASC));
        mongoTemplate.indexOps(PromotionInfo.class).createIndex(new Index().on("oid", Direction.ASC));
    }
}
