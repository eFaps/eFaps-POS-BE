package org.efaps.pos.dto;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = SalesReportDto.Builder.class)
public class SalesReportDto
{

    private final List<SalesReportInfoDto> infos;
    private final List<SalesReportDetailDto> details;
    private final BigDecimal total;

    private SalesReportDto(Builder builder)
    {
        this.infos = builder.infos;
        this.details = builder.details;
        this.total = builder.total;
    }

    public List<SalesReportInfoDto> getInfos()
    {
        return infos;
    }

    public List<SalesReportDetailDto> getDetails()
    {
        return details;
    }

    public BigDecimal getTotal()
    {
        return total;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private List<SalesReportInfoDto> infos = Collections.emptyList();
        private List<SalesReportDetailDto> details = Collections.emptyList();
        private BigDecimal total;

        private Builder()
        {
        }

        public Builder withInfos(List<SalesReportInfoDto> infos)
        {
            this.infos = infos;
            return this;
        }

        public Builder withDetails(List<SalesReportDetailDto> details)
        {
            this.details = details;
            return this;
        }

        public Builder withTotal(BigDecimal total)
        {
            this.total = total;
            return this;
        }

        public SalesReportDto build()
        {
            return new SalesReportDto(this);
        }
    }

}
