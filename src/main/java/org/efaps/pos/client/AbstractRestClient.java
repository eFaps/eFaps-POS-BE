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
package org.efaps.pos.client;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.commons.lang3.StringUtils;
import org.efaps.pos.config.ConfigProperties;
import org.efaps.pos.context.Context;
import org.efaps.pos.sso.SSOClient;
import org.springframework.http.client.ClientHttpRequestInitializer;
import org.springframework.web.client.RestClient;

public abstract class AbstractRestClient
{

    private final RestClient restClient;
    private final ConfigProperties config;
    private final SSOClient ssoClient;

    public AbstractRestClient(final RestClient.Builder restClientBuilder,
                              final ConfigProperties config,
                              final SSOClient ssoClient)
    {
        this.config = config;
        this.ssoClient = ssoClient;
        restClient = restClientBuilder
                        .baseUrl(getBaseUrl())
                        .requestInitializer(getRequestInitializer())
                        .build();

    }

    protected abstract URI getBaseUrl();

    protected ClientHttpRequestInitializer getRequestInitializer()
    {
        return request -> {
            request.getHeaders().set("Authorization", getAuthorization());
            if (Context.get().getCompany() != null) {
                request.getHeaders().set("X-CONTEXT-COMPANY", Context.get().getCompany().getKey());
            }
        };
    }

    protected String getAuthorization()
    {
        String auth = "";
        if (getConfig().getSso() != null && StringUtils.isNotEmpty(getConfig().getSso().getUrl())) {
            auth = "Bearer " + getSSOClient().getToken();
        } else if (config.getAuth() != null) {
            auth = "Basic " + Base64.getEncoder().encodeToString((getConfig().getAuth().getUser() + ":" + getConfig()
                            .getAuth().getPassword()).getBytes(StandardCharsets.UTF_8));
        }
        return auth;
    }

    public RestClient getRestClient()
    {
        return restClient;
    }

    public ConfigProperties getConfig()
    {
        return config;
    }

    public SSOClient getSSOClient()
    {
        return ssoClient;
    }
}
