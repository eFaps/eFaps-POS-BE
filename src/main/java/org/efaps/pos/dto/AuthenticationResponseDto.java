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

@JsonDeserialize(builder = AuthenticationResponseDto.Builder.class)
public class AuthenticationResponseDto
{

    private final String accessToken;

    private final String refreshToken;

    private AuthenticationResponseDto(final Builder _builder)
    {
        accessToken = _builder.accessToken;
        refreshToken = _builder.refreshToken;
    }

    public String getAccessToken()
    {
        return accessToken;
    }

    public String getRefreshToken()
    {
        return refreshToken;
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

        private String accessToken;

        private String refreshToken;

        public Builder withAccessToken(final String _accessToken)
        {
            accessToken = _accessToken;
            return this;
        }

        public Builder withRefreshToken(final String _refreshToken)
        {
            refreshToken = _refreshToken;
            return this;
        }

        public AuthenticationResponseDto build()
        {
            return new AuthenticationResponseDto(this);
        }
    }
}
