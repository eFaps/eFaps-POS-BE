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

import org.efaps.pos.interfaces.ICreditNoteItem;
import org.efaps.pos.interfaces.IInvoiceItem;
import org.efaps.pos.interfaces.IReceiptItem;
import org.efaps.pos.interfaces.ITicketItem;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = PosDocItemDto.Builder.class)
public class PosDocItemDto
    extends AbstractDocItemDto
    implements IReceiptItem, IInvoiceItem, ITicketItem, ICreditNoteItem
{

    private final ProductDto product;
    private final ProductDto standIn;

    public PosDocItemDto(final Builder _builder)
    {
        super(_builder);
        product = _builder.product;
        standIn = _builder.standIn;
    }

    public ProductDto getProduct()
    {
        return product;
    }

    public ProductDto getStandIn()
    {
        return standIn;
    }

    @Override
    public String getDescription()
    {
        return getProduct() == null ? "Missing Product" : getProduct().getDescription();
    }

    @Override
    public String getSku()
    {
        return getProduct() == null ? "Missing Product" : getProduct().getSku();
    }

    @Override
    public String getUoMCode()
    {
        return getProduct() == null ? "Missing Product" : getProduct().getUoMCode();
    }

    public static Builder builder()
    {
        return new Builder();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder
        extends AbstractDocItemDto.Builder<Builder>
    {

        private ProductDto product;
        private ProductDto standIn;

        public Builder withProduct(final ProductDto _productDto)
        {
            product = _productDto;
            return this;
        }

        public Builder withStandIn(final ProductDto standIn)
        {
            this.standIn = standIn;
            return this;
        }

        public PosDocItemDto build()
        {
            return new PosDocItemDto(this);
        }
    }
}
