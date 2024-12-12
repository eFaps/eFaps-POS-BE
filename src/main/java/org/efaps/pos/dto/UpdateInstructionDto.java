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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = UpdateInstructionDto.Builder.class)
public class UpdateInstructionDto
{

    private final String fileOid;
    private final String targetPath;
    private final boolean expand;

    private UpdateInstructionDto(Builder builder)
    {
        this.fileOid = builder.fileOid;
        this.targetPath = builder.targetPath;
        this.expand = builder.expand;
    }

    public String getTargetPath()
    {
        return targetPath;
    }

    public String getFileOid()
    {
        return fileOid;
    }

    public boolean isExpand()
    {
        return expand;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private String fileOid;
        private String targetPath;
        private boolean expand;

        private Builder()
        {
        }

        public Builder withFileOid(String fileOid)
        {
            this.fileOid = fileOid;
            return this;
        }

        public Builder withTargetPath(String targetPath)
        {
            this.targetPath = targetPath;
            return this;
        }

        public Builder withExpand(boolean expand)
        {
            this.expand = expand;
            return this;
        }

        public UpdateInstructionDto build()
        {
            return new UpdateInstructionDto(this);
        }
    }
}
