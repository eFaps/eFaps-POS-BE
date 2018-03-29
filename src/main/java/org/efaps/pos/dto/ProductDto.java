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

@JsonDeserialize(builder = ProductDto.Builder.class)
public class ProductDto
    extends AbstractDto
{
    private final String description;

    private ProductDto(final Builder _builder) {
        super(_builder);
        this.description = _builder.description;
    }

    public String getDescription()
    {
        return this.description;
    }

    /**
     * Creates builder to build {@link AgendaDto}.
     * @return created builder
     */
    public static Builder builder() {
      return new Builder();
    }

    /**
     * Builder to build {@link AgendaDto}.
     */
    public static final class Builder extends AbstractDto.Builder<ProductDto> {
        private String description;

        public Builder withDescription(final String _description) {
            this.description = _description;
            return this;
        }

        @Override
        public ProductDto build() {
            return new ProductDto(this);
          }
    }
}
