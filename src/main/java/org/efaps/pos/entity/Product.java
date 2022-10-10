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
package org.efaps.pos.entity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.efaps.pos.dto.Currency;
import org.efaps.pos.dto.ProductType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
public class Product
{

    @Id
    private String id;
    private String oid;
    private String sku;
    private ProductType type;
    private String description;
    private String note;
    private String imageOid;
    private BigDecimal netPrice;
    private BigDecimal crossPrice;
    private Currency currency;
    private Set<Product2Category> categories;
    private Set<Tax> taxes;
    private String uoM;
    private String uoMCode;
    private Set<ProductRelation> relations;
    private Set<IndicationSet> indicationSets;
    private Set<Barcode> barcodes;

    public String getOid()
    {
        return oid;
    }

    public Product setOid(final String _oid)
    {
        oid = _oid;
        id = _oid;
        return this;
    }

    public String getSKU()
    {
        return sku;
    }

    public Product setSKU(final String _sku)
    {
        sku = _sku;
        return this;
    }

    public ProductType getType()
    {
        return type;
    }

    public Product setType(final ProductType _type)
    {
        type = _type;
        return this;
    }

    public String getDescription()
    {
        return description;
    }

    public Product setDescription(final String _description)
    {
        description = _description;
        return this;
    }

    public String getNote()
    {
        return note;
    }

    public Product setNote(final String note)
    {
        this.note = note;
        return this;
    }

    public String getImageOid()
    {
        return imageOid;
    }

    public Product setImageOid(final String _imageOid)
    {
        imageOid = _imageOid;
        return this;
    }

    public BigDecimal getNetPrice()
    {
        return netPrice;
    }

    public Product setNetPrice(final BigDecimal _netPrice)
    {
        netPrice = _netPrice;
        return this;
    }

    public BigDecimal getCrossPrice()
    {
        return crossPrice;
    }

    public Product setCrossPrice(final BigDecimal _crossPrice)
    {
        crossPrice = _crossPrice;
        return this;
    }

    public Currency getCurrency()
    {
        return currency;
    }

    public Product setCurrency(final Currency currency)
    {
        this.currency = currency;
        return this;
    }

    public Set<Product2Category> getCategories()
    {
        return categories == null ? Collections.emptySet() : categories;
    }

    public Product setCategories(final Set<Product2Category> _categories)
    {
        categories = _categories;
        return this;
    }

    public Set<Tax> getTaxes()
    {
        return taxes;
    }

    public Product setTaxes(final Set<Tax> _taxes)
    {
        taxes = _taxes;
        return this;
    }

    public String getUoM()
    {
        return uoM;
    }

    public Product setUoM(final String _uoM)
    {
        uoM = _uoM;
        return this;
    }

    public String getUoMCode()
    {
        return uoMCode;
    }

    public Product setUoMCode(final String _uoMCode)
    {
        uoMCode = _uoMCode;
        return this;
    }

    public Set<ProductRelation> getRelations()
    {
        return relations;
    }

    public Product setRelations(final Set<ProductRelation> _relations)
    {
        relations = _relations;
        return this;
    }

    public Set<IndicationSet> getIndicationSets()
    {
        return indicationSets;
    }

    public Product setIndicationSets(final Set<IndicationSet> _indicationSets)
    {
        indicationSets = _indicationSets;
        return this;
    }

    public Set<Barcode> getBarcodes()
    {
        return barcodes;
    }

    public Product setBarcodes(final Set<Barcode> barcodes)
    {
        this.barcodes = barcodes;
        return this;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this);
    }

}
