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

import java.math.BigDecimal;
import java.util.Map;

import org.efaps.pos.entity.CollectOrder.State;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = CollectOrderDto.Builder.class)
public class CollectOrderDto
{

    private final String id;

    private final BigDecimal amount;

    private final Currency currency;

    private final State state;

    private final BigDecimal collected;

    private final String orderId;

    private final String collectorKey;

    private final Map<String, Object> additionalInfo;

    private CollectOrderDto(final Builder builder)
    {
        id = builder.id;
        amount = builder.amount;
        currency = builder.currency;
        state = builder.state;
        collected = builder.collected;
        orderId = builder.orderId;
        collectorKey = builder.collectorKey;
        additionalInfo = builder.additionalInfo;
    }

    public String getId()
    {
        return id;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public Currency getCurrency()
    {
        return currency;
    }

    public State getState()
    {
        return state;
    }

    public BigDecimal getCollected()
    {
        return collected;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public String getCollectorKey()
    {
        return collectorKey;
    }

    public Map<String, Object> getAdditionalInfo()
    {
        return additionalInfo;
    }

    /**
     * Creates builder to build {@link CollectOrderDto}.
     *
     * @return created builder
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to build {@link CollectOrderDto}.
     */
    public static final class Builder
    {

        private String id;
        private BigDecimal amount;
        private Currency currency;
        private State state;
        private BigDecimal collected;
        private String orderId;
        private String collectorKey;
        private Map<String, Object> additionalInfo;

        private Builder()
        {
        }

        public Builder withId(final String id)
        {
            this.id = id;
            return this;
        }

        public Builder withAmount(final BigDecimal amount)
        {
            this.amount = amount;
            return this;
        }

        public Builder withCurrency(final Currency currency)
        {
            this.currency = currency;
            return this;
        }

        public Builder withState(final State state)
        {
            this.state = state;
            return this;
        }

        public Builder withCollected(final BigDecimal collected)
        {
            this.collected = collected;
            return this;
        }

        public Builder withOrderId(final String orderId)
        {
            this.orderId = orderId;
            return this;
        }

        public Builder withCollectorKey(final String collectorKey)
        {
            this.collectorKey = collectorKey;
            return this;
        }

        public Builder withAdditionalInfo(final Map<String, Object> additionalInfo)
        {
            this.additionalInfo = additionalInfo;
            return this;
        }

        public CollectOrderDto build()
        {
            return new CollectOrderDto(this);
        }
    }
}
