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

package org.efaps.pos.dto;

import java.math.BigDecimal;
import java.util.Collection;

public class BalanceSummaryDetailDto
{

    private final int documentCount;
    private final int paymentCount;
    private final BigDecimal netTotal;
    private final BigDecimal crossTotal;
    private final Collection<PaymentInfoDto> paymentInfos;
    private final Collection<TaxEntryDto> taxEntries;


    private BalanceSummaryDetailDto(final Builder _builder)
    {
        documentCount = _builder.documentCount;
        paymentCount = _builder.paymentCount;
        netTotal = _builder.netTotal == null ? BigDecimal.ZERO : _builder.netTotal;
        crossTotal = _builder.crossTotal == null ? BigDecimal.ZERO : _builder.crossTotal;
        paymentInfos = _builder.paymentInfos;
        taxEntries = _builder.taxEntries;
    }

    public int getDocumentCount()
    {
        return documentCount;
    }

    public int getPaymentCount()
    {
        return paymentCount;
    }

    public BigDecimal getNetTotal()
    {
        return netTotal;
    }

    public BigDecimal getCrossTotal()
    {
        return crossTotal;
    }

    public Collection<PaymentInfoDto> getPaymentInfos()
    {
        return paymentInfos;
    }

    public Collection<TaxEntryDto> getTaxEntries()
    {
        return taxEntries;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {

        private int documentCount;
        private int paymentCount;
        private BigDecimal netTotal;
        private BigDecimal crossTotal;
        private Collection<PaymentInfoDto> paymentInfos;
        private Collection<TaxEntryDto> taxEntries;

        public Builder withDocumentCount(final int _documentCount)
        {
            documentCount = _documentCount;
            return this;
        }

        public Builder withPaymentCount(final int _paymentCount)
        {
            paymentCount = _paymentCount;
            return this;
        }

        public Builder withCrossTotal(final BigDecimal _crossTotal)
        {
            crossTotal = _crossTotal;
            return this;
        }

        public Builder withNetTotal(final BigDecimal _netTotal)
        {
            netTotal = _netTotal;
            return this;
        }

        public Builder withPayments(final Collection<PaymentInfoDto> _paymentInfos)
        {
            paymentInfos = _paymentInfos;
            return this;
        }

        public Builder withtTaxEntries(final Collection<TaxEntryDto> _taxEntries)
        {
            taxEntries = _taxEntries;
            return this;
        }

        public BalanceSummaryDetailDto build()
        {
            return new BalanceSummaryDetailDto(this);
        }

    }
}
