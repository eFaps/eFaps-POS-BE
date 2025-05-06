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
