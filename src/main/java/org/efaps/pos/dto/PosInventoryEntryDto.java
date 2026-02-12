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

import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = PosInventoryEntryDto.Builder.class)
public class PosInventoryEntryDto
{

    private final String id;
    private final String oid;
    private final BigDecimal quantity;
    private final WarehouseDto warehouse;
    private final ProductDto product;

    private PosInventoryEntryDto(final Builder _builder)
    {
        this.id = _builder.id;
        this.oid = _builder.oid;
        this.quantity = _builder.quantity;
        this.warehouse = _builder.warehouse;
        this.product = _builder.product;
    }

    public String getId()
    {
        return this.id;
    }

    public String getOid()
    {
        return this.oid;
    }

    public BigDecimal getQuantity()
    {
        return this.quantity;
    }

    public WarehouseDto getWarehouse()
    {
        return this.warehouse;
    }

    public ProductDto getProduct()
    {
        return this.product;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        public ProductDto product;
        public WarehouseDto warehouse;
        public BigDecimal quantity;
        private String id;
        private String oid;

        public Builder withId(final String _id)
        {
            this.id = _id;
            return this;
        }

        public Builder withOid(final String _oid)
        {
            this.oid = _oid;
            return this;
        }

        public Builder withQuantity(final BigDecimal _quantity)
        {
            this.quantity = _quantity;
            return this;
        }

        public Builder withProduct(final ProductDto _product)
        {
            this.product = _product;
            return this;
        }

        public Builder withWarehouse(final WarehouseDto _warehouse)
        {
            this.warehouse = _warehouse;
            return this;
        }

        public PosInventoryEntryDto build()
        {
            return new PosInventoryEntryDto(this);
        }
    }

}
