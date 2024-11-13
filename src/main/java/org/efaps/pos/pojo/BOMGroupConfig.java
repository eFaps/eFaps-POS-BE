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
package org.efaps.pos.pojo;

import java.math.BigDecimal;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class BOMGroupConfig
{

    private String oid;
    private String productOid;
    private String name;
    private String description;
    private int weight;
    private int flags;
    private BigDecimal minQuantity;
    private BigDecimal maxQuantity;

    public String getOid()
    {
        return oid;
    }

    public BOMGroupConfig setOid(String oid)
    {
        this.oid = oid;
        return this;
    }

    public String getProductOid()
    {
        return productOid;
    }

    public BOMGroupConfig setProductOid(String productOid)
    {
        this.productOid = productOid;
        return this;
    }

    public String getName()
    {
        return name;
    }

    public BOMGroupConfig setName(String name)
    {
        this.name = name;
        return this;
    }

    public String getDescription()
    {
        return description;
    }

    public BOMGroupConfig setDescription(String description)
    {
        this.description = description;
        return this;
    }

    public int getWeight()
    {
        return weight;
    }

    public BOMGroupConfig setWeight(int weight)
    {
        this.weight = weight;
        return this;
    }

    public int getFlags()
    {
        return flags;
    }

    public BOMGroupConfig setFlags(int flags)
    {
        this.flags = flags;
        return this;
    }

    public BigDecimal getMinQuantity()
    {
        return minQuantity;
    }

    public BOMGroupConfig setMinQuantity(final BigDecimal minQuantity)
    {
        this.minQuantity = minQuantity;
        return this;
    }

    public BigDecimal getMaxQuantity()
    {
        return maxQuantity;
    }

    public BOMGroupConfig setMaxQuantity(final BigDecimal maxQuantity)
    {
        this.maxQuantity = maxQuantity;
        return this;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this);
    }
}
