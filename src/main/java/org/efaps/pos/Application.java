/*
 * Copyright 2003 - 2018 The eFaps Team
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
package org.efaps.pos;

import org.efaps.pos.interfaces.IInvoiceListener;
import org.efaps.pos.interfaces.IReceiptListener;
import org.efaps.pos.interfaces.ITicketListener;
import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.serviceloader.ServiceListFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class Application
{
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(final String _args[]) {
        SpringApplication.run(Application.class);
    }

    @Bean
    public RestTemplate restTemplate(final RestTemplateBuilder _builder) {
        return _builder.build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(final CorsRegistry _registry) {
                _registry.addMapping("/**")
                    .allowedMethods("GET", "POST", "PUT", "DELETE")
                    .allowedOrigins("*")
                    .allowedHeaders("*");
            }
        };
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
                return this.passwordEncryptor.encryptPassword(_rawPassword.toString());
            }

            @Override
            public boolean matches(final CharSequence _rawPassword, final String _encodedPassword)
            {
                return this.passwordEncryptor.checkPassword(_rawPassword.toString(), _encodedPassword);
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
}
