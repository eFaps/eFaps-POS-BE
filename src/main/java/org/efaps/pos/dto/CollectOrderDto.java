package org.efaps.pos.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;

@JsonDeserialize(builder = CollectOrderDto.Builder.class)
public class CollectOrderDto
{

    private final BigDecimal amount;

    public BigDecimal getAmount()
    {
        return amount;
    }

    private CollectOrderDto(final Builder _builder)
    {
        amount = _builder.amount;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {

        private BigDecimal amount;

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
