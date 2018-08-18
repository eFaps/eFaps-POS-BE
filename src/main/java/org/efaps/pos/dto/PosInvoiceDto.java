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
import java.util.stream.Collectors;

import org.efaps.pos.interfaces.IInvoice;
import org.efaps.pos.interfaces.IInvoiceItem;
import org.efaps.pos.util.Converter;

@JsonDeserialize(builder = PosInvoiceDto.Builder.class)
public class PosInvoiceDto
    extends AbstractPayableDocumentDto
    implements IInvoice
{

    public PosInvoiceDto(final Builder _builder)
    {
        super(_builder);
    }

    @Override
    public Set<IInvoiceItem> getInvoiceItems()
    {
        return getItems().stream().map(item -> (IInvoiceItem) item).collect(Collectors.toSet());
    }

    @Override
    public ContactDto getContact()
    {
        return Converter.getContactDto(getContactOid());
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractPayableDocumentDto.Builder<Builder, PosInvoiceDto>
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
        public PosInvoiceDto build()
        {
            return new PosInvoiceDto(this);
        }
    }
}
