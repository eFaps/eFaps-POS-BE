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

@JsonDeserialize(builder = AuthenticationResponseDto.Builder.class)
public class AuthenticationResponseDto
{

    private final String token;

    private AuthenticationResponseDto(final Builder _builder)
    {
        this.token = _builder.token;
    }

    public String getToken()
    {
        return this.token;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to build {@link AgendaDto}.
     */
    public static final class Builder
    {

        private String token;

        public Builder withToken(final String _token)
        {
            this.token = _token;
            return this;
        }

        public AuthenticationResponseDto build()
        {
            return new AuthenticationResponseDto(this);
        }
    }
}
