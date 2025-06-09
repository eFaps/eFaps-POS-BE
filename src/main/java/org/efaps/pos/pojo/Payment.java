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

import org.efaps.pos.dto.Currency;
import org.efaps.pos.dto.PaymentType;

@Deprecated
public class Payment implements IPayment
{

    private String oid;
    private PaymentType type;
    private BigDecimal amount;
    private Long cardTypeId;
    private String cardLabel;
    private String mappingKey;
    private Currency currency;
    private BigDecimal exchangeRate;
    private String collectOrderId;

    public String getOid()
    {
        return oid;
    }

    public Payment setOid(final String _oid)
    {
        oid = _oid;
        return this;
    }

    @Override
    public PaymentType getType()
    {
        return type;
    }

    public Payment setType(final PaymentType _type)
    {
        type = _type;
        return this;
    }

    @Override
    public BigDecimal getAmount()
    {
        return amount;
    }

    public Payment setAmount(final BigDecimal _amount)
    {
        amount = _amount;
        return this;
    }

    public Long getCardTypeId()
    {
        return cardTypeId;
    }

    public Payment setCardTypeId(final Long _cardTypeId)
    {
        cardTypeId = _cardTypeId;
        return this;
    }

    public String getCardLabel()
    {
        return cardLabel;
    }

    public Payment setCardLabel(final String _cardLabel)
    {
        cardLabel = _cardLabel;
        return this;
    }

    public String getMappingKey()
    {
        return mappingKey;
    }

    public Payment setMappingKey(final String _mappingKey)
    {
        mappingKey = _mappingKey;
        return this;
    }

    @Override
    public Currency getCurrency()
    {
        return currency;
    }

    public Payment setCurrency(final Currency currency)
    {
        this.currency = currency;
        return this;
    }

    public BigDecimal getExchangeRate()
    {
        return exchangeRate;
    }

    public Payment setExchangeRate(final BigDecimal exchangeRate)
    {
        this.exchangeRate = exchangeRate;
        return this;
    }

    @Override
    public String getCollectOrderId() {
      return collectOrderId;
    }

    public Payment setCollectOrderId(String collectOrderId) {
      this.collectOrderId = collectOrderId;
      return this;
    }

    @Override
    public String getLabel()
    {
        return getCardLabel();
    }

    @Override
    public int getIndex()
    {
        // TODO Auto-generated method stub
        return 0;
    }
}
