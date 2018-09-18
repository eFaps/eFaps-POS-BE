package org.efaps.pos.config;

import java.util.Properties;

import org.efaps.pos.service.SyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

@Configuration
@Profile(value = { "!demo" })
public class QuartzConfig
{

    private final SyncService syncService;

    /** The threat count. */
    @Value("${org.quartz.threadPool.threadCount}")
    private Integer threatCount;

    /** The sync interval for receipts. */
    @Value("${org.quartz.jobs.syncPayables.interval}")
    private Integer syncPayablesInterval;

    /** The sync interval for receipts. */
    @Value("${org.quartz.jobs.syncContacts.interval}")
    private Integer syncContactsInterval;

    @Autowired
    public QuartzConfig(final SyncService _syncService)
    {
        this.syncService = _syncService;
    }

    @Bean
    public MethodInvokingJobDetailFactoryBean syncPayablesJobDetailFactoryBean()
    {
        final MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetObject(this.syncService);
        obj.setTargetMethod("syncPayables");
        return obj;
    }

    @Bean
    public SimpleTriggerFactoryBean syncPayablesTriggerFactoryBean()
    {
        final SimpleTriggerFactoryBean stFactory = new SimpleTriggerFactoryBean();
        stFactory.setJobDetail(syncPayablesJobDetailFactoryBean().getObject());
        stFactory.setStartDelay(180 * 1000);
        stFactory.setRepeatInterval(this.syncPayablesInterval * 1000);
        return stFactory;
    }

    @Bean
    public MethodInvokingJobDetailFactoryBean syncContactsJobDetailFactoryBean()
    {
        final MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetObject(this.syncService);
        obj.setTargetMethod("syncContacts");
        return obj;
    }

    @Bean
    public SimpleTriggerFactoryBean syncContactsTriggerFactoryBean()
    {
        final SimpleTriggerFactoryBean stFactory = new SimpleTriggerFactoryBean();
        stFactory.setJobDetail(syncContactsJobDetailFactoryBean().getObject());
        stFactory.setStartDelay(180 * 1000);
        stFactory.setRepeatInterval(this.syncContactsInterval * 1000);
        return stFactory;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean()
    {
        final SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
        scheduler.setTriggers(syncPayablesTriggerFactoryBean().getObject());
        final Properties quartzProperties = new Properties();
        quartzProperties.put("org.quartz.threadPool.threadCount", this.threatCount.toString());
        scheduler.setQuartzProperties(quartzProperties);
        return scheduler;
    }
}
