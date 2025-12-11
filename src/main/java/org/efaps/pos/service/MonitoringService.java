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
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.SystemProperties;
import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.config.ConfigProperties;
import org.efaps.pos.config.ConfigProperties.Company;
import org.efaps.pos.context.Context;
import org.efaps.pos.dto.ReportToBaseDto;
import org.efaps.pos.entity.SyncInfo;
import org.efaps.pos.listener.IMonitoringReportContributor;
import org.efaps.pos.pojo.StashId;
import org.efaps.pos.util.Utils;
import org.efaps.pos.util.VersionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import dev.failsafe.Failsafe;
import dev.failsafe.RetryPolicy;
import io.jsonwebtoken.lang.Collections;

@Service
public class MonitoringService
{

    private static final Logger LOG = LoggerFactory.getLogger(MonitoringService.class);

    private final MongoTemplate mongoTemplate;
    private final ConfigProperties configProperties;
    private final EFapsClient eFapsClient;
    private final List<IMonitoringReportContributor> reportContributors;

    public MonitoringService(final MongoTemplate mongoTemplate,
                             final ConfigProperties configProperties,
                             final EFapsClient eFapsClient,
                             final Optional<List<IMonitoringReportContributor>> contributors)
    {
        this.mongoTemplate = mongoTemplate;
        this.configProperties = configProperties;
        this.eFapsClient = eFapsClient;
        this.reportContributors = contributors.isEmpty() ? Collections.emptyList() : contributors.get();
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
        final var currentVersion = VersionUtil.evalVersion();

        final var syncInfo = mongoTemplate.findById(StashId.REPORTTOBASESYNC.getKey(), SyncInfo.class);
        LocalDateTime lastSync;
        if (syncInfo == null) {
            lastSync = LocalDateTime.now();
        } else {
            lastSync = syncInfo.getLastSync();
        }
        final var createdAt = OffsetDateTime.now();

        final var details = new HashMap<String, Object>();
        details.put("start", Utils.toOffset(lastSync));
        details.put("end", createdAt);

        for (final var reportContributor : reportContributors) {
            details.putAll(reportContributor.add2Report(lastSync, Utils.toLocal(createdAt)));
        }

        final var dto = ReportToBaseDto.builder()
                        .withVersion(currentVersion == null ? configProperties.getBeInst().getVersion()
                                        : currentVersion)
                        .withInstalationId(instalationId)
                        .withCreatedAt(createdAt)
                        .withDetails(details)
                        .build();

        if (configProperties.getCompanies().size() > 0) {
            for (final Company company : configProperties.getCompanies()) {
                Context.get().setCompany(company);
                MDC.put("company", company.getTenant());
                send(dto);
            }
        } else {
            send(dto);
        }
    }

    public void send(final ReportToBaseDto dto)
    {
        final var retryPolicy = RetryPolicy.<Boolean>builder()
                        .withMaxAttempts(20)
                        .withBackoff(Duration.ofSeconds(1), Duration.ofSeconds(300))
                        .handleResultIf(e -> !e)
                        .build();
        final var counter = new AtomicInteger(0);
        Failsafe.with(retryPolicy).get(x -> {
            final var count = counter.incrementAndGet();
            LOG.info("Try {} to send report to base: {}", count, dto);
            final var result = eFapsClient.postReportToBase(dto);
            if (result) {
                registerSync(dto);
            }
            return result;
        });

    }

    private void registerSync(final ReportToBaseDto dto)
    {
        var syncInfo = mongoTemplate.findById(StashId.REPORTTOBASESYNC.getKey(), SyncInfo.class);
        if (syncInfo == null) {
            syncInfo = new SyncInfo();
            syncInfo.setId(StashId.REPORTTOBASESYNC.getKey());
        }
        syncInfo.setLastSync(dto.getCreatedAt().toLocalDateTime());
        mongoTemplate.save(syncInfo);
    }

}
