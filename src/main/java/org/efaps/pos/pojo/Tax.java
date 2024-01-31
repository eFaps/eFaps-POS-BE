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
import org.apache.commons.lang3.builder.ToStringStyle;
import org.efaps.pos.dto.TaxType;

public class Tax
{

    private String oid;

    private String key;

    private String catKey;

    private String name;

    private TaxType type;

    private BigDecimal percent;

    private BigDecimal amount;

    public String getOid()
    {
        return oid;
    }

    public Tax setOid(final String _oid)
    {
        oid = _oid;
        return this;
    }

    public String getKey()
    {
        return key;
    }

    public Tax setKey(final String _key)
    {
        key = _key;
        return this;
    }

    public String getCatKey()
    {
        return catKey;
    }

    public Tax setCatKey(final String catKey)
    {
        this.catKey = catKey;
        return this;
    }

    public TaxType getType()
    {
        return type;
    }

    public Tax setType(final TaxType _type)
    {
        type = _type;
        return this;
    }

    public String getName()
    {
        return name;
    }

    public Tax setName(final String _name)
    {
        name = _name;
        return this;
    }

    public BigDecimal getPercent()
    {
        return percent;
    }

    public Tax setPercent(final BigDecimal _percent)
    {
        percent = _percent;
        return this;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public Tax setAmount(final BigDecimal _amount)
    {
        amount = _amount;
        return this;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
