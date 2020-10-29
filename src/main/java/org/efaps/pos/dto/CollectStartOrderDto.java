/*
 * Copyright 2003 - 2020 The eFaps Team
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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = CollectStartOrderDto.Builder.class)
public class CollectStartOrderDto
{

    private final BigDecimal amount;

    private final String orderId;

    private final Map<String, Object> details;

    private CollectStartOrderDto(final Builder builder)
    {
        amount = builder.amount;
        orderId = builder.orderId;
        details = builder.details;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public Map<String, Object> getDetails()
    {
        return details;
    }

    /**
     * Creates builder to build {@link CollectStartOrderDto}.
     *
     * @return created builder
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to build {@link CollectStartOrderDto}.
     */
    public static final class Builder
    {

        private BigDecimal amount;
        private String orderId;
        private Map<String, Object> details = Collections.emptyMap();

        private Builder()
        {
        }

        public Builder withAmount(final BigDecimal amount)
        {
            this.amount = amount;
            return this;
        }

        public Builder withOrderId(final String orderId)
        {
            this.orderId = orderId;
            return this;
        }

        public Builder withDetails(final Map<String, Object> details)
        {
            this.details = details;
            return this;
        }

        public CollectStartOrderDto build()
        {
            return new CollectStartOrderDto(this);
        }
    }
}
