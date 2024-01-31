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

import org.efaps.pos.ConfigProperties;
import org.efaps.pos.dto.ProductType;
import org.efaps.pos.entity.Product;
import org.efaps.pos.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.data.mongodb.core.index.TextIndexDefinition.TextIndexDefinitionBuilder;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class ProductService
{
    private final ConfigProperties configProperties;
    private final MongoTemplate mongoTemplate;
    private final ProductRepository productRepository;


    @Autowired
    public ProductService(final ConfigProperties _configProperties,
                          final MongoTemplate mongoTemplate,
                          final ProductRepository _productRepository)
    {
        configProperties = _configProperties;
        this.mongoTemplate = mongoTemplate;
        productRepository = _productRepository;
    }

    @PostConstruct
    public void init() {
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

    public Page<Product> getProducts(Pageable pageable)
    {
        return productRepository.findAll(pageable);
    }

    public Product getProduct(final String _oid)
    {
        return _oid == null ? null : productRepository.findById(_oid).orElse(null);
    }

    public List<Product> findProducts(final String _term,
                                      boolean textSearch)
    {
        return textSearch ? productRepository.findText(_term, PageRequest.of(0, configProperties.getMaxSearchResult()))
                        .toList()
                        : productRepository.find(_term, PageRequest.of(0, configProperties.getMaxSearchResult()))
                                        .toList();
    }

    public List<Product> findProductsByCategory(final String _categoryOid)
    {
        return productRepository.findByCategoryOid(_categoryOid);
    }

    public List<Product> findProductsByBarcode(final String _barcode)
    {
        return productRepository.findByBarcode(_barcode);
    }

    public List<Product> findProductsByType(final ProductType _type)
    {
        return productRepository.findByType(_type);
    }
}
