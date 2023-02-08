package org.efaps.pos.pojo;

import java.math.BigDecimal;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class ConfigurationBOM
{

    private String oid;
    private String toProductOid;
    private String bomGroupOid;
    private int position;

    private BigDecimal quantity;
    private String uoM;

    public String getOid()
    {
        return oid;
    }

    public ConfigurationBOM setOid(String oid)
    {
        this.oid = oid;
        return this;
    }

    public String getToProductOid()
    {
        return toProductOid;
    }

    public ConfigurationBOM setToProductOid(String toProductOid)
    {
        this.toProductOid = toProductOid;
        return this;
    }

    public String getBomGroupOid()
    {
        return bomGroupOid;
    }

    public ConfigurationBOM setBomGroupOid(String bomGroupOid)
    {
        this.bomGroupOid = bomGroupOid;
        return this;
    }

    public int getPosition()
    {
        return position;
    }

    public ConfigurationBOM setPosition(int position)
    {
        this.position = position;
        return this;
    }

    public BigDecimal getQuantity()
    {
        return quantity;
    }

    public ConfigurationBOM setQuantity(BigDecimal quantity)
    {
        this.quantity = quantity;
        return this;
    }

    public String getUoM()
    {
        return uoM;
    }

    public ConfigurationBOM setUoM(String uoM)
    {
        this.uoM = uoM;
        return this;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this);
    }
}
