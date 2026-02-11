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

import org.apache.commons.lang3.builder.ToStringBuilder;

import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ValidateStockDto.Builder.class)
public class ValidateStockDto
{

    private final String warehouseOid;

    private final List<ValidateStockEntryDto> entries;

    private ValidateStockDto(Builder builder)
    {
        this.warehouseOid = builder.warehouseOid;
        this.entries = builder.entries;
    }

    public String getWarehouseOid()
    {
        return warehouseOid;
    }

    public List<ValidateStockEntryDto> getEntries()
    {
        return entries;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private String warehouseOid;
        private List<ValidateStockEntryDto> entries = Collections.emptyList();

        private Builder()
        {
        }

        public Builder withWarehouseOid(String warehouseOid)
        {
            this.warehouseOid = warehouseOid;
            return this;
        }

        public Builder withEntries(List<ValidateStockEntryDto> entries)
        {
            this.entries = entries;
            return this;
        }

        public ValidateStockDto build()
        {
            return new ValidateStockDto(this);
        }
    }
}
