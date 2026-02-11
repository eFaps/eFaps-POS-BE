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

import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = PosUserDto.Builder.class)
public class PosUserDto
{

    private final String username;
    private final String employeeOid;
    private final String firstName;
    private final String surName;

    private PosUserDto(final Builder _builder)
    {
        this.username = _builder.username;
        this.employeeOid = _builder.employeeOid;
        this.firstName = _builder.firstName;
        this.surName = _builder.surName;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public String getUsername()
    {
        return this.username;
    }

    public String getEmployeeOid()
    {
        return employeeOid;
    }

    public String getFirstName()
    {
        return this.firstName;
    }

    public String getSurName()
    {
        return this.surName;
    }

    /**
     * Builder to build {@link AgendaDto}.
     */
    public static final class Builder
    {

        private String username;
        private String employeeOid;
        private String firstName;
        private String surName;

        public Builder withUsername(final String _username)
        {
            this.username = _username;
            return this;
        }

        public Builder withEmployeeOid(final String employeeOid)
        {
            this.employeeOid = employeeOid;
            return this;
        }

        public Builder withFirstName(final String _firstName)
        {
            this.firstName = _firstName;
            return this;
        }

        public Builder withSurName(final String _surName)
        {
            this.surName = _surName;
            return this;
        }

        public PosUserDto build()
        {
            return new PosUserDto(this);
        }
    }
}
