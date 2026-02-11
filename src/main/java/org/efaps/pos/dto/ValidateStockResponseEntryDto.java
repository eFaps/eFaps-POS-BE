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
package org.efaps.pos.dto;

import java.math.BigDecimal;

import org.apache.commons.lang3.builder.ToStringBuilder;

import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ValidateStockResponseEntryDto.Builder.class)
public class ValidateStockResponseEntryDto
{

    private final String productOid;

    private final BigDecimal quantity;

    private ValidateStockResponseEntryDto(Builder builder)
    {
        this.productOid = builder.productOid;
        this.quantity = builder.quantity;
    }

    public String getProductOid()
    {
        return productOid;
    }

    public BigDecimal getQuantity()
    {
        return quantity;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private String productOid;
        private BigDecimal quantity;

        private Builder()
        {
        }

        public Builder withProductOid(String productOid)
        {
            this.productOid = productOid;
            return this;
        }

        public Builder withQuantity(BigDecimal quantity)
        {
            this.quantity = quantity;
            return this;
        }

        public ValidateStockResponseEntryDto build()
        {
            return new ValidateStockResponseEntryDto(this);
        }
    }
}
