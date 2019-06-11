/*
 * Copyright 2003 - 2019 The eFaps Team
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
import org.efaps.pos.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService
{
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(final CategoryRepository _categoryRepository)
    {
        this.categoryRepository = _categoryRepository;
    }

    public List<Category> getCategories()
    {
        final List<Category> ret = this.categoryRepository.findAll();
        return ret;
    }

    public Category getCategory(final String _oid)
    {
        return this.categoryRepository.findById(_oid).get();
    }
}
