package org.efaps.pos.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ValidateForCreditNoteDto.Builder.class)
public class ValidateForCreditNoteDto
{
    private final String payableOid;

    private ValidateForCreditNoteDto(Builder builder)
    {
        this.payableOid = builder.payableOid;
    }

    public String getPayableOid()
    {
        return payableOid;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private String payableOid;

        private Builder()
        {
        }

        public Builder withPayableOid(String payableOid)
        {
            this.payableOid = payableOid;
            return this;
        }

        public ValidateForCreditNoteDto build()
        {
            return new ValidateForCreditNoteDto(this);
        }
    }



}
