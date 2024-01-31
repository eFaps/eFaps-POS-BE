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


@JsonDeserialize(builder = ProductHeadDto.Builder.class)
public class ProductHeadDto
{
    private final String oid;
    private final String sku;
    private final String description;
    private final String uoM;
    private ProductHeadDto(Builder builder)
    {
        this.oid = builder.oid;
        this.sku = builder.sku;
        this.description = builder.description;
        this.uoM = builder.uoM;
    }
    public String getOid()
    {
        return oid;
    }

    public String getSku()
    {
        return sku;
    }

    public String getDescription()
    {
        return description;
    }

    public String getUoM()
    {
        return uoM;
    }

    public static Builder builder()
    {
        return new Builder();
    }
    public static final class Builder
    {

        private String oid;
        private String sku;
        private String description;
        private String uoM;

        private Builder()
        {
        }

        public Builder withOid(String oid)
        {
            this.oid = oid;
            return this;
        }

        public Builder withSku(String sku)
        {
            this.sku = sku;
            return this;
        }

        public Builder withDescription(String description)
        {
            this.description = description;
            return this;
        }

        public Builder withUoM(String uoM)
        {
            this.uoM = uoM;
            return this;
        }

        public ProductHeadDto build()
        {
            return new ProductHeadDto(this);
        }
    }
}
