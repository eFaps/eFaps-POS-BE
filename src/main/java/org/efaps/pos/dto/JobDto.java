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

import java.util.Collections;
import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = JobDto.Builder.class)
public class JobDto
{

    private final String id;
    private final String documentId;
    private final String documentNumber;
    private final String spotNumber;
    private final String shoutout;

    private final Set<PosDocItemDto> items;

    private JobDto(final Builder builder)
    {
        id = builder.id;
        documentId = builder.documentId;
        documentNumber = builder.documentNumber;
        spotNumber = builder.spotNumber;
        shoutout = builder.shoutout;
        items = builder.items;
    }

    public String getId()
    {
        return id;
    }

    public String getDocumentId()
    {
        return documentId;
    }

    public Set<PosDocItemDto> getItems()
    {
        return items;
    }

    public String getDocumentNumber()
    {
        return documentNumber;
    }

    public String getSpotNumber()
    {
        return spotNumber;
    }

    public String getShoutout()
    {
        return shoutout;
    }

    /**
     * Creates builder to build {@link JobDto}.
     *
     * @return created builder
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to build {@link JobDto}.
     */
    public static final class Builder
    {

        private String id;
        private String documentId;
        private String documentNumber;
        private String spotNumber;
        private String shoutout;
        private Set<PosDocItemDto> items = Collections.emptySet();

        private Builder()
        {
        }

        public Builder withId(final String id)
        {
            this.id = id;
            return this;
        }

        public Builder withDocumentId(final String documentId)
        {
            this.documentId = documentId;
            return this;
        }

        public Builder withDocumentNumber(final String documentNumber)
        {
            this.documentNumber = documentNumber;
            return this;
        }

        public Builder withSpotNumber(final String spotNumber)
        {
            this.spotNumber = spotNumber;
            return this;
        }

        public Builder withShoutout(final String shoutout)
        {
            this.shoutout = shoutout;
            return this;
        }

        public Builder withItems(final Set<PosDocItemDto> items)
        {
            this.items = items;
            return this;
        }

        public JobDto build()
        {
            return new JobDto(this);
        }
    }
}
