package org.efaps.pos.entity;

import org.springframework.data.annotation.Id;

public abstract class AbstractDocument<T>
{

    @Id
    private String id;
    private String oid;
    private String number;

    public String getId()
    {
        return this.id;
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
}
