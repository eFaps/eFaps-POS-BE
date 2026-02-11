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

import java.time.OffsetDateTime;

import org.apache.commons.lang3.builder.ToStringBuilder;

import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = PromotionHeaderDto.Builder.class)
public class PromotionHeaderDto
{

    private final String oid;

    private final String name;

    private final String description;

    private final String label;

    private final int priority;

    private final OffsetDateTime startDateTime;

    private final OffsetDateTime endDateTime;

    private PromotionHeaderDto(Builder builder)
    {
        this.oid = builder.oid;
        this.name = builder.name;
        this.description = builder.description;
        this.label = builder.label;
        this.priority = builder.priority;
        this.startDateTime = builder.startDateTime;
        this.endDateTime = builder.endDateTime;
    }

    public String getOid()
    {
        return oid;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public String getLabel()
    {
        return label;
    }

    public int getPriority()
    {
        return priority;
    }

    public OffsetDateTime getStartDateTime()
    {
        return startDateTime;
    }

    public OffsetDateTime getEndDateTime()
    {
        return endDateTime;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private String oid;
        private String name;
        private String description;
        private String label;
        private int priority;
        private OffsetDateTime startDateTime;
        private OffsetDateTime endDateTime;

        private Builder()
        {
        }

        public Builder withOid(String oid)
        {
            this.oid = oid;
            return this;
        }

        public Builder withName(String name)
        {
            this.name = name;
            return this;
        }

        public Builder withDescription(String description)
        {
            this.description = description;
            return this;
        }

        public Builder withLabel(String label)
        {
            this.label = label;
            return this;
        }

        public Builder withPriority(int priority)
        {
            this.priority = priority;
            return this;
        }

        public Builder withStartDateTime(OffsetDateTime startDateTime)
        {
            this.startDateTime = startDateTime;
            return this;
        }

        public Builder withEndDateTime(OffsetDateTime endDateTime)
        {
            this.endDateTime = endDateTime;
            return this;
        }

        public PromotionHeaderDto build()
        {
            return new PromotionHeaderDto(this);
        }
    }
}
