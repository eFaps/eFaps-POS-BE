/*
 * Copyright 2003 - 2020 The eFaps Team
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
 *
 */
package org.efaps.pos.config;

import org.efaps.pos.service.SyncService;
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

    private final SyncService syncService;

    /** The sync interval for receipts. */
    @Value("${org.quartz.jobs.syncPayables.interval}")
    private Integer syncPayablesInterval;

    /** The sync interval for contacts. */
    @Value("${org.quartz.jobs.syncContacts.interval}")
    private Integer syncContactsInterval;

    /** The sync interval for contacts. */
    @Value("${org.quartz.jobs.syncInventory.interval}")
    private Integer syncInventoryInterval;

    @Value("${org.quartz.jobs.syncExchangeRates.interval:0}")
    private Integer syncExchangeRatesInterval;

    @Value("${org.quartz.jobs.syncEmployees.interval:0}")
    private Integer syncEmployeesInterval;

    /** The sync interval for products. */
    @Value("${org.quartz.jobs.syncProducts.interval:0}")
    private Integer syncProductsInterval;

    @Autowired
    public QuartzConfig(final SyncService _syncService)
    {
        syncService = _syncService;
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
        final SimpleTriggerFactoryBean stFactory = new SimpleTriggerFactoryBean();
        stFactory.setJobDetail(syncPayablesJobDetailFactoryBean().getObject());
        stFactory.setStartDelay(180 * 1000);
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
        final SimpleTriggerFactoryBean stFactory = new SimpleTriggerFactoryBean();
        stFactory.setJobDetail(syncContactsJobDetailFactoryBean().getObject());
        stFactory.setStartDelay(180 * 1000);
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
        final SimpleTriggerFactoryBean stFactory = new SimpleTriggerFactoryBean();
        stFactory.setJobDetail(syncInventoryJobDetailFactoryBean().getObject());
        stFactory.setStartDelay(180 * 1000);
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
        final SimpleTriggerFactoryBean stFactory = new SimpleTriggerFactoryBean();
        stFactory.setJobDetail(syncExchangeRatesJobDetailFactoryBean().getObject());
        stFactory.setStartDelay(180 * 1000);
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
        final SimpleTriggerFactoryBean stFactory = new SimpleTriggerFactoryBean();
        stFactory.setJobDetail(syncEmployeesJobDetailFactoryBean().getObject());
        stFactory.setStartDelay(180 * 1000);
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
        final SimpleTriggerFactoryBean stFactory = new SimpleTriggerFactoryBean();
        stFactory.setJobDetail(syncProductsJobDetailFactoryBean().getObject());
        stFactory.setStartDelay(240 * 1000);
        stFactory.setRepeatInterval(Math.abs(syncProductsInterval) * 1000);
        return stFactory;
    }
}
