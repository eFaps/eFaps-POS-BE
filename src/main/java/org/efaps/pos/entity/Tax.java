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

public class Tax
{

    private String oid;

    private String name;

    private BigDecimal percent;

    public String getOid()
    {
        return this.oid;
    }

    public Tax setOid(final String _oid)
    {
        this.oid = _oid;
        return this;
    }

    public String getName()
    {
        return this.name;
    }

    public Tax setName(final String _name)
    {
        this.name = _name;
        return this;
    }

    public BigDecimal getPercent()
    {
        return this.percent;
    }

    public Tax setPercent(final BigDecimal _percent)
    {
        this.percent = _percent;
        return this;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this);
    }
}
