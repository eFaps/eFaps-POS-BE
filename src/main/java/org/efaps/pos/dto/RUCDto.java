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

@JsonDeserialize(builder = RUCDto.Builder.class)
public class RUCDto
{

    private final String number;

    private final String name;
    private final String state;
    private final String homeState;
    private final String ubigeo;
    private final String streetType;
    private final String street;
    private final String zoneCode;
    private final String zoneType;
    private final String streetNumber;
    private final String streetInterior;
    private final String streetBatch;
    private final String apartmentNumber;
    private final String block;
    private final String kilometer;
    private final String department;
    private final String province;
    private final String district;
    private final String address;
    private final String updated;

    private RUCDto(Builder builder)
    {
        this.number = builder.number;
        this.name = builder.name;
        this.state = builder.state;
        this.homeState = builder.homeState;
        this.ubigeo = builder.ubigeo;
        this.streetType = builder.streetType;
        this.street = builder.street;
        this.zoneCode = builder.zoneCode;
        this.zoneType = builder.zoneType;
        this.streetNumber = builder.streetNumber;
        this.streetInterior = builder.streetInterior;
        this.streetBatch = builder.streetBatch;
        this.apartmentNumber = builder.apartmentNumber;
        this.block = builder.block;
        this.kilometer = builder.kilometer;
        this.department = builder.department;
        this.province = builder.province;
        this.district = builder.district;
        this.address = builder.address;
        this.updated = builder.updated;
    }

    public String getNumber()
    {
        return number;
    }

    public String getName()
    {
        return name;
    }

    public String getState()
    {
        return state;
    }

    public String getHomeState()
    {
        return homeState;
    }

    public String getUbigeo()
    {
        return ubigeo;
    }

    public String getStreetType()
    {
        return streetType;
    }

    public String getStreet()
    {
        return street;
    }

    public String getZoneCode()
    {
        return zoneCode;
    }

    public String getZoneType()
    {
        return zoneType;
    }

    public String getStreetNumber()
    {
        return streetNumber;
    }

    public String getStreetInterior()
    {
        return streetInterior;
    }

    public String getStreetBatch()
    {
        return streetBatch;
    }

    public String getApartmentNumber()
    {
        return apartmentNumber;
    }

    public String getBlock()
    {
        return block;
    }

    public String getKilometer()
    {
        return kilometer;
    }

    public String getDepartment()
    {
        return department;
    }

    public String getProvince()
    {
        return province;
    }

    public String getDistrict()
    {
        return district;
    }

    public String getAddress()
    {
        return address;
    }

    public String getUpdated()
    {
        return updated;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private String number;
        private String name;
        private String state;
        private String homeState;
        private String ubigeo;
        private String streetType;
        private String street;
        private String zoneCode;
        private String zoneType;
        private String streetNumber;
        private String streetInterior;
        private String streetBatch;
        private String apartmentNumber;
        private String block;
        private String kilometer;
        private String department;
        private String province;
        private String district;
        private String address;
        private String updated;

        private Builder()
        {
        }

        public Builder withNumber(String number)
        {
            this.number = number;
            return this;
        }

        public Builder withName(String name)
        {
            this.name = name;
            return this;
        }

        public Builder withState(String state)
        {
            this.state = state;
            return this;
        }

        public Builder withHomeState(String homeState)
        {
            this.homeState = homeState;
            return this;
        }

        public Builder withUbigeo(String ubigeo)
        {
            this.ubigeo = ubigeo;
            return this;
        }

        public Builder withStreetType(String streetType)
        {
            this.streetType = streetType;
            return this;
        }

        public Builder withStreet(String street)
        {
            this.street = street;
            return this;
        }

        public Builder withZoneCode(String zoneCode)
        {
            this.zoneCode = zoneCode;
            return this;
        }

        public Builder withZoneType(String zoneType)
        {
            this.zoneType = zoneType;
            return this;
        }

        public Builder withStreetNumber(String streetNumber)
        {
            this.streetNumber = streetNumber;
            return this;
        }

        public Builder withStreetInterior(String streetInterior)
        {
            this.streetInterior = streetInterior;
            return this;
        }

        public Builder withStreetBatch(String streetBatch)
        {
            this.streetBatch = streetBatch;
            return this;
        }

        public Builder withApartmentNumber(String apartmentNumber)
        {
            this.apartmentNumber = apartmentNumber;
            return this;
        }

        public Builder withBlock(String block)
        {
            this.block = block;
            return this;
        }

        public Builder withKilometer(String kilometer)
        {
            this.kilometer = kilometer;
            return this;
        }

        public Builder withDepartment(String department)
        {
            this.department = department;
            return this;
        }

        public Builder withProvince(String province)
        {
            this.province = province;
            return this;
        }

        public Builder withDistrict(String district)
        {
            this.district = district;
            return this;
        }

        public Builder withAddress(String address)
        {
            this.address = address;
            return this;
        }

        public Builder withUpdated(String updated)
        {
            this.updated = updated;
            return this;
        }

        public RUCDto build()
        {
            return new RUCDto(this);
        }
    }
}
