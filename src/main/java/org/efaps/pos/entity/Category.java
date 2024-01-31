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

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "categories")
public class Category
{

    @Id
    private String id;

    private String oid;

    private String name;

    private int weight;

    private String imageOid;

    private String parentOid;

    public String getOid()
    {
        return oid;
    }

    public Category setOid(final String _oid)
    {
        oid = _oid;
        id = _oid;
        return this;
    }

    public String getName()
    {
        return name;
    }

    public Category setName(final String _name)
    {
        name = _name;
        return this;
    }

    public int getWeight()
    {
        return weight;
    }

    public Category setWeight(final int weight)
    {
        this.weight = weight;
        return this;
    }

    public String getImageOid()
    {
        return imageOid;
    }

    public Category setImageOid(final String imageOid)
    {
        this.imageOid = imageOid;
        return this;
    }

    public String getParentOid()
    {
        return parentOid;
    }

    public Category setParentOid(final String parentOid)
    {
        this.parentOid = parentOid;
        return this;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this);
    }
}
