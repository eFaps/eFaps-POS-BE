/*
 * Copyright © 2003 - 2024 The eFaps Team (-)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.efaps.pos.dto;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = SalesReportDto.Builder.class)
public class SalesReportDto
{

    private final List<SalesReportInfoDto> infos;
    private final List<SalesReportDetailDto> details;
    private final List<MoneyAmountDto> totals;

    private SalesReportDto(Builder builder)
    {
        this.infos = builder.infos;
        this.details = builder.details;
        this.totals = builder.totals;
    }

    public List<SalesReportInfoDto> getInfos()
    {
        return infos;
    }

    public List<SalesReportDetailDto> getDetails()
    {
        return details;
    }

    public List<MoneyAmountDto> getTotals()
    {
        return totals;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private List<SalesReportInfoDto> infos = Collections.emptyList();
        private List<SalesReportDetailDto> details = Collections.emptyList();
        private List<MoneyAmountDto> totals = Collections.emptyList();

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

        public Builder withTotals(List<MoneyAmountDto> totals)
        {
            this.totals = totals;
            return this;
        }

        public SalesReportDto build()
        {
            return new SalesReportDto(this);
        }
    }

}
