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

import org.efaps.pos.interfaces.IReceiptItem;

@JsonDeserialize(builder = PosDocItemDto.Builder.class)
public class PosDocItemDto
    extends AbstractDocItemDto
    implements IReceiptItem
{

    private final ProductDto product;

    public PosDocItemDto(final Builder _builder)
    {
        super(_builder);
        this.product = _builder.product;
    }

    public ProductDto getProduct()
    {
        return this.product;
    }

    @Override
    public String getDescription()
    {
        return getProduct() == null ? "Missing Product" : getProduct().getDescription();
    }

    @Override
    public String getSku()
    {
        return  getProduct() == null ? "Missing Product" : getProduct().getSku();
    }

    @Override
    public String getUoM()
    {
        return  getProduct() == null ? "Missing Product" : getProduct().getUoM();
    }

    /**
     * Creates builder to build {@link AgendaDto}.
     *
     * @return created builder
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to build {@link AgendaDto}.
     */
    public static final class Builder
        extends AbstractDocItemDto.Builder<Builder, PosDocItemDto>
    {

        private ProductDto product;

        public Builder withProduct(final ProductDto _productDto) {
            this.product =_productDto;
            return this;
        }

        @Override
        public PosDocItemDto build()
        {
            return new PosDocItemDto(this);
        }
    }
}
