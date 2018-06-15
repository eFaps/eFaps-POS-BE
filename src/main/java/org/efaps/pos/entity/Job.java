/*
 * Copyright 2003 - 2018 The eFaps Team
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
package org.efaps.pos.entity;

import java.util.Set;

import org.efaps.pos.entity.AbstractDocument.Item;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "jobs")
public class Job
{

    @Id
    private String id;

    private String documentId;

    private String printerOid;

    private Set<Item> items;

    public String getId()
    {
        return this.id;
    }

    public Job setId(final String _id)
    {
        this.id = _id;
        return this;
    }

    public String getPrinterOid()
    {
        return this.printerOid;
    }

    public Job setPrinterOid(final String _printerOid)
    {
        this.printerOid = _printerOid;
        return this;
    }

    public Set<Item> getItems()
    {
        return this.items;
    }

    public Job setItems(final Set<Item> _items)
    {
        this.items = _items;
        return this;
    }

    public String getDocumentId()
    {
        return this.documentId;
    }

    public Job setDocumentId(final String _documentId)
    {
        this.documentId = _documentId;
        return this;
    }
}
