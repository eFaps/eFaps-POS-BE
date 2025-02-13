package org.efaps.pos.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = SalesReportEntryDto.Builder.class)
public class SalesReportEntryDto
{

    private final IPosPaymentDto payment;

    private final AbstractPayableDocumentDto payable;

    private SalesReportEntryDto(Builder builder)
    {
        this.payment = builder.payment;
        this.payable = builder.payable;
    }

    public IPosPaymentDto getPayment()
    {
        return payment;
    }

    public AbstractPayableDocumentDto getPayable()
    {
        return payable;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private IPosPaymentDto payment;
        private AbstractPayableDocumentDto payable;

        private Builder()
        {
        }

        public Builder withPayment(IPosPaymentDto payment)
        {
            this.payment = payment;
            return this;
        }

        public Builder withPayable(AbstractPayableDocumentDto payable)
        {
            this.payable = payable;
            return this;
        }

        public SalesReportEntryDto build()
        {
            return new SalesReportEntryDto(this);
        }
    }
}
