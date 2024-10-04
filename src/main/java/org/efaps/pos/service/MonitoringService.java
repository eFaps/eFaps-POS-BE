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

import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.OffsetDateTime;

import org.apache.commons.lang3.SystemProperties;
import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.config.ConfigProperties;
import org.efaps.pos.config.ConfigProperties.Company;
import org.efaps.pos.context.Context;
import org.efaps.pos.dto.ReportToBaseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Service
public class MonitoringService
{

    private static final Logger LOG = LoggerFactory.getLogger(MonitoringService.class);

    private final ConfigProperties configProperties;
    private final EFapsClient eFapsClient;

    public MonitoringService(final ConfigProperties configProperties,
                             final EFapsClient eFapsClient)
    {
        this.configProperties = configProperties;
        this.eFapsClient = eFapsClient;
    }

    public void reportToBase()
        throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
        InvocationTargetException
    {
        var instalationId = "";
        try {
            instalationId = String.format("%s - %s - %s - %s - %s - %s - %s",
                            InetAddress.getLocalHost(),
                            SystemProperties.getOsName(),
                            SystemProperties.getOsVersion(),
                            SystemProperties.getOsArch(),
                            SystemProperties.getJavaVersion(),
                            SystemProperties.getJavaVmName(),
                            SystemProperties.getUserName());
        } catch (final UnknownHostException e) {
            LOG.error("Catched error during evaluation of InstalationId", e);
        }

        final var dto = ReportToBaseDto.builder()
                        .withVersion(configProperties.getBeInst().getVersion())
                        .withInstalationId(instalationId)
                        .withCreatedAt(OffsetDateTime.now())
                        .build();
        if (configProperties.getCompanies().size() > 0) {
            for (final Company company : configProperties.getCompanies()) {
                Context.get().setCompany(company);
                MDC.put("company", company.getTenant());
                eFapsClient.postReportToBase(dto);
            }
        } else {
            eFapsClient.postReportToBase(dto);
        }
    }
}
