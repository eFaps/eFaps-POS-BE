package org.efaps.pos.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = AuthenticationRefreshDto.Builder.class)
public class AuthenticationRefreshDto
{

    private final String refreshToken;

    private AuthenticationRefreshDto(final Builder _builder)
    {
        refreshToken = _builder.refreshToken;
    }

    public String getRefreshToken()
    {
        return refreshToken;
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

        private String refreshToken;

        public Builder withRefreshToken(final String _refreshToken)
        {
            refreshToken = _refreshToken;
            return this;
        }

        public AuthenticationRefreshDto build()
        {
            return new AuthenticationRefreshDto(this);
        }
    }
}
