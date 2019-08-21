package org.efaps.pos.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = CollectorDto.Builder.class)
public class CollectorDto
{

    private final String label;
    private final String key;

    public CollectorDto(final Builder _builder)
    {
        label = _builder.label;
        key = _builder.key;
    }

    public String getLabel()
    {
        return label;
    }

    public String getKey()
    {
        return key;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {

        private String label;
        private String key;

        public Builder withLabel(final String _label)
        {
            label = _label;
            return this;
        }

        public Builder withKey(final String _key)
        {
            key = _key;
            return this;
        }

        public CollectorDto build()
        {
            return new CollectorDto(this);
        }
    }
}
