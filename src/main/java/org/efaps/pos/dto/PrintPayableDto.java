/*
 * Copyright 2003 - 2020 The eFaps Team
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

import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = PrintPayableDto.Builder.class)
public class PrintPayableDto
{

    private final DocType payableType;
    private final AbstractPayableDocumentDto payable;

    private final PosOrderDto order;

    private final Map<String, Object> additionalInfo;

    private final String amountInWords;

    private PrintPayableDto(final Builder builder)
    {
        payableType = builder.payableType;
        payable = builder.payable;
        order = builder.order;
        additionalInfo = builder.additionalInfo;
        amountInWords = builder.amountInWords;
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

    /**
     * Creates builder to build {@link PrintPayableDto}.
     * @return created builder
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to build {@link PrintPayableDto}.
     */
    public static final class Builder
    {
        private DocType payableType;
        private AbstractPayableDocumentDto payable;
        private PosOrderDto order;
        private Map<String, Object> additionalInfo = Collections.emptyMap();
        private String amountInWords;

        private Builder()
        {
        }

        public Builder withPayableType(final DocType payableType)
        {
            this.payableType = payableType;
            return this;
        }


        public Builder withPayable(final AbstractPayableDocumentDto payable)
        {
            this.payable = payable;
            return this;
        }

        public Builder withOrder(final PosOrderDto order)
        {
            this.order = order;
            return this;
        }

        public Builder withAdditionalInfo(final Map<String, Object> additionalInfo)
        {
            this.additionalInfo = additionalInfo;
            return this;
        }

        public Builder withAmountInWords(final String amountInWords)
        {
            this.amountInWords = amountInWords;
            return this;
        }

        public PrintPayableDto build()
        {
            return new PrintPayableDto(this);
        }
    }
}
