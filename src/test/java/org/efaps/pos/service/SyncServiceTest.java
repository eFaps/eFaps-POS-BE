/*
 * Copyright 2003 - 2018 The eFaps Team
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

import org.efaps.pos.dto.ProductDto;
import org.efaps.pos.entity.Product;
import org.efaps.pos.service.SyncService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureMockRestServiceServer
@ActiveProfiles(profiles = "test")
public class SyncServiceTest
{
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SyncService salesService;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    public void setup() {
        this.mongoTemplate.remove(new Query(), Product.class);
    }

    @Test
    public void test() {
        final Product product = new Product()
                        .setOid("1234.652")
                        .setDescription("This is a description");
        this.mongoTemplate.save(product);

        final List<Product> products = this.salesService.getProducts();
        assertEquals(1, products.size());
        assertEquals("1234.652", products.get(0).getOid());
        assertEquals("This is a description", products.get(0).getDescription());
    }

    @Test
    public void testSyncProductsFirstTime() throws JsonProcessingException {
        assertTrue(this.mongoTemplate.findAll(Product.class).isEmpty());

        final ProductDto product1 = ProductDto.builder()
                        .withDescription("A product description")
                        .withOID("5586.1651")
                        .build();

        final List<ProductDto> productDtos = Arrays.asList(product1);

        this.server.expect(requestTo("http://localhost:8888/servlet/rest/products"))
            .andRespond(withSuccess(this.mapper.writeValueAsString(productDtos), MediaType.APPLICATION_JSON));

        this.salesService.syncProducts();

        final List<Product> products = this.mongoTemplate.findAll(Product.class);
        assertEquals(1, products.size());
    }

    @Test
    public void testSyncProductsUpdate() throws JsonProcessingException {
        assertTrue(this.mongoTemplate.findAll(Product.class).isEmpty());

        final Product product = new Product()
                        .setOid("5586.1651")
                        .setDescription("Some old description");
        this.mongoTemplate.save(product);

        final ProductDto product1 = ProductDto.builder()
                        .withDescription("An updated product description")
                        .withOID("5586.1651")
                        .build();

        final List<ProductDto> productDtos = Arrays.asList(product1);

        this.server.expect(requestTo("http://localhost:8888/servlet/rest/products"))
            .andRespond(withSuccess(this.mapper.writeValueAsString(productDtos), MediaType.APPLICATION_JSON));

        this.salesService.syncProducts();

        final List<Product> products = this.mongoTemplate.findAll(Product.class);
        assertEquals(1, products.size());
        assertEquals("5586.1651", products.get(0).getOid());
        assertEquals("An updated product description", products.get(0).getDescription());
    }

    @Test
    public void testSyncProductsRemoveObsolete() throws JsonProcessingException {
        assertTrue(this.mongoTemplate.findAll(Product.class).isEmpty());

        final Product product = new Product()
                        .setOid("5586.1650")
                        .setDescription("This product should be removed");
        this.mongoTemplate.save(product);

        final ProductDto product1 = ProductDto.builder()
                        .withDescription("An updated product description")
                        .withOID("5586.1651")
                        .build();

        final List<ProductDto> productDtos = Arrays.asList(product1);

        this.server.expect(requestTo("http://localhost:8888/servlet/rest/products"))
            .andRespond(withSuccess(this.mapper.writeValueAsString(productDtos), MediaType.APPLICATION_JSON));

        this.salesService.syncProducts();

        final List<Product> products = this.mongoTemplate.findAll(Product.class);
        assertEquals(1, products.size());
        assertEquals("5586.1651", products.get(0).getOid());
        assertEquals("An updated product description", products.get(0).getDescription());
    }
}
