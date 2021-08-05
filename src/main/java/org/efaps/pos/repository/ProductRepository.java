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
package org.efaps.pos.repository;

import java.util.List;

import org.efaps.pos.dto.ProductType;
import org.efaps.pos.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ProductRepository
    extends MongoRepository<Product, String>
{
    Page<Product> findByDescriptionLikeOrSkuLikeAllIgnoreCase(String _term1, String _term2, Pageable pageable);

    List<Product> findByCategoryOidsContains(String _oid);

    @Query("{'barcodes.code':'?0'}")
    List<Product> findByBarcode(String _barcode);

    List<Product> findByType( ProductType type);
}
