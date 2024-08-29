package org.efaps.pos.config;

import org.efaps.pos.entity.Product;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.mongodb.core.MongoTemplate;
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
        mongoTemplate.indexOps(Product.class).ensureIndex(textIndex);
    }

}
