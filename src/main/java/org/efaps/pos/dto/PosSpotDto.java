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

import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = PosSpotDto.Builder.class)
public class PosSpotDto
{

    private final String id;
    private final String label;

    private PosSpotDto(final Builder _builder)
    {
        id = _builder.id;
        label = _builder.label;
    }

    public String getId()
    {
        return id;
    }

    public String getLabel()
    {
        return label;
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
            id = _id;
            return this;
        }

        public Builder withLabel(final String _label)
        {
            label = _label;
            return this;
        }

        public PosSpotDto build()
        {
            return new PosSpotDto(this);
        }
    }
}
