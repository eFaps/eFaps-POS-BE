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

import java.util.List;

import org.efaps.pos.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class CategoryService
{
    private final MongoTemplate mongoTemplate;

    @Autowired
    public CategoryService(final MongoTemplate _mongoTemplate)
    {
        this.mongoTemplate = _mongoTemplate;
    }

    public List<Category> getCategories()
    {
        final List<Category> ret = this.mongoTemplate.findAll(Category.class);
        return ret;
    }

    public Category getCategory(final String _oid)
    {
        return this.mongoTemplate.findById(_oid, Category.class);
    }
}
