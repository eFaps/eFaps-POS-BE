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
package org.efaps.pos.util;

import java.math.BigDecimal;
import java.util.Objects;

import org.efaps.pos.dto.Currency;
import org.efaps.pos.dto.PaymentInfoDto;
import org.efaps.pos.dto.PaymentType;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = PaymentGroup.Builder.class)
public class PaymentGroup
{

    private final PaymentType type;
    private final String label;
    private final Currency currency;

    private PaymentGroup(Builder builder)
    {
        this.type = builder.type;
        this.label = builder.label;
        this.currency = builder.currency;
    }

    public PaymentType getType()
    {
        return type;
    }

    public String getLabel()
    {
        return label;
    }

    public Currency getCurrency()
    {
        return currency;
    }

    public PaymentInfoDto getInfo(final int count,
                                  final BigDecimal amount)
    {
        return PaymentInfoDto.builder()
                        .withCount(count)
                        .withAmount(amount)
                        .withLabel(label)
                        .withType(type)
                        .withCurrency(currency)
                        .build();
    }

    @Override
    public boolean equals(final Object _obj)
    {
        boolean ret = false;
        if (_obj instanceof final PaymentGroup other) {
            ret = other.type.equals(type)
                            && Objects.equals(other.label, label)
                            && Objects.equals(other.currency, currency);
        } else {
            ret = super.equals(_obj);
        }
        return ret;
    }

    @Override
    public int hashCode()
    {
        return type.hashCode() + (label == null ? 0 : label.hashCode()) + currency.hashCode();
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {

        private PaymentType type;
        private String label;
        private Currency currency = Currency.PEN;

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

        public Builder withCurrency(final Currency currency)
        {
            this.currency = currency;
            return this;
        }

        public PaymentGroup build()
        {
            return new PaymentGroup(this);
        }
    }
}
