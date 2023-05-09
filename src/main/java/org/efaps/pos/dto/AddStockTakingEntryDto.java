/*
 * Copyright 2003 - 2023 The eFaps Team
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
package org.efaps.pos.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = AddStockTakingEntryDto.Builder.class)
public class AddStockTakingEntryDto
{

    private final String id;
    private final BigDecimal quantity;
    private final String productOid;

    private AddStockTakingEntryDto(Builder builder)
    {
        this.id = builder.id;
        this.quantity = builder.quantity;
        this.productOid = builder.productOid;
    }

    public String getId()
    {
        return id;
    }

    public BigDecimal getQuantity()
    {
        return quantity;
    }

    public String getProductOid()
    {
        return productOid;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private String id;
        private BigDecimal quantity;
        private String productOid;

        private Builder()
        {
        }

        public Builder withId(String id)
        {
            this.id = id;
            return this;
        }

        public Builder withQuantity(BigDecimal quantity)
        {
            this.quantity = quantity;
            return this;
        }

        public Builder withProductOid(String productOid)
        {
            this.productOid = productOid;
            return this;
        }

        public AddStockTakingEntryDto build()
        {
            return new AddStockTakingEntryDto(this);
        }
    }
}
