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
import org.efaps.pos.dto.BOMActionType;

public class BOMAction
{

    private BOMActionType type;
    private BigDecimal netAmount;
    private BigDecimal crossAmount;

    public BOMActionType getType()
    {
        return type;
    }

    public BOMAction setType(BOMActionType type)
    {
        this.type = type;
        return this;
    }

    public BigDecimal getNetAmount()
    {
        return netAmount;
    }

    public BOMAction setNetAmount(BigDecimal netAmount)
    {
        this.netAmount = netAmount;
        return this;
    }

    public BigDecimal getCrossAmount()
    {
        return crossAmount;
    }

    public BOMAction setCrossAmount(BigDecimal crossAmount)
    {
        this.crossAmount = crossAmount;
        return this;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this);
    }
}
