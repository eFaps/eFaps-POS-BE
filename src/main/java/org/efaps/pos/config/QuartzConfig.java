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
package org.efaps.pos.config;

import org.efaps.pos.service.MonitoringService;
import org.efaps.pos.service.SyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

@Configuration
@Profile(value = { "!demo" })
public class QuartzConfig
{
    private static final Logger LOG = LoggerFactory.getLogger(QuartzConfig.class);

    private final SyncService syncService;

    private final MonitoringService monitoringService;

    /** The sync interval for receipts. */
    @Value("${org.quartz.jobs.syncPayables.interval}")
    private Integer syncPayablesInterval;

    @Value("${org.quartz.jobs.syncPayables.delay:180}")
    private Integer syncPayablesDelay;

    /** The sync interval for contacts. */
    @Value("${org.quartz.jobs.syncContacts.interval}")
    private Integer syncContactsInterval;

    @Value("${org.quartz.jobs.syncContacts.delay:180}")
    private Integer syncContactsDelay;

    /** The sync interval for contacts. */
    @Value("${org.quartz.jobs.syncInventory.interval}")
    private Integer syncInventoryInterval;

    @Value("${org.quartz.jobs.syncInventory.delay:180}")
    private Integer syncInventoryDelay;

    @Value("${org.quartz.jobs.syncExchangeRates.interval:0}")
    private Integer syncExchangeRatesInterval;

    @Value("${org.quartz.jobs.syncExchangeRates.delay:240}")
    private Integer syncExchangeRatesDelay;

    @Value("${org.quartz.jobs.syncEmployees.interval:0}")
    private Integer syncEmployeesInterval;

    @Value("${org.quartz.jobs.syncEmployees.delay:360}")
    private Integer syncEmployeesDelay;

    @Value("${org.quartz.jobs.syncProducts.interval:0}")
    private Integer syncProductsInterval;

    @Value("${org.quartz.jobs.syncProducts.delay:240}")
    private Integer syncProductsDelay;

    @Value("${org.quartz.jobs.syncLogs.interval:0}")
    private Integer syncLogsInterval;

    @Value("${org.quartz.jobs.syncLogs.delay:360}")
    private Integer syncLogsDelay;

    @Value("${org.quartz.jobs.syncPromotions.interval:0}")
    private Integer syncPromotionsInterval;

    @Value("${org.quartz.jobs.syncPromotions.delay:240}")
    private Integer syncPromotionsDelay;

    @Value("${org.quartz.jobs.syncPromotionInfos.interval:0}")
    private Integer syncPromotionInfosInterval;

    @Value("${org.quartz.jobs.syncPromotionInfos.delay:360}")
    private Integer syncPromotionInfosDelay;

    @Value("${org.quartz.jobs.reportToBase.interval:3600}")
    private Integer reportToBaseInterval;

    @Value("${org.quartz.jobs.reportToBase.delay:420}")
    private Integer reportToBaseDelay;


    @Value("${org.quartz.jobs.syncPosFiles.interval:0}")
    private Integer syncPosFilesInterval;

    @Value("${org.quartz.jobs.syncPosFiles.delay:360}")
    private Integer syncPosFilesDelay;


    @Autowired
    public QuartzConfig(final SyncService _syncService,
                        final MonitoringService monitoringService)
    {
        syncService = _syncService;
        this.monitoringService = monitoringService;
    }

    @Bean
    @ConditionalOnExpression(value = "#{${org.quartz.jobs.syncPayables.interval} > 0}")
    public MethodInvokingJobDetailFactoryBean syncPayablesJobDetailFactoryBean()
    {
        final MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetObject(syncService);
        obj.setTargetMethod("runSyncJob");
        obj.setArguments("syncPayables");
        obj.setConcurrent(false);
        return obj;
    }

    @Bean
    @ConditionalOnExpression(value = "#{${org.quartz.jobs.syncPayables.interval} > 0}")
    public SimpleTriggerFactoryBean syncPayablesTriggerFactoryBean()
    {
        LOG.info("Registering Quartz trigger 'syncPayables' with delay: {}s, interval: {}s",
                        syncPayablesDelay, syncPayablesInterval);
        final SimpleTriggerFactoryBean stFactory = new SimpleTriggerFactoryBean();
        stFactory.setJobDetail(syncPayablesJobDetailFactoryBean().getObject());
        stFactory.setStartDelay(syncPayablesDelay * 1000);
        stFactory.setRepeatInterval(Math.abs(syncPayablesInterval) * 1000);
        return stFactory;
    }

    @Bean
    @ConditionalOnExpression(value = "#{${org.quartz.jobs.syncContacts.interval} > 0}")
    public MethodInvokingJobDetailFactoryBean syncContactsJobDetailFactoryBean()
    {
        final MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetObject(syncService);
        obj.setTargetMethod("runSyncJob");
        obj.setArguments("syncContacts");
        obj.setConcurrent(false);
        return obj;
    }

    @Bean
    @ConditionalOnExpression(value = "#{${org.quartz.jobs.syncContacts.interval} > 0}")
    public SimpleTriggerFactoryBean syncContactsTriggerFactoryBean()
    {
        LOG.info("Registering Quartz trigger 'syncContacts' with delay: {}s, interval: {}s",
                        syncContactsDelay, syncContactsInterval);
        final SimpleTriggerFactoryBean stFactory = new SimpleTriggerFactoryBean();
        stFactory.setJobDetail(syncContactsJobDetailFactoryBean().getObject());
        stFactory.setStartDelay(syncContactsDelay * 1000);
        stFactory.setRepeatInterval(Math.abs(syncContactsInterval) * 1000);
        return stFactory;
    }

    @Bean
    @ConditionalOnExpression(value = "#{${org.quartz.jobs.syncInventory.interval} > 0}")
    public MethodInvokingJobDetailFactoryBean syncInventoryJobDetailFactoryBean()
    {
        final MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetObject(syncService);
        obj.setTargetMethod("runSyncJob");
        obj.setArguments("syncInventory");
        obj.setConcurrent(false);
        return obj;
    }

    @Bean
    @ConditionalOnExpression(value = "#{${org.quartz.jobs.syncInventory.interval} > 0}")
    public SimpleTriggerFactoryBean syncInventoryTriggerFactoryBean()
    {
        LOG.info("Registering Quartz trigger 'syncInventory' with delay: {}s, interval: {}s",
                        syncInventoryDelay, syncInventoryInterval);
        final SimpleTriggerFactoryBean stFactory = new SimpleTriggerFactoryBean();
        stFactory.setJobDetail(syncInventoryJobDetailFactoryBean().getObject());
        stFactory.setStartDelay(syncInventoryDelay * 1000);
        stFactory.setRepeatInterval(Math.abs(syncInventoryInterval) * 1000);
        return stFactory;
    }

    @Bean
    @ConditionalOnExpression(value = "#{${org.quartz.jobs.syncExchangeRates.interval:0} > 0}")
    public MethodInvokingJobDetailFactoryBean syncExchangeRatesJobDetailFactoryBean()
    {
        final MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetObject(syncService);
        obj.setTargetMethod("runSyncJob");
        obj.setArguments("syncExchangeRates");
        obj.setConcurrent(false);
        return obj;
    }

    @Bean
    @ConditionalOnExpression(value = "#{${org.quartz.jobs.syncExchangeRates.interval:0} > 0}")
    public SimpleTriggerFactoryBean syncExchangeRatesTriggerFactoryBean()
    {
        LOG.info("Registering Quartz trigger 'syncExchangeRates' with delay: {}s, interval: {}s",
                        syncExchangeRatesDelay, syncExchangeRatesInterval);
        final SimpleTriggerFactoryBean stFactory = new SimpleTriggerFactoryBean();
        stFactory.setJobDetail(syncExchangeRatesJobDetailFactoryBean().getObject());
        stFactory.setStartDelay(syncExchangeRatesDelay * 1000);
        stFactory.setRepeatInterval(Math.abs(syncExchangeRatesInterval) * 1000);
        return stFactory;
    }

    @Bean
    @ConditionalOnExpression(value = "#{${org.quartz.jobs.syncEmployees.interval:0} > 0}")
    public MethodInvokingJobDetailFactoryBean syncEmployeesJobDetailFactoryBean()
    {
        final MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetObject(syncService);
        obj.setTargetMethod("runSyncJob");
        obj.setArguments("syncEmployees");
        obj.setConcurrent(false);
        return obj;
    }

    @Bean
    @ConditionalOnExpression(value = "#{${org.quartz.jobs.syncEmployees.interval:0} > 0}")
    public SimpleTriggerFactoryBean syncEmployeesTriggerFactoryBean()
    {
        LOG.info("Registering Quartz trigger 'syncEmployees' with delay: {}s, interval: {}s",
                        syncEmployeesDelay, syncEmployeesInterval);
        final SimpleTriggerFactoryBean stFactory = new SimpleTriggerFactoryBean();
        stFactory.setJobDetail(syncEmployeesJobDetailFactoryBean().getObject());
        stFactory.setStartDelay(syncEmployeesDelay * 1000);
        stFactory.setRepeatInterval(Math.abs(syncEmployeesInterval) * 1000);
        return stFactory;
    }

    @Bean
    @ConditionalOnExpression(value = "#{${org.quartz.jobs.syncProducts.interval:0} > 0}")
    public MethodInvokingJobDetailFactoryBean syncProductsJobDetailFactoryBean()
    {
        final MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetObject(syncService);
        obj.setTargetMethod("runSyncJob");
        obj.setArguments("syncProducts");
        obj.setConcurrent(false);
        return obj;
    }

    @Bean
    @ConditionalOnExpression(value = "#{${org.quartz.jobs.syncProducts.interval:0} > 0}")
    public SimpleTriggerFactoryBean syncProductsTriggerFactoryBean()
    {
        LOG.info("Registering Quartz trigger 'syncProducts' with delay: {}s, interval: {}s",
                        syncProductsDelay, syncProductsInterval);
        final SimpleTriggerFactoryBean stFactory = new SimpleTriggerFactoryBean();
        stFactory.setJobDetail(syncProductsJobDetailFactoryBean().getObject());
        stFactory.setStartDelay(syncProductsDelay * 1000);
        stFactory.setRepeatInterval(Math.abs(syncProductsInterval) * 1000);
        return stFactory;
    }

    @Bean
    @ConditionalOnExpression(value = "#{${org.quartz.jobs.syncLogs.interval:0} > 0}")
    public MethodInvokingJobDetailFactoryBean syncLogsJobDetailFactoryBean()
    {
        final MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetObject(syncService);
        obj.setTargetMethod("runSyncJob");
        obj.setArguments("syncLogs");
        obj.setConcurrent(false);
        return obj;
    }

    @Bean
    @ConditionalOnExpression(value = "#{${org.quartz.jobs.syncLogs.interval:0} > 0}")
    public SimpleTriggerFactoryBean syncLogsTriggerFactoryBean()
    {
        LOG.info("Registering Quartz trigger 'syncLogs' with delay: {}s, interval: {}s",
                        syncLogsDelay, syncLogsInterval);
        final SimpleTriggerFactoryBean stFactory = new SimpleTriggerFactoryBean();
        stFactory.setJobDetail(syncLogsJobDetailFactoryBean().getObject());
        stFactory.setStartDelay(syncLogsDelay * 1000);
        stFactory.setRepeatInterval(Math.abs(syncLogsInterval) * 1000);
        return stFactory;
    }

    @Bean
    @ConditionalOnExpression(value = "#{${org.quartz.jobs.syncPromotions.interval:0} > 0}")
    public MethodInvokingJobDetailFactoryBean syncPromotionsJobDetailFactoryBean()
    {
        final MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetObject(syncService);
        obj.setTargetMethod("runSyncJob");
        obj.setArguments("syncPromotions");
        obj.setConcurrent(false);
        return obj;
    }

    @Bean
    @ConditionalOnExpression(value = "#{${org.quartz.jobs.syncPromotions.interval:0} > 0}")
    public SimpleTriggerFactoryBean syncPromotionsTriggerFactoryBean()
    {
        LOG.info("Registering Quartz trigger 'syncPromotions' with delay: {}s, interval: {}s",
                        syncPromotionsDelay, syncPromotionsInterval);
        final SimpleTriggerFactoryBean stFactory = new SimpleTriggerFactoryBean();
        stFactory.setJobDetail(syncPromotionsJobDetailFactoryBean().getObject());
        stFactory.setStartDelay(syncPromotionsDelay * 1000);
        stFactory.setRepeatInterval(Math.abs(syncPromotionsInterval) * 1000);
        return stFactory;
    }

    @Bean
    @ConditionalOnExpression(value = "#{${org.quartz.jobs.syncPromotionInfos.interval:0} > 0}")
    public MethodInvokingJobDetailFactoryBean syncPromotionInfosJobDetailFactoryBean()
    {
        final MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetObject(syncService);
        obj.setTargetMethod("runSyncJob");
        obj.setArguments("syncPromotionInfos");
        obj.setConcurrent(false);
        return obj;
    }

    @Bean
    @ConditionalOnExpression(value = "#{${org.quartz.jobs.syncPromotionInfos.interval:0} > 0}")
    public SimpleTriggerFactoryBean syncPromotionInfosTriggerFactoryBean()
    {
        LOG.info("Registering Quartz trigger 'syncPromotionInfos' with delay: {}s, interval: {}s",
                        syncPromotionInfosDelay, syncPromotionInfosInterval);
        final SimpleTriggerFactoryBean stFactory = new SimpleTriggerFactoryBean();
        stFactory.setJobDetail(syncPromotionInfosJobDetailFactoryBean().getObject());
        stFactory.setStartDelay(syncPromotionInfosDelay * 1000);
        stFactory.setRepeatInterval(Math.abs(syncPromotionInfosInterval) * 1000);
        return stFactory;
    }

    @Bean
    @ConditionalOnExpression(value = "#{${org.quartz.jobs.reportToBase.interval:3600} > 0}")
    public MethodInvokingJobDetailFactoryBean reportToBaseJobDetailFactoryBean()
    {
        final MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetObject(monitoringService);
        obj.setTargetMethod("reportToBase");
        obj.setConcurrent(false);
        return obj;
    }

    @Bean
    @ConditionalOnExpression(value = "#{${org.quartz.jobs.reportToBase.interval:3600} > 0}")
    public SimpleTriggerFactoryBean reportToBaseTriggerFactoryBean()
    {
        LOG.info("Registering Quartz trigger 'reportToBase' with delay: {}s, interval: {}s",
                        reportToBaseDelay, reportToBaseInterval);

        final SimpleTriggerFactoryBean stFactory = new SimpleTriggerFactoryBean();
        stFactory.setJobDetail(reportToBaseJobDetailFactoryBean().getObject());
        stFactory.setStartDelay(reportToBaseDelay * 1000);
        stFactory.setRepeatInterval(Math.abs(reportToBaseInterval) * 1000);
        return stFactory;
    }


    @Bean
    @ConditionalOnExpression(value = "#{${org.quartz.jobs.syncPosFiles.interval:0} > 0}")
    public MethodInvokingJobDetailFactoryBean syncPosFilesJobDetailFactoryBean()
    {
        final MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetObject(syncService);
        obj.setTargetMethod("runSyncJob");
        obj.setArguments("syncPosFiles");
        obj.setConcurrent(false);
        return obj;
    }

    @Bean
    @ConditionalOnExpression(value = "#{${org.quartz.jobs.syncPosFiles.interval:0} > 0}")
    public SimpleTriggerFactoryBean syncPosFilesTriggerFactoryBean()
    {
        LOG.info("Registering Quartz trigger 'syncPosFiles' with delay: {}s, interval: {}s",
                        syncPosFilesDelay, syncPosFilesInterval);
        final SimpleTriggerFactoryBean stFactory = new SimpleTriggerFactoryBean();
        stFactory.setJobDetail(syncPosFilesJobDetailFactoryBean().getObject());
        stFactory.setStartDelay(syncPosFilesDelay * 1000);
        stFactory.setRepeatInterval(Math.abs(syncPosFilesInterval) * 1000);
        return stFactory;
    }

}
