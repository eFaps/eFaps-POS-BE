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

@JsonDeserialize(builder = BalanceSummaryDto.Builder.class)
public class BalanceSummaryDto
{

    private final BalanceDto balance;


    private BalanceSummaryDto(final Builder _builder)
    {
        balance = _builder.balance;
    }

    public BalanceDto getBalance()
    {
        return balance;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private BalanceDto balance;

        public Builder withBalance(final BalanceDto _balance) {
            balance = _balance;
            return this;
        }

        public BalanceSummaryDto build()
        {
            return new BalanceSummaryDto(this);
        }
    }

}
