/*
 * Copyright 2003 - 2023 The eFaps Team
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
package org.efaps.pos.service;

import java.util.Map;

public class CollectorException
    extends Exception
{

    private static final long serialVersionUID = 1L;

    private String ident;
    private String key;
    private Map<String, String> info;

    public CollectorException(final String msg)
    {
        super(msg);
    }

    public CollectorException(final String msg,
                              final Throwable e)
    {
        super(msg, e);
    }

    public CollectorException(final String ident,
                              final String key,
                              final Map<String, String> info,
                              final Throwable e)
    {
        super(e.getMessage(), e);
    }

    public CollectorException(final String ident,
                              final String key,
                              final Map<String, String> info,
                              final String msg)
    {
        super(msg);
        this.ident = ident;
        this.key = key;
        this.info = info;
    }

    public String getIdent()
    {
        return ident;
    }

    public void setIdent(String ident)
    {
        this.ident = ident;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public Map<String, String> getInfo()
    {
        return info;
    }

    public void setInfo(Map<String, String> info)
    {
        this.info = info;
    }
}
