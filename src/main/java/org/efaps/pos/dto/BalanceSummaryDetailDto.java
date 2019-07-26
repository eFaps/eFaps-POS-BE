package org.efaps.pos.dto;

import java.math.BigDecimal;
import java.util.Collection;

public class BalanceSummaryDetailDto
{

    private final int documentCount;
    private final int paymentCount;
    private final BigDecimal netTotal;
    private final BigDecimal crossTotal;
    private final Collection<PaymentInfoDto> paymentInfos;

    private BalanceSummaryDetailDto(final Builder _builder)
    {
        documentCount = _builder.documentCount;
        paymentCount = _builder.paymentCount;
        netTotal = _builder.netTotal == null ? BigDecimal.ZERO : _builder.netTotal;
        crossTotal = _builder.crossTotal == null ? BigDecimal.ZERO : _builder.crossTotal;
        paymentInfos = _builder.paymentInfos;
    }

    public int getDocumentCount()
    {
        return documentCount;
    }

    public int getPaymentCount()
    {
        return paymentCount;
    }

    public BigDecimal getNetTotal()
    {
        return netTotal;
    }

    public BigDecimal getCrossTotal()
    {
        return crossTotal;
    }

    public Collection<PaymentInfoDto> getPaymentInfos()
    {
        return paymentInfos;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {

        private int documentCount;
        private int paymentCount;
        private BigDecimal netTotal;
        private BigDecimal crossTotal;
        private Collection<PaymentInfoDto> paymentInfos;

        public Builder withDocumentCount(final int _documentCount)
        {
            documentCount = _documentCount;
            return this;
        }

        public Builder withPaymentCount(final int _paymentCount)
        {
            paymentCount = _paymentCount;
            return this;
        }

        public Builder withCrossTotal(final BigDecimal _crossTotal)
        {
            crossTotal = _crossTotal;
            return this;
        }

        public Builder withNetTotal(final BigDecimal _netTotal)
        {
            netTotal = _netTotal;
            return this;
        }

        public Builder withPayments(final Collection<PaymentInfoDto> _paymentInfos)
        {
            paymentInfos = _paymentInfos;
            return this;
        }

        public BalanceSummaryDetailDto build()
        {
            return new BalanceSummaryDetailDto(this);
        }

    }
}
