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

import jakarta.annotation.Generated;
import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = DistrictDto.Builder.class)
public class DistrictDto
{

    private final String ubigeo;
    private final String codDep;
    private final String codProv;
    private final String codDist;
    private final String departamento;
    private final String provincia;
    private final String distrito;

    @Generated("SparkTools")
    private DistrictDto(Builder builder)
    {
        this.ubigeo = builder.ubigeo;
        this.codDep = builder.codDep;
        this.codProv = builder.codProv;
        this.codDist = builder.codDist;
        this.departamento = builder.departamento;
        this.provincia = builder.provincia;
        this.distrito = builder.distrito;
    }

    public String getUbigeo()
    {
        return ubigeo;
    }

    public String getCodDep()
    {
        return codDep;
    }

    public String getCodProv()
    {
        return codProv;
    }

    public String getCodDist()
    {
        return codDist;
    }

    public String getDepartamento()
    {
        return departamento;
    }

    public String getProvincia()
    {
        return provincia;
    }

    public String getDistrito()
    {
        return distrito;
    }

    @Generated("SparkTools")
    public static Builder builder()
    {
        return new Builder();
    }

    @Generated("SparkTools")
    public static final class Builder
    {

        private String ubigeo;
        private String codDep;
        private String codProv;
        private String codDist;
        private String departamento;
        private String provincia;
        private String distrito;

        private Builder()
        {
        }

        public Builder withUbigeo(String ubigeo)
        {
            this.ubigeo = ubigeo;
            return this;
        }

        public Builder withCodDep(String codDep)
        {
            this.codDep = codDep;
            return this;
        }

        public Builder withCodProv(String codProv)
        {
            this.codProv = codProv;
            return this;
        }

        public Builder withCodDist(String codDist)
        {
            this.codDist = codDist;
            return this;
        }

        public Builder withDepartamento(String departamento)
        {
            this.departamento = departamento;
            return this;
        }

        public Builder withProvincia(String provincia)
        {
            this.provincia = provincia;
            return this;
        }

        public Builder withDistrito(String distrito)
        {
            this.distrito = distrito;
            return this;
        }

        public DistrictDto build()
        {
            return new DistrictDto(this);
        }
    }
}
