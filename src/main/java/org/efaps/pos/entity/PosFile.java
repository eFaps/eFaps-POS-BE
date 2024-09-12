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
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "files")
public class PosFile
{

    @Id
    private String id;
    private String oid;
    private String name;
    private String description;
    private String fileName;
    private Map<String, String> tags;
    @LastModifiedBy
    private String lastModifiedUser;
    @LastModifiedDate
    private Instant lastModifiedDate;

    public String getId()
    {
        return id;
    }

    public PosFile setId(String id)
    {
        this.id = id;
        return this;
    }

    public String getOid()
    {
        return oid;
    }

    public PosFile setOid(String oid)
    {
        this.oid = oid;
        return this;
    }

    public String getName()
    {
        return name;
    }

    public PosFile setName(String name)
    {
        this.name = name;
        return this;
    }

    public String getDescription()
    {
        return description;
    }

    public PosFile setDescription(String description)
    {
        this.description = description;
        return this;
    }

    public String getFileName()
    {
        return fileName;
    }

    public PosFile setFileName(String fileName)
    {
        this.fileName = fileName;
        return this;
    }

    public Map<String, String> getTags()
    {
        return tags;
    }

    public PosFile setTags(Map<String, String> tags)
    {
        this.tags = tags;
        return this;
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

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this);
    }
}
