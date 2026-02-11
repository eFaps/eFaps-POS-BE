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

import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = MoneyAmountDto.Builder.class)
public class MoneyAmountDto
{

    private final Currency currency;
    private final BigDecimal amount;

    private MoneyAmountDto(Builder builder)
    {
        this.currency = builder.currency;
        this.amount = builder.amount;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public Currency getCurrency()
    {
        return currency;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private Currency currency;
        private BigDecimal amount;

        private Builder()
        {
        }

        public Builder withCurrency(Currency currency)
        {
            this.currency = currency;
            return this;
        }

        public Builder withAmount(BigDecimal amount)
        {
            this.amount = amount;
            return this;
        }

        public MoneyAmountDto build()
        {
            return new MoneyAmountDto(this);
        }
    }
}
