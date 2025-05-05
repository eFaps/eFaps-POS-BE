package org.efaps.pos.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


@JsonDeserialize(builder = ValidateForCreditNoteResponseDto.Builder.class)
public class ValidateForCreditNoteResponseDto
{
    private final boolean valid;

    private ValidateForCreditNoteResponseDto(Builder builder)
    {
        this.valid = builder.valid;
    }

    public boolean isValid()
    {
        return valid;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private boolean valid;

        private Builder()
        {
        }

        public Builder withValid(boolean valid)
        {
            this.valid = valid;
            return this;
        }

        public ValidateForCreditNoteResponseDto build()
        {
            return new ValidateForCreditNoteResponseDto(this);
        }
    }
}
