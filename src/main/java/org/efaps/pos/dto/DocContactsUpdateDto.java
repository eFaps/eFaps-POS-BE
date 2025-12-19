package org.efaps.pos.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jakarta.annotation.Generated;

@JsonDeserialize(builder = DocContactsUpdateDto.Builder.class)
public class DocContactsUpdateDto
{

    private final String contactIdent;

    private final String loyaltyContactIdent;

    @Generated("SparkTools")
    private DocContactsUpdateDto(Builder builder)
    {
        this.contactIdent = builder.contactIdent;
        this.loyaltyContactIdent = builder.loyaltyContactIdent;
    }

    public String getContactIdent()
    {
        return contactIdent;
    }

    public String getLoyaltyContactIdent()
    {
        return loyaltyContactIdent;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    @Generated("SparkTools")
    public static Builder builder()
    {
        return new Builder();
    }

    @Generated("SparkTools")
    public static final class Builder
    {

        private String contactIdent;
        private String loyaltyContactIdent;

        private Builder()
        {
        }

        public Builder withContactIdent(String contactIdent)
        {
            this.contactIdent = contactIdent;
            return this;
        }

        public Builder withLoyaltyContactIdent(String loyaltyContactIdent)
        {
            this.loyaltyContactIdent = loyaltyContactIdent;
            return this;
        }

        public DocContactsUpdateDto build()
        {
            return new DocContactsUpdateDto(this);
        }
    }
}
