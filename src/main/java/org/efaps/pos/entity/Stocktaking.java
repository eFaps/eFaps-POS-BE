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

import java.time.Instant;
import java.time.LocalDateTime;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.efaps.pos.dto.StocktakingStatus;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "stocktakings")
public class Stocktaking
{

    @Id
    private String id;
    private String oid;
    private String userOid;
    private StocktakingStatus status;
    private String number;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String warehouseOid;

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

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getOid()
    {
        return oid;
    }

    public Stocktaking setOid(String oid)
    {
        this.oid = oid;
        return this;
    }

    public String getUserOid()
    {
        return userOid;
    }

    public Stocktaking setUserOid(String userOid)
    {
        this.userOid = userOid;
        return this;
    }

    public StocktakingStatus getStatus()
    {
        return status;
    }

    public Stocktaking setStatus(StocktakingStatus status)
    {
        this.status = status;
        return this;
    }

    public String getNumber()
    {
        return number;
    }

    public Stocktaking setNumber(String number)
    {
        this.number = number;
        return this;
    }

    public String getWarehouseOid()
    {
        return warehouseOid;
    }

    public Stocktaking setWarehouseOid(String warehouseOid)
    {
        this.warehouseOid = warehouseOid;
        return this;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public LocalDateTime getStartAt()
    {
        return startAt;
    }

    public Stocktaking setStartAt(LocalDateTime startAt)
    {
        this.startAt = startAt;
        return this;
    }

    public LocalDateTime getEndAt()
    {
        return endAt;
    }

    public Stocktaking setEndAt(LocalDateTime endAt)
    {
        this.endAt = endAt;
        return this;
    }

    public Instant getCreatedDate()
    {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate)
    {
        this.createdDate = createdDate;
    }

    public String getLastModifiedUser()
    {
        return lastModifiedUser;
    }

    public void setLastModifiedUser(String lastModifiedUser)
    {
        this.lastModifiedUser = lastModifiedUser;
    }

    public Instant getLastModifiedDate()
    {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate)
    {
        this.lastModifiedDate = lastModifiedDate;
    }

    public long getVersion()
    {
        return version;
    }

    public void setVersion(long version)
    {
        this.version = version;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this);
    }
}
