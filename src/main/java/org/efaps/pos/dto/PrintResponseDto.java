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

@JsonDeserialize(builder = PrintResponseDto.Builder.class)
public class PrintResponseDto
{
    private final String key;
    private final PrinterDto printer;

    private PrintResponseDto(final Builder _builder)
    {
        this.key = _builder.key;
        this.printer = _builder.printer;
    }

    public String getKey()
    {
        return this.key;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {

        private String key;
        private PrinterDto printer;

        public Builder withKey(final String _key)
        {
            this.key = _key;
            return this;
        }

        public Builder withPrinter(final PrinterDto _printer)
        {
            this.printer = _printer;
            return this;
        }

        public PrintResponseDto build()
        {
            return new PrintResponseDto(this);
        }
    }


    public PrinterDto getPrinter()
    {
        return this.printer;
    }
}
