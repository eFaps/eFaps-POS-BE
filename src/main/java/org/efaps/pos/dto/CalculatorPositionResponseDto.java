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
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = CalculatorPositionResponseDto.Builder.class)
public class CalculatorPositionResponseDto
{

    private final BigDecimal quantity;

    private final String productOid;

    private final BigDecimal netUnitPrice;

    private final BigDecimal netPrice;

    private final List<TaxEntryDto> taxes;

    private final BigDecimal taxAmount;

    private final BigDecimal crossUnitPrice;

    private final BigDecimal crossPrice;

    private CalculatorPositionResponseDto(Builder builder)
    {
        this.quantity = builder.quantity;
        this.productOid = builder.productOid;
        this.netUnitPrice = builder.netUnitPrice;
        this.netPrice = builder.netPrice;
        this.taxes = builder.taxes;
        this.taxAmount = builder.taxAmount;
        this.crossUnitPrice = builder.crossUnitPrice;
        this.crossPrice = builder.crossPrice;
    }

    public BigDecimal getQuantity()
    {
        return quantity;
    }

    public String getProductOid()
    {
        return productOid;
    }

    public BigDecimal getNetUnitPrice()
    {
        return netUnitPrice;
    }

    public BigDecimal getCrossUnitPrice()
    {
        return crossUnitPrice;
    }

    public BigDecimal getNetPrice()
    {
        return netPrice;
    }

    public List<TaxEntryDto> getTaxes()
    {
        return taxes;
    }

    public BigDecimal getTaxAmount()
    {
        return taxAmount;
    }

    public BigDecimal getCrossPrice()
    {
        return crossPrice;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private BigDecimal quantity;
        private String productOid;
        private BigDecimal netUnitPrice;
        private BigDecimal netPrice;
        private List<TaxEntryDto> taxes = Collections.emptyList();
        private BigDecimal taxAmount;
        private BigDecimal crossUnitPrice;
        private BigDecimal crossPrice;

        private Builder()
        {
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

        public Builder withNetUnitPrice(BigDecimal netUnitPrice)
        {
            this.netUnitPrice = netUnitPrice;
            return this;
        }

        public Builder withNetPrice(BigDecimal netPrice)
        {
            this.netPrice = netPrice;
            return this;
        }

        public Builder withTaxes(List<TaxEntryDto> taxes)
        {
            this.taxes = taxes;
            return this;
        }

        public Builder withTaxAmount(BigDecimal taxAmount)
        {
            this.taxAmount = taxAmount;
            return this;
        }

        public Builder withCrossUnitPrice(BigDecimal crossUnitPrice)
        {
            this.crossUnitPrice = crossUnitPrice;
            return this;
        }

        public Builder withCrossPrice(BigDecimal crossPrice)
        {
            this.crossPrice = crossPrice;
            return this;
        }

        public CalculatorPositionResponseDto build()
        {
            return new CalculatorPositionResponseDto(this);
        }
    }
}
