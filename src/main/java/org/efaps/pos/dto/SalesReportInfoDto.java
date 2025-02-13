package org.efaps.pos.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = SalesReportInfoDto.Builder.class)
public class SalesReportInfoDto
{

    private final PaymentType type;
    private final String label;
    private final int count;
    private final BigDecimal amount;

    private SalesReportInfoDto(Builder builder)
    {
        this.type = builder.type;
        this.label = builder.label;
        this.count = builder.count;
        this.amount = builder.amount;
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

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private PaymentType type;
        private String label;
        private int count;
        private BigDecimal amount;

        private Builder()
        {
        }

        public Builder withType(PaymentType type)
        {
            this.type = type;
            return this;
        }

        public Builder withLabel(String label)
        {
            this.label = label;
            return this;
        }

        public Builder withCount(int count)
        {
            this.count = count;
            return this;
        }

        public Builder withAmount(BigDecimal amount)
        {
            this.amount = amount;
            return this;
        }

        public SalesReportInfoDto build()
        {
            return new SalesReportInfoDto(this);
        }
    }
}
