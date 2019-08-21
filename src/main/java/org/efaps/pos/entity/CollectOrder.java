package org.efaps.pos.entity;

import java.math.BigDecimal;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "collectorders")
public class CollectOrder
{

    @Id
    private String id;

    private BigDecimal amount;

    private Collector collector;

    public String getId()
    {
        return id;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public CollectOrder setAmount(final BigDecimal _amount)
    {
        amount = _amount;
        return this;
    }

    public Collector getCollector()
    {
        return collector;
    }

    public CollectOrder setCollector(final Collector _collector)
    {
        collector = _collector;
        return this;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this);
    }
}
