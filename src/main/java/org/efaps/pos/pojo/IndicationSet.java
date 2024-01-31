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
package org.efaps.pos.pojo;

import java.util.Set;

import org.springframework.data.annotation.Id;

public class IndicationSet
{

    @Id
    private String id;

    private String name;

    private String description;

    private boolean required;

    private boolean multiple;

    private String imageOid;

    private Set<Indication> indications;

    public String getDescription()
    {
        return description;
    }

    public IndicationSet setDescription(final String description)
    {
        this.description = description;
        return this;
    }

    public boolean isMultiple()
    {
        return multiple;
    }

    public IndicationSet setMultiple(final boolean multiple)
    {
        this.multiple = multiple;
        return this;
    }

    public String getImageOid()
    {
        return imageOid;
    }

    public IndicationSet setImageOid(final String imageOid)
    {
        this.imageOid = imageOid;
        return this;
    }

    public IndicationSet setRequired(final boolean required)
    {
        this.required = required;
        return this;
    }

    public String getId()
    {
        return id;
    }

    public IndicationSet setId(final String _id)
    {
        id = _id;
        return this;
    }

    public String getName()
    {
        return name;
    }

    public IndicationSet setName(final String _name)
    {
        name = _name;
        return this;
    }

    public boolean isRequired()
    {
        return required;
    }

    public IndicationSet setRequired(final Boolean _required)
    {
        required = _required == null ? false : _required;
        return this;
    }

    public Set<Indication> getIndications()
    {
        return indications;
    }

    public IndicationSet setIndications(final Set<Indication> _indications)
    {
        indications = _indications;
        return this;
    }
}
