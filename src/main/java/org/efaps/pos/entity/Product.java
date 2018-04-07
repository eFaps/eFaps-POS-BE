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
package org.efaps.pos.entity;

import java.math.BigDecimal;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
public class Product
{

    @Id
    private String id;

    private String oid;

    private String sku;

    private String description;

    private String imageOid;

    private BigDecimal netPrice;
    private BigDecimal crossPrice;

    public String getOid()
    {
        return this.oid;
    }

    public Product setOid(final String _oid)
    {
        this.oid = _oid;
        this.id = _oid;
        return this;
    }

    public String getSKU()
    {
        return this.sku;
    }

    public Product setSKU(final String _sku)
    {
        this.sku = _sku;
        return this;
    }

    public String getDescription()
    {
        return this.description;
    }

    public Product setDescription(final String _description)
    {
        this.description = _description;
        return this;
    }

    public String getImageOid()
    {
        return this.imageOid;
    }

    public Product setImageOid(final String _imageOid)
    {
        this.imageOid = _imageOid;
        return this;
    }

    public BigDecimal getNetPrice()
    {
        return this.netPrice;
    }

    public Product setNetPrice(final BigDecimal _netPrice)
    {
        this.netPrice = _netPrice;
        return this;
    }

    public BigDecimal getCrossPrice()
    {
        return this.crossPrice;
    }

    public Product setCrossPrice(final BigDecimal _crossPrice)
    {
        this.crossPrice = _crossPrice;
        return this;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this);
    }
}
