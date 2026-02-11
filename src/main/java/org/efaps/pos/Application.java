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
package org.efaps.pos;

import org.efaps.pos.interfaces.IInvoiceListener;
import org.efaps.pos.interfaces.IReceiptListener;
import org.efaps.pos.interfaces.ITicketListener;
import org.efaps.pos.listener.ICollectorListener;
import org.efaps.pos.listener.IConversionListener;
import org.efaps.pos.listener.IDocumentListener;
import org.efaps.pos.listener.ILogListener;
import org.efaps.pos.listener.ILoyaltyListener;
import org.efaps.pos.listener.IMonitoringReportContributor;
import org.efaps.pos.listener.IPrintListener;
import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.springframework.beans.factory.serviceloader.ServiceListFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties
@ConfigurationPropertiesScan
@EnableMongoAuditing
@EnableMongoRepositories("org.efaps.pos.repository")
public class Application
{

    public static void main(final String _args[])
    {
        SpringApplication.run(Application.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new PasswordEncoder()
        {

            private final PasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();

            @Override
            public String encode(final CharSequence _rawPassword)
            {
                return passwordEncryptor.encryptPassword(_rawPassword.toString());
            }

            @Override
            public boolean matches(final CharSequence _rawPassword,
                                   final String _encodedPassword)
            {
                return passwordEncryptor.checkPassword(_rawPassword.toString(), _encodedPassword);
            }
        };
    }

    @Bean(name = "receiptListeners")
    public ServiceListFactoryBean receiptListeners()
    {
        final ServiceListFactoryBean serviceListFactoryBean = new ServiceListFactoryBean();
        serviceListFactoryBean.setServiceType(IReceiptListener.class);
        return serviceListFactoryBean;
    }

    @Bean(name = "invoiceListeners")
    public ServiceListFactoryBean invoiceListeners()
    {
        final ServiceListFactoryBean serviceListFactoryBean = new ServiceListFactoryBean();
        serviceListFactoryBean.setServiceType(IInvoiceListener.class);
        return serviceListFactoryBean;
    }

    @Bean(name = "ticketListeners")
    public ServiceListFactoryBean ticketListeners()
    {
        final ServiceListFactoryBean serviceListFactoryBean = new ServiceListFactoryBean();
        serviceListFactoryBean.setServiceType(ITicketListener.class);
        return serviceListFactoryBean;
    }

    @Bean(name = "collectorListeners")
    public ServiceListFactoryBean collectorListeners()
    {
        final ServiceListFactoryBean serviceListFactoryBean = new ServiceListFactoryBean();
        serviceListFactoryBean.setServiceType(ICollectorListener.class);
        return serviceListFactoryBean;
    }

    @Bean(name = "printListeners")
    public ServiceListFactoryBean printListeners()
    {
        final ServiceListFactoryBean serviceListFactoryBean = new ServiceListFactoryBean();
        serviceListFactoryBean.setServiceType(IPrintListener.class);
        return serviceListFactoryBean;
    }

    @Bean(name = "logListeners")
    public ServiceListFactoryBean logListeners()
    {
        final ServiceListFactoryBean serviceListFactoryBean = new ServiceListFactoryBean();
        serviceListFactoryBean.setServiceType(ILogListener.class);
        return serviceListFactoryBean;
    }

    @Bean(name = "documentListeners")
    public ServiceListFactoryBean documentListeners()
    {
        final ServiceListFactoryBean serviceListFactoryBean = new ServiceListFactoryBean();
        serviceListFactoryBean.setServiceType(IDocumentListener.class);
        return serviceListFactoryBean;
    }

    @Bean(name = "loyaltyListeners")
    public ServiceListFactoryBean loyaltyListeners()
    {
        final ServiceListFactoryBean serviceListFactoryBean = new ServiceListFactoryBean();
        serviceListFactoryBean.setServiceType(ILoyaltyListener.class);
        return serviceListFactoryBean;
    }

    @Bean(name = "monitoringReportContributors")
    public ServiceListFactoryBean monitoringReportContributors()
    {
        final ServiceListFactoryBean serviceListFactoryBean = new ServiceListFactoryBean();
        serviceListFactoryBean.setServiceType(IMonitoringReportContributor.class);
        return serviceListFactoryBean;
    }

    @Bean(name = "conversionListeners")
    public ServiceListFactoryBean conversionListeners()
    {
        final ServiceListFactoryBean serviceListFactoryBean = new ServiceListFactoryBean();
        serviceListFactoryBean.setServiceType(IConversionListener.class);
        return serviceListFactoryBean;
    }

    @Bean
    public TaskExecutor taskExecutor()
    {
        return new SimpleAsyncTaskExecutor();
    }
}
