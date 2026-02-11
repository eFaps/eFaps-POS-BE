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

@JsonDeserialize(builder = HealthDto.Builder.class)
public class HealthDto
{

    private final HealthStatus status;

    private HealthDto(final Builder builder)
    {
        status = builder.status;
    }

    public HealthStatus getStatus()
    {
        return status;
    }

    /**
     * Creates builder to build {@link HealthDto}.
     *
     * @return created builder
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to build {@link HealthDto}.
     */
    public static final class Builder
    {

        private HealthStatus status;

        private Builder()
        {
        }

        public Builder withStatus(final HealthStatus status)
        {
            this.status = status;
            return this;
        }

        public HealthDto build()
        {
            return new HealthDto(this);
        }
    }
}
