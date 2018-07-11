/*
 * Copyright 2003 - 2018 The eFaps Team
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

import java.time.LocalDate;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.efaps.pos.dto.BalanceStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "balance")
public class Balance
{

    @Id
    private String id;
    private String oid;
    private String number;
    private String userOid;
    private LocalDate startAt;
    private LocalDate endAt;
    private BalanceStatus status;

    public String getId()
    {
        return this.id;
    }

    public Balance setId(final String _id)
    {
        this.id = _id;
        return this;
    }

    public String getOid()
    {
        return this.oid;
    }

    public Balance setOid(final String _oid)
    {
        this.oid = _oid;
        return this;
    }

    public String getNumber()
    {
        return this.number;
    }

    public Balance setNumber(final String _number)
    {
        this.number = _number;
        return this;
    }

    public LocalDate getStartAt()
    {
        return this.startAt;
    }

    public Balance setStartAt(final LocalDate _startAt)
    {
        this.startAt = _startAt;
        return this;
    }

    public LocalDate getEndAt()
    {
        return this.endAt;
    }

    public Balance setEndAt(final LocalDate _endAt)
    {
        this.endAt = _endAt;
        return this;
    }

    public String getUserOid()
    {
        return this.userOid;
    }

    public Balance setUserOid(final String _userOid)
    {
        this.userOid = _userOid;
        return this;
    }

    public BalanceStatus getStatus()
    {
        return this.status;
    }

    public Balance setStatus(final BalanceStatus _status)
    {
        this.status = _status;
        return this;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this);
    }
}
