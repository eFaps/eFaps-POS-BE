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

package org.efaps.pos.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Set;

@JsonDeserialize(builder = PosOrderDto.Builder.class)
public class PosOrderDto
    extends AbstractDocumentDto
{

    public PosOrderDto(final Builder _builder)
    {
        super(_builder);
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractDocumentDto.Builder<Builder, PosOrderDto>
    {

        public Builder withItems(final Set<PosDocItemDto> _items)
        {
            setItems(_items);
            return this;
        }

        @Override
        public PosOrderDto build()
        {
            return new PosOrderDto(this);
        }
    }
}