package org.efaps.pos.entity;

import java.math.BigDecimal;
import java.util.Set;

import org.efaps.pos.dto.DocStatus;
import org.springframework.data.annotation.Id;

public abstract class AbstractDocument<T>
{

    @Id
    private String id;
    private String oid;
    private String number;
    private Set<Item> items;
    private DocStatus status;

    public String getId()
    {
        return this.id;
    }

    @SuppressWarnings("unchecked")
    public T setId(final String _id)
    {
        this.id = _id;
        return (T) this;
    }

    public String getOid()
    {
        return this.oid;
    }

    @SuppressWarnings("unchecked")
    public T setOid(final String _oid)
    {
        this.oid = _oid;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setNumber(final String _number)
    {
        this.number = _number;
        return (T) this;
    }

    public String getNumber()
    {
        return this.number;
    }

    public Set<Item> getItems()
    {
        return this.items;
    }

    @SuppressWarnings("unchecked")
    public T setItems(final Set<Item> _items)
    {
        this.items = _items;
        return (T) this;
    }

    public DocStatus getStatus()
    {
        return this.status;
    }

    @SuppressWarnings("unchecked")
    public T setStatus(final DocStatus _status)
    {
        this.status = _status;
        return (T) this;
    }

    public static class Item
    {

        private String oid;
        private Integer index;
        private String productOid;
        private BigDecimal quantity;
        private BigDecimal netUnitPrice;
        private BigDecimal crossUnitPrice;
        private BigDecimal netPrice;
        private BigDecimal crossPrice;

        public Integer getIndex()
        {
            return this.index;
        }

        public Item setIndex(final Integer _index)
        {
            this.index = _index;
            return this;
        }

        public String getOid()
        {
            return this.oid;
        }

        public Item setOid(final String _oid)
        {
            this.oid = _oid;
            return this;
        }

        public String getProductOid()
        {
            return this.productOid;
        }

        public Item setProductOid(final String _productOid)
        {
            this.productOid = _productOid;
            return this;
        }

        public BigDecimal getQuantity()
        {
            return this.quantity;
        }

        public Item setQuantity(final BigDecimal _quantity)
        {
            this.quantity = _quantity;
            return this;
        }

        public BigDecimal getNetUnitPrice()
        {
            return this.netUnitPrice;
        }

        public Item setNetUnitPrice(final BigDecimal _netUnitPrice)
        {
            this.netUnitPrice = _netUnitPrice;
            return this;
        }

        public BigDecimal getCrossUnitPrice()
        {
            return this.crossUnitPrice;
        }

        public Item setCrossUnitPrice(final BigDecimal _crossUnitPrice)
        {
            this.crossUnitPrice = _crossUnitPrice;
            return this;
        }

        public BigDecimal getNetPrice()
        {
            return this.netPrice;
        }

        public Item setNetPrice(final BigDecimal _netPrice)
        {
            this.netPrice = _netPrice;
            return this;
        }

        public BigDecimal getCrossPrice()
        {
            return this.crossPrice;
        }

        public Item setCrossPrice(final BigDecimal _crossPrice)
        {
            this.crossPrice = _crossPrice;
            return this;
        }
    }
}
