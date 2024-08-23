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
package org.efaps.pos.client;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.http.MediaType;

public class Checkout
{

    private byte[] content;
    private MediaType contentType;
    private String filename;

    public byte[] getContent()
    {
        return this.content;
    }

    public void setContent(final byte[] _content)
    {
        this.content = _content;
    }

    public void setContentType(final MediaType _contentType)
    {
        this.contentType = _contentType;
    }

    public MediaType getContentType()
    {
        return this.contentType;
    }

    public String getFilename()
    {
        return this.filename;
    }

    public void setFilename(final String _filename)
    {
        this.filename = _filename;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this);
    }
}
