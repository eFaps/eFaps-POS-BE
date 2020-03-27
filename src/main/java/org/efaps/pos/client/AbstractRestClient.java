
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

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.commons.lang3.StringUtils;
import org.efaps.pos.ConfigProperties;
import org.efaps.pos.context.Context;
import org.efaps.pos.sso.SSOClient;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity.HeadersBuilder;
import org.springframework.web.client.RestTemplate;

public abstract class AbstractRestClient
{
    private final RestTemplate restTemplate;
    private final ConfigProperties config;
    private final SSOClient ssoClient;

    public AbstractRestClient(final RestTemplateBuilder _restTemplateBuilder,
                              final ConfigProperties _config,
                              final SSOClient _ssoClient) {
        restTemplate = _restTemplateBuilder.build();
        config = _config;
        ssoClient = _ssoClient;
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

    protected <T extends HeadersBuilder<T>> HeadersBuilder<T> addHeader(final HeadersBuilder<T> _builder)
    {
        HeadersBuilder<T> ret = _builder.header("Authorization", getAuthorization());
        if (Context.get().getCompany() != null) {
            ret = ret.header("X-CONTEXT-COMPANY", Context.get().getCompany().getKey());
        }
        return ret;
    }


    public RestTemplate getRestTemplate()
    {
        return restTemplate;
    }

    public ConfigProperties getConfig() {
        return config;
    }

    public SSOClient getSSOClient()
    {
        return ssoClient;
    }
}
