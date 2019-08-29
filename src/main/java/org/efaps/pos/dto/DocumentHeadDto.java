package org.efaps.pos.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DocumentHeadDto
{

    private final String number;
    private final BigDecimal netTotal;
    private final BigDecimal crossTotal;
    private final String currency;
    private final LocalDate date;
    private final DocStatus status;

    public DocumentHeadDto(final Builder<?, ?> _builder)
    {
        number = _builder.number;
        netTotal = _builder.netTotal;
        crossTotal = _builder.crossTotal;
        currency = _builder.currency;
        date = _builder.date;
        status = _builder.status;
    }

    public String getNumber()
    {
        return number;
    }

    public BigDecimal getNetTotal()
    {
        return netTotal;
    }

    public BigDecimal getCrossTotal()
    {
        return crossTotal;
    }

    public String getCurrency()
    {
        return currency;
    }

    public LocalDate getDate()
    {
        return date;
    }

    public DocStatus getStatus()
    {
        return status;
    }

    public static class Builder<S extends Builder<S, T>, T extends DocumentHeadDto>
    {

        private String number;
        private BigDecimal netTotal;
        private BigDecimal crossTotal;
        private String currency;
        private LocalDate date;
        private DocStatus status;

        @SuppressWarnings("unchecked")
        public S withNumber(final String _number)
        {
            this.number = _number;
            return (S) this;
        }

        @SuppressWarnings("unchecked")
        public S withNetTotal(final BigDecimal _netTotal)
        {
            this.netTotal = _netTotal;
            return (S) this;
        }

        @SuppressWarnings("unchecked")
        public S withCrossTotal(final BigDecimal _crossTotal)
        {
            this.crossTotal = _crossTotal;
            return (S) this;
        }

        @SuppressWarnings("unchecked")
        public S withCurrency(final String _currency)
        {
            currency = _currency;
            return (S) this;
        }

        @SuppressWarnings("unchecked")
        public S withDate(final LocalDate _date)
        {
            date = _date;
            return (S) this;
        }

        @SuppressWarnings("unchecked")
        public S withStatus(final DocStatus _status)
        {
            status = _status;
            return (S) this;
        }

        public DocumentHeadDto build()
        {
            return new DocumentHeadDto(this);
        }
    }
}
