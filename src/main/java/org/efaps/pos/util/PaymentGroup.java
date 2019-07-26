package org.efaps.pos.util;

import java.math.BigDecimal;

import org.efaps.pos.dto.PaymentInfoDto;
import org.efaps.pos.dto.PaymentType;

public class PaymentGroup
{

    private final PaymentType type;
    private Long cardTypeId;
    private String cardLabel;

    private PaymentGroup(final Builder _builder)
    {
        type = _builder.type;
        cardTypeId = _builder.cardTypeId == null ? 0 : _builder.cardTypeId;
        cardLabel = _builder.cardLabel == null ? "" : _builder.cardLabel;
    }

    public PaymentType getType()
    {
        return type;
    }

    public Long getCardTypeId()
    {
        return cardTypeId;
    }

    public void setCardTypeId(final Long cardTypeId)
    {
        this.cardTypeId = cardTypeId;
    }

    public String getCardLabel()
    {
        return cardLabel;
    }

    public void setCardLabel(final String cardLabel)
    {
        this.cardLabel = cardLabel;
    }

    public PaymentInfoDto getInfo(final int _count, final BigDecimal _amount) {
        return PaymentInfoDto.builder()
                        .withCount(_count)
                        .withAmount(_amount)
                        .withCardLabel(cardLabel)
                        .withCardTypeId(cardTypeId)
                        .withType(type)
                        .build();
    }

    @Override
    public boolean equals(final Object _obj)
    {
        boolean ret = false;
        if (_obj instanceof PaymentGroup) {
            final PaymentGroup other = (PaymentGroup) _obj;
            ret = other.type.equals(type) && other.cardTypeId.equals(cardTypeId);
        } else {
            ret = super.equals(_obj);
        }
        return ret;
    }

    @Override
    public int hashCode()
    {
        return type.hashCode() + cardTypeId.hashCode();
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

        public PaymentGroup build()
        {
            return new PaymentGroup(this);
        }
    }
}
