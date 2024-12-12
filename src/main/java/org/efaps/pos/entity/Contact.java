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

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.efaps.pos.dto.IdentificationType;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "contacts")
public class Contact
{

    @Id
    private String id;

    private String oid;

    private String name;

    private IdentificationType idType;

    private String idNumber;

    private String email;

    private String firstName;

    private String lastName;

    private Visibility visibility;

    private Boolean updated;

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

    public Contact setId(final String _id)
    {
        id = _id;
        return this;
    }

    public String getOid()
    {
        return oid;
    }

    public Contact setOid(final String _oid)
    {
        oid = _oid;
        return this;
    }

    public String getName()
    {
        return name;
    }

    public Contact setName(final String _name)
    {
        name = _name;
        return this;
    }

    public IdentificationType getIdType()
    {
        return idType;
    }

    public Contact setIdType(final IdentificationType _idType)
    {
        idType = _idType;
        return this;
    }

    public String getIdNumber()
    {
        return idNumber;
    }

    public Contact setIdNumber(final String _idNumber)
    {
        idNumber = _idNumber;
        return this;
    }

    public String getEmail()
    {
        return email;
    }

    public Contact setEmail(final String _email)
    {
        email = _email;
        return this;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public Contact setFirstName(final String firstName)
    {
        this.firstName = firstName;
        return this;
    }

    public String getLastName()
    {
        return lastName;
    }

    public Contact setLastName(final String lastName)
    {
        this.lastName = lastName;
        return this;
    }

    public Visibility getVisibility()
    {
        return visibility;
    }

    public Contact setVisibility(Visibility visibility)
    {
        this.visibility = visibility;
        return this;
    }

    public boolean isUpdated()
    {
        return updated;
    }

    public void setUpdated(Boolean updated)
    {
        this.updated = updated;
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
