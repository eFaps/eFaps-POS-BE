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
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


@JsonDeserialize(builder = UpdateDto.Builder.class)
public class UpdateDto
{
    private final String version;
    private final List<UpdateInstructionDto> instructions;


    private UpdateDto(Builder builder)
    {
        this.version = builder.version;
        this.instructions = builder.instructions;
    }

    public String getVersion()
    {
        return version;
    }


    public List<UpdateInstructionDto> getInstructions()
    {
        return instructions;
    }

    public static Builder builder()
    {
        return new Builder();
    }


    public static final class Builder
    {

        private String version;
        private List<UpdateInstructionDto> instructions = Collections.emptyList();

        private Builder()
        {
        }

        public Builder withVersion(String version)
        {
            this.version = version;
            return this;
        }

        public Builder withInstructions(List<UpdateInstructionDto> instructions)
        {
            this.instructions = instructions;
            return this;
        }

        public UpdateDto build()
        {
            return new UpdateDto(this);
        }
    }
}
