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

import org.efaps.pos.entity.Product;
import org.efaps.pos.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService
{

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(final ProductRepository _productRepository)
    {
        productRepository = _productRepository;
    }

    public List<Product> getProducts()
    {
        return productRepository.findAll();
    }

    public Product getProduct(final String _oid)
    {
        return _oid == null ? null : productRepository.findById(_oid).orElse(null);
    }

    public List<Product> findProducts(final String _term)
    {
        return productRepository.findByDescriptionLikeIgnoreCase(_term);
    }

    public List<Product> findProductsByCategory(final String _categoryOid)
    {
        return productRepository.findByCategoryOidsContains(_categoryOid);
    }

    public List<Product> findProductsByBarcode(final String _barcode)
    {
        return productRepository.findByBarcodesContains(_barcode);
    }
}
