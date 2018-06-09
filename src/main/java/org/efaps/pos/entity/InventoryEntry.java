package org.efaps.pos.entity;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "inventory")
public class InventoryEntry
{

    @Id
    private String id;

    private String oid;

    private BigDecimal quantity;

    private String warehouseOid;

    private String productOid;

    public String getId()
    {
        return this.id;
    }

    public InventoryEntry setId(final String _id)
    {
        this.id = _id;
        return this;
    }

    public String getOid()
    {
        return this.oid;
    }

    public InventoryEntry setOid(final String _oid)
    {
        this.oid = _oid;
        return this;
    }

    public BigDecimal getQuantity()
    {
        return this.quantity;
    }

    public void setQuantity(final BigDecimal _quantity)
    {
        this.quantity = _quantity;
    }

    public String getWarehouseOid()
    {
        return this.warehouseOid;
    }

    public InventoryEntry setWarehouseOid(final String _warehouseOid)
    {
        this.warehouseOid = _warehouseOid;
        return this;
    }

    public String getProductOid()
    {
        return this.productOid;
    }

    public InventoryEntry setProductOid(final String _productOid)
    {
        this.productOid = _productOid;
        return this;
    }
}
