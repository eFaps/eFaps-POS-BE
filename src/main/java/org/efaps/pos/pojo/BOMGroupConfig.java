package org.efaps.pos.pojo;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class BOMGroupConfig
{

    private String oid;
    private String productOid;
    private String name;
    private String description;
    private int weight;

    private int flags;

    public String getOid()
    {
        return oid;
    }

    public BOMGroupConfig setOid(String oid)
    {
        this.oid = oid;
        return this;
    }

    public String getProductOid()
    {
        return productOid;
    }

    public BOMGroupConfig setProductOid(String productOid)
    {
        this.productOid = productOid;
        return this;
    }

    public String getName()
    {
        return name;
    }

    public BOMGroupConfig setName(String name)
    {
        this.name = name;
        return this;
    }

    public String getDescription()
    {
        return description;
    }

    public BOMGroupConfig setDescription(String description)
    {
        this.description = description;
        return this;
    }

    public int getWeight()
    {
        return weight;
    }

    public BOMGroupConfig setWeight(int weight)
    {
        this.weight = weight;
        return this;
    }

    public int getFlags()
    {
        return flags;
    }

    public BOMGroupConfig setFlags(int flags)
    {
        this.flags = flags;
        return this;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this);
    }
}
