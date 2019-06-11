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

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.efaps.pos.dto.IdentificationType;
import org.springframework.data.annotation.Id;
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

    public String getId()
    {
        return this.id;
    }

    public Contact setId(final String _id)
    {
        this.id = _id;
        return this;
    }

    public String getOid()
    {
        return this.oid;
    }

    public Contact setOid(final String _oid)
    {
        this.oid = _oid;
        return this;
    }

    public String getName()
    {
        return this.name;
    }

    public Contact setName(final String _name)
    {
        this.name = _name;
        return this;
    }

    public IdentificationType getIdType()
    {
        return this.idType;
    }

    public Contact setIdType(final IdentificationType _idType)
    {
        this.idType = _idType;
        return this;
    }

    public String getIdNumber()
    {
        return this.idNumber;
    }

    public Contact setIdNumber(final String _idNumber)
    {
        this.idNumber = _idNumber;
        return this;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this);
    }

}
