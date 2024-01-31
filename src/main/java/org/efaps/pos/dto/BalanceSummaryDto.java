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

import java.util.Collections;
import java.util.List;

public class BalanceSummaryDto
{

    private final BalanceDto balance;
    private final PosUserDto user;
    private final List<CashEntryDto> cashEntries;
    private final BalanceSummaryDetailDto detail;
    private final BalanceSummaryDetailDto invoiceDetail;
    private final BalanceSummaryDetailDto receiptDetail;
    private final BalanceSummaryDetailDto ticketDetail;
    private final BalanceSummaryDetailDto creditNoteDetail;

    private BalanceSummaryDto(final Builder builder)
    {
        balance = builder.balance;
        user = builder.user;
        cashEntries = builder.cashEntries;
        detail = builder.detail;
        invoiceDetail = builder.invoiceDetail;
        receiptDetail = builder.receiptDetail;
        ticketDetail = builder.ticketDetail;
        creditNoteDetail=builder.creditNoteDetail;
    }

    public BalanceDto getBalance()
    {
        return balance;
    }

    public PosUserDto getUser()
    {
        return user;
    }

    public List<CashEntryDto> getCashEntries()
    {
        return cashEntries;
    }

    public BalanceSummaryDetailDto getDetail()
    {
        return detail;
    }

    public BalanceSummaryDetailDto getInvoiceDetail()
    {
        return invoiceDetail;
    }

    public BalanceSummaryDetailDto getReceiptDetail()
    {
        return receiptDetail;
    }

    public BalanceSummaryDetailDto getTicketDetail()
    {
        return ticketDetail;
    }

    public BalanceSummaryDetailDto getCreditNoteDetail()
    {
        return creditNoteDetail;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private BalanceDto balance;
        private PosUserDto user;
        private List<CashEntryDto> cashEntries = Collections.emptyList();
        private BalanceSummaryDetailDto detail;
        private BalanceSummaryDetailDto invoiceDetail;
        private BalanceSummaryDetailDto receiptDetail;
        private BalanceSummaryDetailDto ticketDetail;
        private BalanceSummaryDetailDto creditNoteDetail;

        private Builder()
        {
        }

        public Builder withBalance(final BalanceDto balance)
        {
            this.balance = balance;
            return this;
        }

        public Builder withUser(final PosUserDto user)
        {
            this.user = user;
            return this;
        }

        public Builder withCashEntries(final List<CashEntryDto> cashEntries)
        {
            this.cashEntries = cashEntries;
            return this;
        }

        public Builder withDetail(final BalanceSummaryDetailDto detail)
        {
            this.detail = detail;
            return this;
        }

        public Builder withInvoiceDetail(final BalanceSummaryDetailDto invoiceDetail)
        {
            this.invoiceDetail = invoiceDetail;
            return this;
        }

        public Builder withReceiptDetail(final BalanceSummaryDetailDto receiptDetail)
        {
            this.receiptDetail = receiptDetail;
            return this;
        }

        public Builder withTicketDetail(final BalanceSummaryDetailDto ticketDetail)
        {
            this.ticketDetail = ticketDetail;
            return this;
        }

        public Builder withCreditNoteDetaill(final BalanceSummaryDetailDto creditNoteDetail)
        {
            this.creditNoteDetail = creditNoteDetail;
            return this;
        }

        public BalanceSummaryDto build()
        {
            return new BalanceSummaryDto(this);
        }
    }

}
