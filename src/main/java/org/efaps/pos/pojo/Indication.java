/*
 * Copyright 2003 - 2021 The eFaps Team
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

package org.efaps.pos.pojo;

import org.springframework.data.annotation.Id;

public class Indication
{

    @Id
    private String id;

    private String value;

    private String description;

    private String imageOid;

    public String getDescription()
    {
        return description;
    }

    public Indication setDescription(final String description)
    {
        this.description = description;
        return this;
    }

    public String getImageOid()
    {
        return imageOid;
    }

    public Indication setImageOid(final String imageOid)
    {
        this.imageOid = imageOid;
        return this;
    }

    public String getId()
    {
        return id;
    }

    public Indication setId(final String _id)
    {
        id = _id;
        return this;
    }

    public String getValue()
    {
        return value;
    }

    public Indication setValue(final String _value)
    {
        value = _value;
        return this;
    }
}
