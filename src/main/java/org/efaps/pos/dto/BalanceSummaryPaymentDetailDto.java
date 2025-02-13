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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = BalanceSummaryPaymentDetailDto.Builder.class)
public class BalanceSummaryPaymentDetailDto
{

    private final IPosPaymentDto payment;
    private final String payableNumber;
    private final DocType payableType;

    private BalanceSummaryPaymentDetailDto(Builder builder)
    {
        this.payment = builder.payment;
        this.payableNumber = builder.payableNumber;
        this.payableType = builder.payableType;
    }

    public IPosPaymentDto getPayment()
    {
        return payment;
    }

    public String getPayableNumber()
    {
        return payableNumber;
    }

    public DocType getPayableType()
    {
        return payableType;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private IPosPaymentDto payment;
        private String payableNumber;
        private DocType payableType;

        private Builder()
        {
        }

        public Builder withPayment(IPosPaymentDto payment)
        {
            this.payment = payment;
            return this;
        }

        public Builder withPayableNumber(String payableNumber)
        {
            this.payableNumber = payableNumber;
            return this;
        }

        public Builder withPayableType(DocType payableType)
        {
            this.payableType = payableType;
            return this;
        }

        public BalanceSummaryPaymentDetailDto build()
        {
            return new BalanceSummaryPaymentDetailDto(this);
        }
    }
}
