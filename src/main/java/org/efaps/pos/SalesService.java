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

package org.efaps.pos;

import java.util.List;
import java.util.stream.Collectors;

import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.entity.Product;
import org.efaps.pos.util.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
@Service
public class SalesService
{
    private final MongoTemplate mongoTemplate;
    private final EFapsClient eFapsClient;
    @Autowired
    public SalesService(final MongoTemplate _mongoTemplate, final EFapsClient _eFapsClient) {
        this.mongoTemplate = _mongoTemplate;
        this.eFapsClient = _eFapsClient;
    }

    public List<Product> getProducts() {
        final List<Product> ret = this.mongoTemplate.findAll(Product.class);
        return ret;
    }

    public void syncProducts() {
        final List<Product> products = this.eFapsClient.getProducts().stream()
                        .map(dto -> Converter.fromDto(dto))
                        .collect(Collectors.toList());
        final List<Product> existingProducts = this.mongoTemplate.findAll(Product.class);
        existingProducts.forEach(existing -> {
            if (!products.stream()
                .filter(product -> product.getOid().equals(existing.getOid()))
                .findFirst().isPresent()) {
                this.mongoTemplate.remove(existing);
            }
        });
        products.forEach(product -> this.mongoTemplate.save(product));
    }
}
