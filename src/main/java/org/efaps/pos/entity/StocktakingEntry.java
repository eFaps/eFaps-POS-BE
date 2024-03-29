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

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "stocktaking-entries")
public class StocktakingEntry
{

    @Id
    private String id;
    private String productOid;
    private BigDecimal quantity;
    private String stocktakingId;
    private String comment;

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

    public String getProductOid()
    {
        return productOid;
    }

    public StocktakingEntry setProductOid(String productOid)
    {
        this.productOid = productOid;
        return this;
    }

    public BigDecimal getQuantity()
    {
        return quantity;
    }

    public StocktakingEntry setQuantity(BigDecimal quantity)
    {
        this.quantity = quantity;
        return this;
    }

    public String getStocktakingId()
    {
        return stocktakingId;
    }

    public StocktakingEntry setStocktakingId(String stocktakingId)
    {
        this.stocktakingId = stocktakingId;
        return this;
    }

    public String getComment()
    {
        return comment;
    }

    public StocktakingEntry setComment(String comment)
    {
        this.comment = comment;
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
}
