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

import java.net.InetSocketAddress;
import java.net.Proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

@Configuration
public class RestClientConfig
{

    private static final Logger LOG = LoggerFactory.getLogger(RestClientConfig.class);

    private final ConfigProperties configProperties;

    public RestClientConfig(ConfigProperties configProperties)
    {
        this.configProperties = configProperties;
    }

    @Bean
    public RestClientCustomizer customizer()
    {
        return restClientBuilder -> {
            if (configProperties.getProxy() != null && configProperties.getProxy().getUri() != null) {
                final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.example.com", 8080));
                final SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
                factory.setProxy(proxy);
                restClientBuilder.requestFactory(factory);
                LOG.warn("Not completlty implemented!!!!");
            }
            restClientBuilder.requestInterceptor(new ClientRequestLoggingInterceptor())
                            .bufferContent((uri,
                                            httpMethod) -> true);
        };
    }
}
