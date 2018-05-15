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

    /** The threat count. */
    @Value("${org.quartz.jobs.syncReceipts.interval}")
    private Integer syncReceiptsInterval;

    /** The threat count. */
    @Value("${org.quartz.jobs.syncInvoices.interval}")
    private Integer syncInvoicesInterval;

    /** The threat count. */
    @Value("${org.quartz.jobs.syncTickets.interval}")
    private Integer syncTicketsInterval;

    @Autowired
    public QuartzConfig(final SyncService _syncService)
    {
        this.syncService = _syncService;
    }

    @Bean
    public MethodInvokingJobDetailFactoryBean syncReceiptsJobDetailFactoryBean()
    {
        final MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetObject(this.syncService);
        obj.setTargetMethod("syncReceipts");
        return obj;
    }

    @Bean
    public SimpleTriggerFactoryBean syncReceiptsTriggerFactoryBean()
    {
        final SimpleTriggerFactoryBean stFactory = new SimpleTriggerFactoryBean();
        stFactory.setJobDetail(syncReceiptsJobDetailFactoryBean().getObject());
        stFactory.setStartDelay(180 * 1000);
        stFactory.setRepeatInterval(this.syncReceiptsInterval * 1000);
        return stFactory;
    }

    @Bean
    public MethodInvokingJobDetailFactoryBean syncInvoicesJobDetailFactoryBean()
    {
        final MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetObject(this.syncService);
        obj.setTargetMethod("syncInvoices");
        return obj;
    }

    @Bean
    public SimpleTriggerFactoryBean syncInvoicesTriggerFactoryBean()
    {
        final SimpleTriggerFactoryBean stFactory = new SimpleTriggerFactoryBean();
        stFactory.setJobDetail(syncInvoicesJobDetailFactoryBean().getObject());
        stFactory.setStartDelay(180 * 1000);
        stFactory.setRepeatInterval(this.syncInvoicesInterval * 1000);
        return stFactory;
    }

    @Bean
    public MethodInvokingJobDetailFactoryBean syncTicketsJobDetailFactoryBean()
    {
        final MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetObject(this.syncService);
        obj.setTargetMethod("syncTickets");
        return obj;
    }

    @Bean
    public SimpleTriggerFactoryBean syncTicketsTriggerFactoryBean()
    {
        final SimpleTriggerFactoryBean stFactory = new SimpleTriggerFactoryBean();
        stFactory.setJobDetail(syncTicketsJobDetailFactoryBean().getObject());
        stFactory.setStartDelay(180 * 1000);
        stFactory.setRepeatInterval(this.syncTicketsInterval * 1000);
        return stFactory;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean()
    {
        final SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
        scheduler.setTriggers(syncReceiptsTriggerFactoryBean().getObject(), syncInvoicesTriggerFactoryBean()
                        .getObject(), syncTicketsTriggerFactoryBean().getObject());
        final Properties quartzProperties = new Properties();
        quartzProperties.put("org.quartz.threadPool.threadCount", this.threatCount.toString());
        scheduler.setQuartzProperties(quartzProperties);
        return scheduler;
    }
}
