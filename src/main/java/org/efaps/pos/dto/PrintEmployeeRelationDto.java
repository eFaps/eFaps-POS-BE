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

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = PrintEmployeeRelationDto.Builder.class)
public class PrintEmployeeRelationDto
{

    private final EmployeeRelationType type;
    private final EmployeeDto employee;

    private PrintEmployeeRelationDto(final Builder builder)
    {
        type = builder.type;
        employee = builder.employee;
    }

    public EmployeeRelationType getType()
    {
        return type;
    }

    public EmployeeDto getEmployee()
    {
        return employee;
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

        private EmployeeRelationType type;
        private EmployeeDto employee;

        private Builder()
        {
        }

        public Builder withType(final EmployeeRelationType type)
        {
            this.type = type;
            return this;
        }

        public Builder withEmployee(final EmployeeDto employee)
        {
            this.employee = employee;
            return this;
        }

        public PrintEmployeeRelationDto build()
        {
            return new PrintEmployeeRelationDto(this);
        }
    }
}
