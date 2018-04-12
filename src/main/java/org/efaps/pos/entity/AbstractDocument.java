package org.efaps.pos.entity;

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
    }
}
