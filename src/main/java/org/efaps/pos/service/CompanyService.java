package org.efaps.pos.service;

import java.util.List;
import java.util.stream.Collectors;

import org.efaps.pos.ConfigProperties;
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
