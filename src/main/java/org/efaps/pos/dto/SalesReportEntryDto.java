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

import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = SalesReportEntryDto.Builder.class)
public class SalesReportEntryDto
{

    private final IPosPaymentDto payment;

    private final AbstractPayableDocumentDto payable;

    private SalesReportEntryDto(Builder builder)
    {
        this.payment = builder.payment;
        this.payable = builder.payable;
    }

    public IPosPaymentDto getPayment()
    {
        return payment;
    }

    public AbstractPayableDocumentDto getPayable()
    {
        return payable;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private IPosPaymentDto payment;
        private AbstractPayableDocumentDto payable;

        private Builder()
        {
        }

        public Builder withPayment(IPosPaymentDto payment)
        {
            this.payment = payment;
            return this;
        }

        public Builder withPayable(AbstractPayableDocumentDto payable)
        {
            this.payable = payable;
            return this;
        }

        public SalesReportEntryDto build()
        {
            return new SalesReportEntryDto(this);
        }
    }
}
