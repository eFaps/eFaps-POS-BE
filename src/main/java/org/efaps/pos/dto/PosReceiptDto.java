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

import java.util.Set;
import java.util.stream.Collectors;

import org.efaps.pos.interfaces.IReceipt;
import org.efaps.pos.interfaces.IReceiptItem;
import org.efaps.pos.util.Converter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = PosReceiptDto.Builder.class)
public class PosReceiptDto
    extends AbstractPayableDocumentDto
    implements IReceipt
{
    private final DiscountDto discount;

    public PosReceiptDto(final Builder _builder)
    {
        super(_builder);
        discount = _builder.discount;
    }

    public DiscountDto getDiscount()
    {
        return discount;
    }

    @Override
    public Set<IReceiptItem> getReceiptItems()
    {
        return getItems().stream().map(item -> (IReceiptItem) item).collect(Collectors.toSet());
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder
        extends AbstractPayableDocumentDto.Builder<Builder, PosReceiptDto>
    {
        private DiscountDto discount;

        public Builder withDiscount(final DiscountDto _discount)
        {
            discount = _discount;
            return this;
        }

        public Builder withItems(final Set<PosDocItemDto> _items)
        {
            setItems(_items);
            return this;
        }

        public Builder withPayments(final Set<PosPaymentDto> _payments)
        {
            if (_payments == null) {
              setPayments(null);
            } else {
              setPayments(_payments.stream().map(posDto -> (PaymentDto) posDto).collect(Collectors.toSet()));
            }
            return this;
        }

        @Override
        public PosReceiptDto build()
        {
            return new PosReceiptDto(this);
        }
    }
}
