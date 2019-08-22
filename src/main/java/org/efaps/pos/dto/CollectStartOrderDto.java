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

@JsonDeserialize(builder = CollectStartOrderDto.Builder.class)
public class CollectStartOrderDto
{

    private final BigDecimal amount;

    private CollectStartOrderDto(final Builder _builder)
    {
        amount = _builder.amount;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {

        private BigDecimal amount;

        public Builder withAmount(final BigDecimal _amount)
        {
            amount = _amount;
            return this;
        }

        public CollectStartOrderDto build()
        {
            return new CollectStartOrderDto(this);
        }
    }
}