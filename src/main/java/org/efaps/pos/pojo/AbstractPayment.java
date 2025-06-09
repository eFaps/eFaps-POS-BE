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
package org.efaps.pos.pojo;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import org.efaps.pos.dto.Currency;
import org.efaps.pos.dto.PaymentType;

public abstract class AbstractPayment
    implements IPayment
{

    private String oid;
    private int index;
    private PaymentType type;
    private BigDecimal amount;
    private Currency currency;
    private String collectOrderId;
    private BigDecimal exchangeRate;

    private String info;
    private OffsetDateTime operationDateTime;

    @Override
    public int getIndex()
    {
        return index;
    }

    public AbstractPayment setIndex(int index)
    {
        this.index = index;
        return this;
    }

    public BigDecimal getExchangeRate()
    {
        return exchangeRate;
    }

    public AbstractPayment setExchangeRate(BigDecimal exchangeRate)
    {
        this.exchangeRate = exchangeRate;
        return this;
    }

    public String getInfo()
    {
        return info;
    }

    public AbstractPayment setInfo(String info)
    {
        this.info = info;
        return this;
    }

    public OffsetDateTime getOperationDateTime()
    {
        return operationDateTime;
    }

    public AbstractPayment setOperationDateTime(OffsetDateTime operationDateTime)
    {
        this.operationDateTime = operationDateTime;
        return this;
    }

    @Override
    public Currency getCurrency()
    {
        return currency;
    }

    public AbstractPayment setCurrency(Currency currency)
    {
        this.currency = currency;
        return this;
    }

    public String getOid()
    {
        return oid;
    }

    public AbstractPayment setOid(String oid)
    {
        this.oid = oid;
        return this;
    }

    @Override
    public PaymentType getType()
    {
        return type;
    }

    public AbstractPayment setType(PaymentType type)
    {
        this.type = type;
        return this;
    }

    @Override
    public String getCollectOrderId()
    {
        return collectOrderId;
    }

    public AbstractPayment setCollectOrderId(String collectOrderId)
    {
        this.collectOrderId = collectOrderId;
        return this;
    }

    @Override
    public BigDecimal getAmount()
    {
        return amount;
    }

    public AbstractPayment setAmount(BigDecimal amount)
    {
        this.amount = amount;
        return this;
    }
}
