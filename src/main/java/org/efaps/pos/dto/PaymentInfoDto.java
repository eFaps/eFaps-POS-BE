package org.efaps.pos.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = PaymentInfoDto.Builder.class)
public class PaymentInfoDto
{

    private final PaymentType type;
    private final String label;
    private final int count;
    private final BigDecimal amount;
    private final Currency currency;

    private PaymentInfoDto(final Builder _builder)
    {
        type = _builder.type;
        label = _builder.label == null ? "" : _builder.label;
        count = _builder.count;
        amount = _builder.amount;
        currency = _builder.currency;
    }

    public PaymentType getType()
    {
        return type;
    }

    public String getLabel()
    {
        return label;
    }

    public int getCount()
    {
        return count;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public Currency getCurrency()
    {
        return currency;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {

        private PaymentType type;
        private String label;
        private int count;
        private BigDecimal amount;
        private Currency currency;

        public Builder withType(final PaymentType _type)
        {
            type = _type;
            return this;
        }

        public Builder withLabel(final String label)
        {
            this.label = label;
            return this;
        }

        public Builder withCount(final int _count)
        {
            count = _count;
            return this;
        }

        public Builder withAmount(final BigDecimal _amount)
        {
            amount = _amount;
            return this;
        }

        public Builder withCurrency(final Currency _currency)
        {
            currency = _currency;
            return this;
        }

        public PaymentInfoDto build()
        {
            return new PaymentInfoDto(this);
        }
    }
}
