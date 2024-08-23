package org.efaps.pos.dto;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = DumpDto.Builder.class)
public class DumpDto
{

    private final String oid;
    private final OffsetDateTime updateAt;

    private DumpDto(Builder builder)
    {
        this.oid = builder.oid;
        this.updateAt = builder.updateAt;
    }

    public String getOid()
    {
        return oid;
    }

    public OffsetDateTime getUpdateAt()
    {
        return updateAt;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private String oid;
        private OffsetDateTime updateAt;

        private Builder()
        {
        }

        public Builder withOid(String oid)
        {
            this.oid = oid;
            return this;
        }

        public Builder withUpdateAt(OffsetDateTime updateAt)
        {
            this.updateAt = updateAt;
            return this;
        }

        public DumpDto build()
        {
            return new DumpDto(this);
        }
    }
}
