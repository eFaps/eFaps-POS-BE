/*
 * Copyright 2003 - 2018 The eFaps Team
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
 *
 */
package org.efaps.pos.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = SpotDto.Builder.class)
public class SpotDto
{

    private final String name;

    private SpotDto(final Builder _builder)
    {
        this.name = _builder.name;

    }

    public String getName()
    {
        return this.name;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {

        private String name;

        public Builder withName(final String _name)
        {
            this.name = _name;
            return this;
        }

        public SpotDto build()
        {
            return new SpotDto(this);
        }
    }
}
