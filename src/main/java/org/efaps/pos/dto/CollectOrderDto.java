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

import java.math.BigDecimal;

import org.efaps.pos.entity.CollectOrder.State;

@JsonDeserialize(builder = CollectOrderDto.Builder.class)
public class CollectOrderDto
{

    private final String id;

    private final BigDecimal amount;

    private final State state;

    private final BigDecimal collected;

    private CollectOrderDto(final Builder _builder)
    {
        id = _builder.id;
        amount = _builder.amount;
        state = _builder.state;
        collected = _builder.collected;
    }

    public String getId()
    {
        return id;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public State getState()
    {
        return state;
    }

    public BigDecimal getCollected()
    {
        return collected;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {

        private String id;
        private BigDecimal amount;
        private State state;
        private BigDecimal collected;

        public Builder withId(final String _id)
        {
            id = _id;
            return this;
        }

        public Builder withAmount(final BigDecimal _amount)
        {
            amount = _amount;
            return this;
        }

        public Builder withState(final State _state)
        {
            state = _state;
            return this;
        }

        public Builder withCollected(final BigDecimal _collected)
        {
            collected = _collected;
            return this;
        }

        public CollectOrderDto build()
        {
            return new CollectOrderDto(this);
        }
    }
}
