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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.efaps.pos.entity.Category;
import org.efaps.pos.util.CategoryNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
public class CategoryServiceTest
{

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CategoryService categoryService;

    @BeforeEach
    public void setup()
    {
        mongoTemplate.remove(new Query(), Category.class);
    }

    @Test
    public void testGetCategories()
    {
        mongoTemplate.save(new Category().setOid("1.1"));
        mongoTemplate.save(new Category().setOid("1.2"));
        mongoTemplate.save(new Category().setOid("1.3"));

        final List<Category> categories = categoryService.getCategories();
        assertEquals(3, categories.size());
    }

    @Test
    public void testGetCategory()
        throws CategoryNotFoundException
    {
        mongoTemplate.save(new Category().setOid("1.1"));
        mongoTemplate.save(new Category().setOid("1.2"));
        mongoTemplate.save(new Category().setOid("1.3"));

        final Category category = categoryService.getCategory("1.3");
        assertEquals("1.3", category.getOid());
    }

    @Test
    public void testGetCategoryThrowsError()
    {
        assertThrows(CategoryNotFoundException.class, () -> {
            categoryService.getCategory("notanOID");
        });
    }
}
