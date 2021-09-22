/*
 * Copyright 2003 - 2021 The eFaps Team
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
import java.time.LocalDate;

public class DocumentHeadDto
{
    private final String id;
    private final String number;
    private final BigDecimal netTotal;
    private final BigDecimal crossTotal;
    private final Currency currency;
    private final BigDecimal exchangeRate;
    private final LocalDate date;
    private final DocStatus status;

    public DocumentHeadDto(final Builder<?, ?> _builder)
    {
        id = _builder.id;
        number = _builder.number;
        netTotal = _builder.netTotal;
        crossTotal = _builder.crossTotal;
        currency = _builder.currency;
        exchangeRate = _builder.exchangeRate;
        date = _builder.date;
        status = _builder.status;
    }

    public String getId()
    {
        return id;
    }

    public String getNumber()
    {
        return number;
    }

    public BigDecimal getNetTotal()
    {
        return netTotal;
    }

    public BigDecimal getCrossTotal()
    {
        return crossTotal;
    }

    public Currency getCurrency()
    {
        return currency;
    }

    public BigDecimal getExchangeRate()
    {
        return exchangeRate;
    }

    public LocalDate getDate()
    {
        return date;
    }

    public DocStatus getStatus()
    {
        return status;
    }

    public static class Builder<S extends Builder<S, T>, T extends DocumentHeadDto>
    {
        private String id;
        private String number;
        private BigDecimal netTotal;
        private BigDecimal crossTotal;
        private Currency currency;
        private BigDecimal exchangeRate;
        private LocalDate date;
        private DocStatus status;

        @SuppressWarnings("unchecked")
        public S withId(final String _id)
        {
            this.id = _id;
            return (S) this;
        }

        @SuppressWarnings("unchecked")
        public S withNumber(final String _number)
        {
            this.number = _number;
            return (S) this;
        }

        @SuppressWarnings("unchecked")
        public S withNetTotal(final BigDecimal _netTotal)
        {
            this.netTotal = _netTotal;
            return (S) this;
        }

        @SuppressWarnings("unchecked")
        public S withCrossTotal(final BigDecimal _crossTotal)
        {
            this.crossTotal = _crossTotal;
            return (S) this;
        }

        @SuppressWarnings("unchecked")
        public S withCurrency(final Currency _currency)
        {
            currency = _currency;
            return (S) this;
        }

        @SuppressWarnings("unchecked")
        public S withExchangeRate(final BigDecimal _exchangeRate)
        {
            exchangeRate = _exchangeRate;
            return (S) this;
        }

        @SuppressWarnings("unchecked")
        public S withDate(final LocalDate _date)
        {
            date = _date;
            return (S) this;
        }

        @SuppressWarnings("unchecked")
        public S withStatus(final DocStatus _status)
        {
            status = _status;
            return (S) this;
        }

        public DocumentHeadDto build()
        {
            return new DocumentHeadDto(this);
        }
    }
}
