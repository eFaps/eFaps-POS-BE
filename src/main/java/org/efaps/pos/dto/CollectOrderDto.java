package org.efaps.pos.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;

@JsonDeserialize(builder = CollectOrderDto.Builder.class)
public class CollectOrderDto
{
    private final String id;

    private final BigDecimal amount;

    private CollectOrderDto(final Builder _builder)
    {
        id = _builder.id;
        amount = _builder.amount;
    }

    public String getId()
    {
        return id;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private String id;
        private BigDecimal amount;

        public Builder withId(final String _id)
        {
            id = _id;
            return this;
        }

        public Builder withAmount(final BigDecimal _amount)
        {
            amount = _amount;
            return this;
        }

        public CollectOrderDto build()
        {
            return new CollectOrderDto(this);
        }
    }
}
