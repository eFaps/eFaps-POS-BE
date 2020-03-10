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

import java.time.LocalDateTime;

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
    private String key;
    private String userOid;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private BalanceStatus status;
    private boolean synced;

    public String getId()
    {
        return id;
    }

    public Balance setId(final String _id)
    {
        id = _id;
        return this;
    }

    public String getOid()
    {
        return oid;
    }

    public Balance setOid(final String _oid)
    {
        oid = _oid;
        return this;
    }

    public String getNumber()
    {
        return number;
    }

    public Balance setNumber(final String _number)
    {
        number = _number;
        return this;
    }

    public String getKey()
    {
        return key;
    }

    public Balance setKey(final String _key)
    {
        key = _key;
        return this;
    }

    public LocalDateTime getStartAt()
    {
        return startAt;
    }

    public Balance setStartAt(final LocalDateTime _startAt)
    {
        startAt = _startAt;
        return this;
    }

    public LocalDateTime getEndAt()
    {
        return endAt;
    }

    public Balance setEndAt(final LocalDateTime _endAt)
    {
        endAt = _endAt;
        return this;
    }

    public String getUserOid()
    {
        return userOid;
    }

    public Balance setUserOid(final String _userOid)
    {
        userOid = _userOid;
        return this;
    }

    public BalanceStatus getStatus()
    {
        return status;
    }

    public Balance setStatus(final BalanceStatus _status)
    {
        status = _status;
        return this;
    }

    public boolean isSynced()
    {
        return synced;
    }

    public Balance setSynced(final boolean _synced)
    {
        synced = _synced;
        return this;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this);
    }
}
