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

import java.time.LocalTime;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;

import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = PrintPayableDto.Builder.class)
public class PrintPayableDto
{

    private final DocType payableType;

    private final AbstractPayableDocumentDto payable;

    private final PrintPosOrderDto order;

    private final Map<String, Object> additionalInfo;

    private final String amountInWords;

    private final LocalTime time;

    private final PromoInfoDto promoInfo;

    private final Set<PrintEmployeeRelationDto> employees;

    private PrintPayableDto(Builder builder)
    {
        this.payableType = builder.payableType;
        this.payable = builder.payable;
        this.order = builder.order;
        this.additionalInfo = builder.additionalInfo;
        this.amountInWords = builder.amountInWords;
        this.time = builder.time;
        this.promoInfo = builder.promoInfo;
        this.employees = builder.employees;
    }

    public DocType getPayableType()
    {
        return payableType;
    }

    public AbstractPayableDocumentDto getPayable()
    {
        return payable;
    }

    public PosOrderDto getOrder()
    {
        return order;
    }

    public Map<String, Object> getAdditionalInfo()
    {
        return additionalInfo;
    }

    public String getAmountInWords()
    {
        return amountInWords;
    }

    public LocalTime getTime()
    {
        return time;
    }

    public PromoInfoDto getPromoInfo()
    {
        return promoInfo;
    }

    public Set<PrintEmployeeRelationDto> getEmployees()
    {
        return employees;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private DocType payableType;
        private AbstractPayableDocumentDto payable;
        private PrintPosOrderDto order;
        private Map<String, Object> additionalInfo = Collections.emptyMap();
        private String amountInWords;
        private LocalTime time;
        private PromoInfoDto promoInfo;
        private Set<PrintEmployeeRelationDto> employees = Collections.emptySet();

        private Builder()
        {
        }

        public Builder withPayableType(DocType payableType)
        {
            this.payableType = payableType;
            return this;
        }

        public Builder withPayable(AbstractPayableDocumentDto payable)
        {
            this.payable = payable;
            return this;
        }

        public Builder withOrder(PrintPosOrderDto order)
        {
            this.order = order;
            return this;
        }

        public Builder withAdditionalInfo(Map<String, Object> additionalInfo)
        {
            this.additionalInfo = additionalInfo;
            return this;
        }

        public Builder withAmountInWords(String amountInWords)
        {
            this.amountInWords = amountInWords;
            return this;
        }

        public Builder withTime(LocalTime time)
        {
            this.time = time;
            return this;
        }

        public Builder withPromoInfo(PromoInfoDto promoInfo)
        {
            this.promoInfo = promoInfo;
            return this;
        }

        public Builder withEmployees(Set<PrintEmployeeRelationDto> employees)
        {
            this.employees = employees;
            return this;
        }

        public PrintPayableDto build()
        {
            return new PrintPayableDto(this);
        }
    }
}
