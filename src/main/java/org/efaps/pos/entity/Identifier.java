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
