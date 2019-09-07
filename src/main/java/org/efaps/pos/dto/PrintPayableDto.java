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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = PrintPayableDto.Builder.class)
public class PrintPayableDto
{

    private final AbstractPayableDocumentDto payable;

    private final PosOrderDto order;

    public PrintPayableDto(final Builder _builder)
    {
        payable = _builder.payable;
        order = _builder.order;
    }

    public AbstractPayableDocumentDto getPayable()
    {
        return payable;
    }

    public PosOrderDto getOrder()
    {
        return order;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {

        private AbstractPayableDocumentDto payable;

        private PosOrderDto order;

        public Builder withPayable(final AbstractPayableDocumentDto _payable)
        {
            payable = _payable;
            return this;
        }

        public Builder withOrder(final PosOrderDto _order)
        {
            order = _order;
            return this;
        }

        public PrintPayableDto build()
        {
            return new PrintPayableDto(this);
        }
    }
}
