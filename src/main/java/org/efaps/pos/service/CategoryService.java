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
package org.efaps.pos.service;

import java.util.List;
import java.util.stream.Collectors;

import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.entity.Category;
import org.efaps.pos.repository.CategoryRepository;
import org.efaps.pos.util.CategoryNotFoundException;
import org.efaps.pos.util.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

@Service
public class CategoryService
{

    private static final Logger LOG = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;
    private final EFapsClient eFapsClient;

    @Autowired
    public CategoryService(final CategoryRepository categoryRepository,
                           final EFapsClient eFapsClient)
    {
        this.categoryRepository = categoryRepository;
        this.eFapsClient = eFapsClient;
    }

    public List<Category> getCategories()
    {
        final List<Category> ret = categoryRepository.findAll(Sort.by(Order.by("weight")));
        return ret;
    }

    public Category getCategory(final String _oid)
        throws CategoryNotFoundException
    {
        return categoryRepository.findById(_oid).orElseThrow(CategoryNotFoundException::new);
    }

    public void sync()
    {
        LOG.info("Syncing Categories");
        final List<Category> categories = eFapsClient.getCategories().stream()
                        .map(Converter::toEntity)
                        .collect(Collectors.toList());
        if (!categories.isEmpty()) {
            final var catOids  =categories.stream().map(Category::getOid).collect(Collectors.toSet());
            categoryRepository.findAll().forEach(existing -> {
                if (!catOids.contains(existing.getOid())) {
                    categoryRepository.delete(existing);
                }
            });
            for (final var category : categories) {
                categoryRepository.save(category);
            }
        }
    }
}
