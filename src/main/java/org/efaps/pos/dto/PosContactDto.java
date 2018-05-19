/*
 * Copyright 2003 - 2018 The eFaps Team
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

@JsonDeserialize(builder = PosContactDto.Builder.class)
public class PosContactDto
{
    private final String id;
    private final String oid;
    private final String name;
    private final String taxNumber;

    private PosContactDto(final Builder _builder)
    {
        this.id = _builder.id;
        this.oid = _builder.oid;
        this.name = _builder.name;
        this.taxNumber = _builder.taxNumber;
    }

    public String getId()
    {
        return this.id;
    }

    public String getOid()
    {
        return this.oid;
    }

    public String getName()
    {
        return this.name;
    }

    public String getTaxNumber()
    {
        return this.taxNumber;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private String id;
        private String oid;
        private String name;
        private String taxNumber;

        public Builder withId(final String _id)
        {
            this.id = _id;
            return this;
        }

        public Builder withOID(final String _oid)
        {
            this.oid = _oid;
            return this;
        }

        public Builder withName(final String _name)
        {
            this.name = _name;
            return this;
        }

        public Builder withTaxNumber(final String _taxNumber)
        {
            this.taxNumber = _taxNumber;
            return this;
        }


        public PosContactDto build()
        {
            return new PosContactDto(this);
        }
    }
}
