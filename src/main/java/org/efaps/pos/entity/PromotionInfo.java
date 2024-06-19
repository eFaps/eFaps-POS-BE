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
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.efaps.pos.dto.PromoInfoDto;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "promotionInfos")
public class PromotionInfo
{

    @Id
    private String id;
    private String oid;
    private String documentId;
    private PromoInfoDto promoInfo;
    private List<PromotionEntity> promotions;

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

    public String getOid()
    {
        return oid;
    }

    public PromotionInfo setOid(final String oid)
    {
        this.oid = oid;
        return this;
    }

    public String getDocumentId()
    {
        return documentId;
    }

    public PromotionInfo setDocumentId(final String documentId)
    {
        this.documentId = documentId;
        return this;
    }

    public PromoInfoDto getPromoInfo()
    {
        return promoInfo;
    }

    public PromotionInfo setPromoInfo(final PromoInfoDto promoInfo)
    {
        this.promoInfo = promoInfo;
        return this;
    }

    public List<PromotionEntity> getPromotions()
    {
        return promotions;
    }

    public PromotionInfo setPromotions(final List<PromotionEntity> promotions)
    {
        this.promotions = promotions;
        return this;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(final String user)
    {
        this.user = user;
    }

    public Instant getCreatedDate()
    {
        return createdDate;
    }

    public void setCreatedDate(final Instant createdDate)
    {
        this.createdDate = createdDate;
    }

    public String getLastModifiedUser()
    {
        return lastModifiedUser;
    }

    public void setLastModifiedUser(final String lastModifiedUser)
    {
        this.lastModifiedUser = lastModifiedUser;
    }

    public Instant getLastModifiedDate()
    {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(final Instant lastModifiedDate)
    {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this);
    }
}
