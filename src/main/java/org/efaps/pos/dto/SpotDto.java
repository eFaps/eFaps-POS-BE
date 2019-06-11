/*
 * Copyright 2003 - 2019 The eFaps Team
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

    private final String id;
    private final String label;

    private SpotDto(final Builder _builder)
    {
        this.id = _builder.id;
        this.label = _builder.label;
    }

    public String getId()
    {
        return this.id;
    }

    public String getLabel()
    {
        return this.label;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {

        private String id;
        private String label;

        public Builder withId(final String _id)
        {
            this.id = _id;
            return this;
        }

        public Builder withLabel(final String _label)
        {
            this.label = _label;
            return this;
        }

        public SpotDto build()
        {
            return new SpotDto(this);
        }
    }
}
