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
