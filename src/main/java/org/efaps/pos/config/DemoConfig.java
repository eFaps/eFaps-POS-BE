/*
 * Copyright 2003 - 2020 The eFaps Team
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
package org.efaps.pos.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

@ConfigurationProperties("demo")
@Profile(value = { "demo" })
public class DemoConfig
{

    private String users;
    private String products;
    private String workspaces;
    private String poss;
    private String categories;
    private String sequences;
    private String contacts;
    private String files;

    public String getUsers()
    {
        return users;
    }

    public void setUsers(final String users)
    {
        this.users = users;
    }

    public String getProducts()
    {
        return products;
    }

    public void setProducts(final String products)
    {
        this.products = products;
    }

    public String getWorkspaces()
    {
        return workspaces;
    }

    public void setWorkspaces(final String workspaces)
    {
        this.workspaces = workspaces;
    }

    public String getPoss()
    {
        return poss;
    }

    public void setPoss(final String poss)
    {
        this.poss = poss;
    }

    public String getCategories()
    {
        return categories;
    }

    public void setCategories(final String categories)
    {
        this.categories = categories;
    }

    public String getSequences()
    {
        return sequences;
    }

    public void setSequences(final String sequences)
    {
        this.sequences = sequences;
    }

    public String getContacts()
    {
        return contacts;
    }

    public void setContacts(final String contacts)
    {
        this.contacts = contacts;
    }

    public String getFiles()
    {
        return files;
    }

    public void setFiles(String files)
    {
        this.files = files;
    }

}
