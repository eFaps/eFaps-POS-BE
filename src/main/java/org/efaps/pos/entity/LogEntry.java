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
import org.efaps.pos.dto.LogLevel;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "logs")
public class LogEntry
{

    @Id
    private String id;

    private String oid;

    private String ident;

    private String key;

    private String message;

    private LogLevel level;

    private Map<String, String> value;

    @CreatedDate
    private Instant createdDate;

    @Version
    private long version;

    public String getId()
    {
        return id;
    }

    public void setId(final String id)
    {
        this.id = id;
    }

    public String getOid()
    {
        return oid;
    }

    public LogEntry setOid(final String oid)
    {
        this.oid = oid;
        return this;
    }

    public String getIdent()
    {
        return ident;
    }

    public LogEntry setIdent(final String ident)
    {
        this.ident = ident;
        return this;
    }

    public String getKey()
    {
        return key;
    }

    public LogEntry setKey(final String key)
    {
        this.key = key;
        return this;
    }

    public String getMessage()
    {
        return message;
    }

    public LogEntry setMessage(final String message)
    {
        this.message = message;
        return this;
    }

    public LogLevel getLevel()
    {
        return level;
    }

    public LogEntry setLevel(final LogLevel level)
    {
        this.level = level;
        return this;
    }

    public Map<String, String> getValue()
    {
        return value;
    }

    public LogEntry setValue(final Map<String, String> value)
    {
        this.value = value;
        return this;
    }

    public Instant getCreatedDate()
    {
        return createdDate;
    }

    public void setCreatedDate(final Instant createdDate)
    {
        this.createdDate = createdDate;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this);
    }
}
