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

package org.efaps.pos.entity;

import java.math.BigDecimal;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "collectorders")
public class CollectOrder
{

    @Id
    private String id;

    private BigDecimal amount;

    private Collector collector;

    private State state;

    private BigDecimal collected;

    private String transactionId;

    public String getId()
    {
        return id;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public CollectOrder setAmount(final BigDecimal _amount)
    {
        amount = _amount;
        return this;
    }

    public Collector getCollector()
    {
        return collector;
    }

    public CollectOrder setCollector(final Collector _collector)
    {
        collector = _collector;
        return this;
    }

    public State getState()
    {
        return state;
    }

    public CollectOrder setState(final State _state)
    {
        state = _state;
        return this;
    }

    public BigDecimal getCollected()
    {
        return collected;
    }

    public void setCollected(final BigDecimal collected)
    {
        this.collected = collected;
    }

    public String getTransactionId()
    {
        return transactionId;
    }

    public CollectOrder setTransactionId(final String _transactionId)
    {
        transactionId = _transactionId;
        return this;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this);
    }

    public enum State
    {
        INVALID, PENDING, SUCCESS
    }
}
