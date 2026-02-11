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

import java.util.Collections;
import java.util.Map;

import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = CollectStartResponseDto.Builder.class)
public class CollectStartResponseDto
{
    private final String collectOrderId;

    private final Map<String, Object> details;

    private CollectStartResponseDto(final Builder builder)
    {
        collectOrderId = builder.collectOrderId;
        details = builder.details;
    }

    public String getCollectOrderId()
    {
        return collectOrderId;
    }

    public Map<String, Object> getDetails()
    {
        return details;
    }

    /**
     * Creates builder to build {@link CollectStartResponseDto}.
     * @return created builder
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to build {@link CollectStartResponseDto}.
     */
    public static final class Builder
    {

        private String collectOrderId;
        private Map<String, Object> details = Collections.emptyMap();

        private Builder()
        {
        }

        public Builder withCollectOrderId(final String collectOrderId)
        {
            this.collectOrderId = collectOrderId;
            return this;
        }

        public Builder withDetails(final Map<String, Object> details)
        {
            this.details = details;
            return this;
        }

        public CollectStartResponseDto build()
        {
            return new CollectStartResponseDto(this);
        }
    }
}
