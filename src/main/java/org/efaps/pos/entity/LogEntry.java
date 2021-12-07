package org.efaps.pos.entity;

import java.time.Instant;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.boot.logging.LogLevel;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "logs")
public class LogEntry
{

    @Id
    private String id;

    private String ident;

    private String key;

    private String value;

    private LogLevel level;

    @CreatedDate
    private Instant createdDate;

    public String getId()
    {
        return id;
    }

    public void setId(final String id)
    {
        this.id = id;
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

    public String getValue()
    {
        return value;
    }

    public LogEntry setValue(final String value)
    {
        this.value = value;
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
