/*
 * Copyright 2003 - 2018 The eFaps Team
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

import java.util.Set;

import org.efaps.pos.interfaces.IReceipt;

@JsonDeserialize(builder = PosReceiptDto.Builder.class)
public class PosReceiptDto
    extends AbstractPayableDocumentDto
    implements IReceipt
{

    public PosReceiptDto(final Builder _builder)
    {
        super(_builder);
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractPayableDocumentDto.Builder<Builder, PosReceiptDto>
    {

        public Builder withItems(final Set<PosDocItemDto> _items)
        {
            setItems(_items);
            return this;
        }

        public Builder withPayments(final Set<PaymentDto> _payments)
        {
            setPayments(_payments);
            return this;
        }

        @Override
        public PosReceiptDto build()
        {
            return new PosReceiptDto(this);
        }
    }
}
