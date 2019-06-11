/*
 * Copyright 2003 - 2019 The eFaps Team
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
 *
 */
package org.efaps.pos.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Set;

@JsonDeserialize(builder = JobDto.Builder.class)
public class JobDto
{

    private final String id;
    private final String documentId;
    private final String documentNumber;
    private final String spotNumber;

    private final Set<PosDocItemDto> items;

    private JobDto(final Builder _builder)
    {
        this.id = _builder.id;
        this.documentId = _builder.documentId;
        this.items = _builder.items;
        this.documentNumber = _builder.documentNumber;
        this.spotNumber = _builder.spotNumber;
    }

    public String getId()
    {
        return this.id;
    }

    public String getDocumentId()
    {
        return this.documentId;
    }

    public Set<PosDocItemDto> getItems()
    {
        return this.items;
    }

    public String getDocumentNumber()
    {
        return this.documentNumber;
    }

    public String getSpotNumber()
    {
        return this.spotNumber;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {

        private String id;
        private String documentId;
        private String documentNumber;
        private String spotNumber;
        private Set<PosDocItemDto> items;

        public Builder withId(final String _id)
        {
            this.id = _id;
            return this;
        }

        public Builder withDocumentId(final String _documentId)
        {
            this.documentId = _documentId;
            return this;
        }

        public Builder withDocumentNumber(final String _documentNumber)
        {
            this.documentNumber = _documentNumber;
            return this;
        }

        public Builder withSpotNumber(final String _spotNumber)
        {
            this.spotNumber = _spotNumber;
            return this;
        }

        public Builder withItems(final Set<PosDocItemDto> _items)
        {
            this.items = _items;
            return this;
        }

        public JobDto build()
        {
            return new JobDto(this);
        }
    }
}
