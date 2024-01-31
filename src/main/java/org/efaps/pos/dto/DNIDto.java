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

import java.time.LocalDate;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = DNIDto.Builder.class)
public class DNIDto
{

    private final String number;
    private final String name;
    private final String mothersLastname;
    private final String fathersLastname;
    private final String fullName;
    private final Integer verificationCode;
    private final String gender;
    private final LocalDate birthDate;
    private final String maritalStatus;
    private final String location;
    private final String locationReniec;
    private final String region;
    private final String province;
    private final String district;
    private final String address;
    private final OffsetDateTime updatedAt;

    private DNIDto(final Builder builder)
    {
        this.number = builder.number;
        this.name = builder.name;
        this.mothersLastname = builder.mothersLastname;
        this.fathersLastname = builder.fathersLastname;
        this.fullName = builder.fullName;
        this.verificationCode = builder.verificationCode;
        this.gender = builder.gender;
        this.birthDate = builder.birthDate;
        this.maritalStatus = builder.maritalStatus;
        this.location = builder.location;
        this.locationReniec = builder.locationReniec;
        this.region = builder.region;
        this.province = builder.province;
        this.district = builder.district;
        this.address = builder.address;
        this.updatedAt = builder.updatedAt;
    }

    public String getNumber()
    {
        return number;
    }

    public String getName()
    {
        return name;
    }

    public String getMothersLastname()
    {
        return mothersLastname;
    }

    public String getFathersLastname()
    {
        return fathersLastname;
    }

    public String getFullName()
    {
        return fullName;
    }

    public Integer getVerificationCode()
    {
        return verificationCode;
    }

    public String getGender()
    {
        return gender;
    }

    public LocalDate getBirthDate()
    {
        return birthDate;
    }

    public String getMaritalStatus()
    {
        return maritalStatus;
    }

    public String getLocation()
    {
        return location;
    }

    public String getLocationReniec()
    {
        return locationReniec;
    }

    public String getRegion()
    {
        return region;
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

    public OffsetDateTime getUpdatedAt()
    {
        return updatedAt;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private String number;
        private String name;
        private String mothersLastname;
        private String fathersLastname;
        private String fullName;
        private Integer verificationCode;
        private String gender;
        private LocalDate birthDate;
        private String maritalStatus;
        private String location;
        private String locationReniec;
        private String region;
        private String province;
        private String district;
        private String address;
        private OffsetDateTime updatedAt;

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

        public Builder withMothersLastname(String mothersLastname)
        {
            this.mothersLastname = mothersLastname;
            return this;
        }

        public Builder withFathersLastname(String fathersLastname)
        {
            this.fathersLastname = fathersLastname;
            return this;
        }

        public Builder withFullName(String fullName)
        {
            this.fullName = fullName;
            return this;
        }

        public Builder withVerificationCode(Integer verificationCode)
        {
            this.verificationCode = verificationCode;
            return this;
        }

        public Builder withGender(String gender)
        {
            this.gender = gender;
            return this;
        }

        public Builder withBirthDate(LocalDate birthDate)
        {
            this.birthDate = birthDate;
            return this;
        }

        public Builder withMaritalStatus(String maritalStatus)
        {
            this.maritalStatus = maritalStatus;
            return this;
        }

        public Builder withLocation(String location)
        {
            this.location = location;
            return this;
        }

        public Builder withLocationReniec(String locationReniec)
        {
            this.locationReniec = locationReniec;
            return this;
        }

        public Builder withRegion(String region)
        {
            this.region = region;
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

        public Builder withUpdatedAt(OffsetDateTime updatedAt)
        {
            this.updatedAt = updatedAt;
            return this;
        }

        public DNIDto build()
        {
            return new DNIDto(this);
        }
    }
}
