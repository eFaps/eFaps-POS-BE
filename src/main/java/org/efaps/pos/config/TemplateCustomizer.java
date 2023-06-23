/*
 * Copyright 2003 - 2023 The eFaps Team
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

import java.net.URISyntaxException;
import java.util.Optional;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.core5.http.HttpHost;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class TemplateCustomizer
    implements org.springframework.boot.web.client.RestTemplateCustomizer
{

    @Value("${proxyUri:#{null}}")
    private Optional<String> proxyUri;

    @Override
    public void customize(final RestTemplate _restTemplate)
    {
        if (proxyUri.isPresent()) {
            try {
                final HttpHost proxy = HttpHost.create(proxyUri.get());
                final CloseableHttpClient httpClient = HttpClientBuilder.create()
                                .setRoutePlanner(new DefaultProxyRoutePlanner(proxy)).build();
                _restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(
                                new HttpComponentsClientHttpRequestFactory(httpClient)));
            } catch (final URISyntaxException e) {
                throw new RuntimeException(e);
            }
        } else {
            _restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(_restTemplate.getRequestFactory()));
        }
        _restTemplate.getInterceptors().add(new RequestResponseLoggingInterceptor());
    }
}
