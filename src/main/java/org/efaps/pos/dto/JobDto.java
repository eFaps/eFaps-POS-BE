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
import java.util.Set;

import tools.jackson.databind.annotation.JsonDeserialize;

import jakarta.annotation.Generated;

@JsonDeserialize(builder = JobDto.Builder.class)
public class JobDto
{

    private final String id;
    private final String documentId;
    private final String documentNumber;
    private final String spotNumber;
    private final String shoutout;
    private final String note;
    private final ContactDto contact;

    private final Set<PosDocItemDto> items;

    @Generated("SparkTools")
    private JobDto(Builder builder)
    {
        this.id = builder.id;
        this.documentId = builder.documentId;
        this.documentNumber = builder.documentNumber;
        this.spotNumber = builder.spotNumber;
        this.shoutout = builder.shoutout;
        this.note = builder.note;
        this.contact = builder.contact;
        this.items = builder.items;
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

    public String getNote()
    {
        return note;
    }

    public ContactDto getContact()
    {
        return contact;
    }

    @Generated("SparkTools")
    public static Builder builder()
    {
        return new Builder();
    }

    @Generated("SparkTools")
    public static final class Builder
    {

        private String id;
        private String documentId;
        private String documentNumber;
        private String spotNumber;
        private String shoutout;
        private String note;
        private ContactDto contact;
        private Set<PosDocItemDto> items = Collections.emptySet();

        private Builder()
        {
        }

        public Builder withId(String id)
        {
            this.id = id;
            return this;
        }

        public Builder withDocumentId(String documentId)
        {
            this.documentId = documentId;
            return this;
        }

        public Builder withDocumentNumber(String documentNumber)
        {
            this.documentNumber = documentNumber;
            return this;
        }

        public Builder withSpotNumber(String spotNumber)
        {
            this.spotNumber = spotNumber;
            return this;
        }

        public Builder withShoutout(String shoutout)
        {
            this.shoutout = shoutout;
            return this;
        }

        public Builder withNote(String note)
        {
            this.note = note;
            return this;
        }

        public Builder withContact(ContactDto contact)
        {
            this.contact = contact;
            return this;
        }

        public Builder withItems(Set<PosDocItemDto> items)
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
