package org.efaps.pos.dto;

import java.util.Collection;
import java.util.Collections;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = SalesReportDetailDto.Builder.class)
public class SalesReportDetailDto
{
    private final PaymentType type;
    private final String label;
    private final Collection<SalesReportEntryDto> entries;
    private SalesReportDetailDto(Builder builder)
    {
        this.type = builder.type;
        this.label = builder.label;
        this.entries = builder.entries;
    }
    public PaymentType getType()
    {
        return type;
    }
    public String getLabel()
    {
        return label;
    }
    public Collection<SalesReportEntryDto> getEntries()
    {
        return entries;
    }
    public static Builder builder()
    {
        return new Builder();
    }
    public static final class Builder
    {

        private PaymentType type;
        private String label;
        private Collection<SalesReportEntryDto> entries = Collections.emptyList();

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

        public Builder withEntries(Collection<SalesReportEntryDto> entries)
        {
            this.entries = entries;
            return this;
        }

        public SalesReportDetailDto build()
        {
            return new SalesReportDetailDto(this);
        }
    }
}
