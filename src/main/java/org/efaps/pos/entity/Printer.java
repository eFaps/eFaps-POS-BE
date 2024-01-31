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

import org.efaps.pos.dto.PrinterType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "printers")
public class Printer
{

    @Id
    private String id;

    private String oid;

    private String name;

    private PrinterType type;

    public String getOid()
    {
        return this.oid;
    }

    public Printer setOid(final String _oid)
    {
        this.oid = _oid;
        this.id = _oid;
        return this;
    }

    public String getName()
    {
        return this.name;
    }

    public Printer setName(final String _name)
    {
        this.name = _name;
        return this;
    }

    public PrinterType getType()
    {
        return this.type;
    }

    public Printer setType(final PrinterType _type)
    {
        this.type = _type;
        return this;
    }
}
