package org.efaps.pos.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;

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

        public Builder withOID(final String _oid)
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
