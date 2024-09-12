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
package org.efaps.pos.dto;

import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = PosFileDto.Builder.class)
public class PosFileDto
{
    private final String oid;
    private final String name;
    private final String description;
    private final String fileName;
    private final String path;
    private final Map<String, String> tags;

    private PosFileDto(Builder builder)
    {
        this.oid = builder.oid;
        this.name = builder.name;
        this.description = builder.description;
        this.fileName = builder.fileName;
        this.path = builder.path;
        this.tags = builder.tags;
    }

    public String getOid()
    {
        return oid;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public String getFileName()
    {
        return fileName;
    }

    public String getPath()
    {
        return path;
    }

    public Map<String, String> getTags()
    {
        return tags;
    }
    public static Builder builder()
    {
        return new Builder();
    }
    public static final class Builder
    {

        private String oid;
        private String name;
        private String description;
        private String fileName;
        private String path;
        private Map<String, String> tags = Collections.emptyMap();

        private Builder()
        {
        }

        public Builder withOid(String oid)
        {
            this.oid = oid;
            return this;
        }

        public Builder withName(String name)
        {
            this.name = name;
            return this;
        }

        public Builder withDescription(String description)
        {
            this.description = description;
            return this;
        }

        public Builder withFileName(String fileName)
        {
            this.fileName = fileName;
            return this;
        }

        public Builder withPath(String path)
        {
            this.path = path;
            return this;
        }

        public Builder withTags(Map<String, String> tags)
        {
            this.tags = tags;
            return this;
        }

        public PosFileDto build()
        {
            return new PosFileDto(this);
        }
    }
}
