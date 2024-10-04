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
package org.efaps.pos.service;

import java.util.List;
import java.util.stream.Collectors;

import org.efaps.pos.config.ConfigProperties;
import org.efaps.pos.dto.PosCompanyDto;
import org.springframework.stereotype.Service;

@Service
public class CompanyService
{

    private final ConfigProperties configProperties;

    public CompanyService(final ConfigProperties _configProperties)
    {
        configProperties = _configProperties;
    }

    public List<PosCompanyDto> getCompanies()
    {
        return configProperties.getCompanies().stream()
                        .map(company->PosCompanyDto.builder()
                                        .withKey(company.getKey())
                                        .withLabel(company.getLabel())
                                        .build())
                        .collect(Collectors.toList());
    }
}
