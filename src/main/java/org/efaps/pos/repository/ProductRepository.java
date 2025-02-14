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
    @Query("""
        { '$or' : [\
        { 'description' : { '$regularExpression' : { 'pattern' : '?0', 'options' : 'i'}}}, \
        { 'sku' : { '$regularExpression' : { 'pattern' : '?0', 'options' : 'i'}}},\
        { 'barcodes.code' : '?0'}\
        ]} """)
    Page<Product> find(String term, Pageable pageable);

    @Query(value = """
        { \
        $text : { $search: '?0' }\
        }""", sort=" { score: { $meta: 'textScore' }}")
    Page<Product> findText(String term, Pageable pageable);

    @Query("{'categories.categoryOid':'?0'}")
    List<Product> findByCategoryOid(String oid);

    @Query("{'barcodes.code':'?0'}")
    List<Product> findByBarcode(String barcode);

    List<Product> findByType(ProductType type);

    List<Product> findBySku(String sku);

    @Query("{'configurationBOMs.oid':'?0'}")
    List<Product> findByBomOid(String bomOid);
}
