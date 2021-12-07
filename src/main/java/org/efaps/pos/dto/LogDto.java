package org.efaps.pos.dto;

import org.springframework.boot.logging.LogLevel;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = LogDto.Builder.class)
public class LogDto
{

    private final String key;

    private final String value;

    private final LogLevel level;

    private LogDto(final Builder builder)
    {
        key = builder.key;
        value = builder.value;
        level = builder.level;
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }

    public LogLevel getLevel()
    {
        return level;
    }

    /**
     * Creates builder to build {@link LogDto}.
     *
     * @return created builder
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to build {@link LogDto}.
     */
    public static final class Builder
    {

        private String key;
        private String value;
        private LogLevel level;

        private Builder()
        {
        }

        public Builder withKey(final String key)
        {
            this.key = key;
            return this;
        }

        public Builder withValue(final String value)
        {
            this.value = value;
            return this;
        }

        public Builder withLevel(final LogLevel level)
        {
            this.level = level;
            return this;
        }

        public LogDto build()
        {
            return new LogDto(this);
        }
    }
}
