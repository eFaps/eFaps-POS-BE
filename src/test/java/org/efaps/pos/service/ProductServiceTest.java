/*
 * Copyright 2003 - 2022 The eFaps Team
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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.efaps.pos.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureMockRestServiceServer
@ActiveProfiles(profiles = "test")
@AutoConfigureDataMongo
public class ProductServiceTest
{
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ProductService productcService;

    @BeforeEach
    public void setup() {
        mongoTemplate.remove(new Query(), Product.class);
    }

    @Test
    public void test() {
        final Product product = new Product()
                        .setOid("1234.652")
                        .setDescription("This is a description");
        mongoTemplate.save(product);

        final Page<Product> products = productcService.getProducts(PageRequest.of(0, 20));
        assertEquals(1, products.getContent().size());
        assertEquals("1234.652", products.getContent().get(0).getOid());
        assertEquals("This is a description", products.getContent().get(0).getDescription());
    }
}
