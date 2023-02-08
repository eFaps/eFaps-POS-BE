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
package org.efaps.pos.pojo;

import java.math.BigDecimal;

import org.efaps.pos.dto.DiscountType;

public class Discount
{

    private DiscountType type;
    private BigDecimal value;
    private String productOid;
    private String label;

    public DiscountType getType()
    {
        return type;
    }

    public Discount setType(final DiscountType type)
    {
        this.type = type;
        return this;
    }

    public BigDecimal getValue()
    {
        return value;
    }

    public Discount setValue(final BigDecimal value)
    {
        this.value = value;
        return this;
    }

    public String getProductOid()
    {
        return productOid;
    }

    public Discount setProductOid(final String productOid)
    {
        this.productOid = productOid;
        return this;
    }

    public String getLabel()
    {
        return label;
    }

    public Discount setLabel(final String label)
    {
        this.label = label;
        return this;
    }
}
