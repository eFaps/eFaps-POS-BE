package org.efaps.pos.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "stash")
public class Identifier
{
    public static String KEY = Identifier.class.getName() + ".Key";

    @Id
    private String id;
    private String identifier;
    private LocalDateTime created;

    public String getId()
    {
        return this.id;
    }

    public Identifier setId(final String _id)
    {
        this.id = _id;
        return this;
    }

    public String getIdentifier()
    {
        return this.identifier;
    }

    public Identifier setIdentifier(final String _identifier)
    {
        this.identifier = _identifier;
        return this;
    }

    public LocalDateTime getCreated()
    {
        return this.created;
    }

    public Identifier setCreated(final LocalDateTime _created)
    {
        this.created = _created;
        return this;
    }

}
