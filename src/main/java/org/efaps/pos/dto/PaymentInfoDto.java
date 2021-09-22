package org.efaps.pos.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = PaymentInfoDto.Builder.class)
public class PaymentInfoDto
{

    private final PaymentType type;
    private final Long cardTypeId;
    private final String cardLabel;
    private final int count;
    private final BigDecimal amount;
    private final Currency currency;

    private PaymentInfoDto(final Builder _builder)
    {
        type = _builder.type;
        cardTypeId = _builder.cardTypeId == null ? 0 : _builder.cardTypeId;
        cardLabel = _builder.cardLabel == null ? "" : _builder.cardLabel;
        count = _builder.count;
        amount = _builder.amount;
        currency = _builder.currency;
    }

    public PaymentType getType()
    {
        return type;
    }

    public Long getCardTypeId()
    {
        return cardTypeId;
    }

    public String getCardLabel()
    {
        return cardLabel;
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
        private Long cardTypeId;
        private String cardLabel;
        private int count;
        private BigDecimal amount;
        private Currency currency;

        public Builder withType(final PaymentType _type)
        {
            type = _type;
            return this;
        }

        public Builder withCardTypeId(final Long _cardTypeId)
        {
            cardTypeId = _cardTypeId;
            return this;
        }

        public Builder withCardLabel(final String _cardLabel)
        {
            cardLabel = _cardLabel;
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
