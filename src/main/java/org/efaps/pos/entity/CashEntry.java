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
package org.efaps.pos.entity;

import java.math.BigDecimal;
import java.time.Instant;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.efaps.pos.dto.CashEntryType;
import org.efaps.pos.dto.Currency;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "cashentries")
public class CashEntry
{

    @Id
    private String id;
    private String balanceOid;
    private CashEntryType entryType;
    private BigDecimal amount;
    private Currency currency;
    private String description;

    @CreatedBy
    private String user;

    @CreatedDate
    private Instant createdDate;

    @LastModifiedBy
    private String lastModifiedUser;

    @LastModifiedDate
    private Instant lastModifiedDate;

    @Version
    private long version;

    public String getBalanceOid()
    {
        return balanceOid;
    }

    public CashEntry setBalanceOid(final String balanceOid)
    {
        this.balanceOid = balanceOid;
        return this;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public CashEntry setAmount(final BigDecimal amount)
    {
        this.amount = amount;
        return this;
    }

    public Currency getCurrency()
    {
        return currency;
    }

    public CashEntry setCurrency(final Currency currency)
    {
        this.currency = currency;
        return this;
    }

    public String getDescription()
    {
        return description;
    }

    public CashEntry setDescription(String description)
    {
        this.description = description;
        return this;
    }

    public String getId()
    {
        return id;
    }

    public String getUser()
    {
        return user;
    }

    public Instant getCreatedDate()
    {
        return createdDate;
    }

    public String getLastModifiedUser()
    {
        return lastModifiedUser;
    }

    public Instant getLastModifiedDate()
    {
        return lastModifiedDate;
    }

    public long getVersion()
    {
        return version;
    }

    public CashEntryType getEntryType()
    {
        return entryType;
    }

    public CashEntry setEntryType(final CashEntryType entryType)
    {
        this.entryType = entryType;
        return this;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }
}
