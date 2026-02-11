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

/**
 * The Class AuthenticationRequestDto.
 */
@JsonDeserialize(builder = AuthenticationRequestDto.Builder.class)
public class AuthenticationRequestDto
{
    private final String userName;
    private final String password;

    private AuthenticationRequestDto(final Builder _builder)
    {
        this.userName = _builder.userName;
        this.password = _builder.password;
    }

    public String getUserName()
    {
        return this.userName;
    }

    public String getPassword()
    {
        return this.password;
    }

    /**
     * Creates builder to build {@link AgendaDto}.
     *
     * @return created builder
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to build {@link AgendaDto}.
     */
    public static final class Builder
    {
        private String userName;
        private String password;

        public Builder withUserName(final String _userName) {
            this.userName = _userName;
            return this;
        }

        public Builder withPassword(final String _password) {
            this.password = _password;
            return this;
        }

        public AuthenticationRequestDto build()
        {
            return new AuthenticationRequestDto(this);
        }
    }
}
