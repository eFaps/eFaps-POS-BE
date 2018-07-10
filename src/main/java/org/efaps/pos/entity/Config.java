package org.efaps.pos.entity;

import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "stash")
public class Config
{
    public static String KEY = Config.class.getName() + ".Key";

    @Id
    private String id;

    private Map<String, String> properties;

    public String getId()
    {
        return this.id;
    }

    public Config setId(final String _id)
    {
        this.id = _id;
        return this;
    }

    public Map<String, String> getProperties()
    {
        return this.properties;
    }

    public Config setProperties(final Map<String, String> _properties)
    {
        this.properties = _properties;
        return this;
    }
}
