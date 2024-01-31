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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = PaymentInfoDto.Builder.class)
public class PaymentInfoDto
{

    private final PaymentType type;
    private final String label;
    private final int count;
    private final BigDecimal amount;
    private final Currency currency;

    private PaymentInfoDto(final Builder _builder)
    {
        type = _builder.type;
        label = _builder.label == null ? "" : _builder.label;
        count = _builder.count;
        amount = _builder.amount;
        currency = _builder.currency;
    }

    public PaymentType getType()
    {
        return type;
    }

    public String getLabel()
    {
        return label;
    }

    public int getCount()
    {
        return count;
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

    public static class Builder
    {

        private PaymentType type;
        private String label;
        private int count;
        private BigDecimal amount;
        private Currency currency;

        public Builder withType(final PaymentType _type)
        {
            type = _type;
            return this;
        }

        public Builder withLabel(final String label)
        {
            this.label = label;
            return this;
        }

        public Builder withCount(final int _count)
        {
            count = _count;
            return this;
        }

        public Builder withAmount(final BigDecimal _amount)
        {
            amount = _amount;
            return this;
        }

        public Builder withCurrency(final Currency _currency)
        {
            currency = _currency;
            return this;
        }

        public PaymentInfoDto build()
        {
            return new PaymentInfoDto(this);
        }
    }
}
