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

import java.net.URISyntaxException;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class TemplateCustomizer
    implements org.springframework.boot.restclient.RestTemplateCustomizer
{
    private static final Logger LOG = LoggerFactory.getLogger(TemplateCustomizer.class);

    private final ConfigProperties configProperties;

    public TemplateCustomizer(ConfigProperties configProperties)
    {
        this.configProperties = configProperties;
    }

    @Override
    public void customize(final RestTemplate _restTemplate)
    {
        if (configProperties.getProxy() != null && configProperties.getProxy().getUri() != null) {
            LOG.info("Configuring Proxy: {}", configProperties.getProxy().getUri());
            try {
                final HttpHost proxy = HttpHost.create(configProperties.getProxy().getUri());
                final CloseableHttpClient httpClient = HttpClientBuilder.create()
                                .setRoutePlanner(new DefaultProxyRoutePlanner(proxy)
                                {

                                    @Override
                                    public HttpHost determineProxy(HttpHost target,
                                                                   HttpContext context)
                                        throws HttpException
                                    {
                                        if (configProperties.getProxy().getIgnoreHostNames() != null
                                                        && configProperties.getProxy().getIgnoreHostNames()
                                                                        .contains(target.getHostName())) {
                                            LOG.debug("Ignoring Proxy for : {}", target.getHostName());
                                            return null;
                                        }
                                        LOG.debug("applying Proxy for : {}", target.getHostName());
                                        return super.determineProxy(target, context);
                                    }

                                }).build();
                _restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(
                                new HttpComponentsClientHttpRequestFactory(httpClient)));
            } catch (final URISyntaxException e) {
                throw new RuntimeException(e);
            }
        } else {
            _restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(_restTemplate.getRequestFactory()));
        }
        _restTemplate.getInterceptors().add(new ClientRequestLoggingInterceptor());
    }
}
