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
package org.efaps.pos.sso;

import java.time.LocalDateTime;
import java.util.Map;

import org.efaps.pos.ConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class SSOClient
{

    private static final Logger LOG = LoggerFactory.getLogger(SSOClient.class);
    private final ConfigProperties config;
    private final RestTemplate restTemplate;
    private String token;
    private LocalDateTime expires;

    @Autowired
    public SSOClient(final ConfigProperties _config,
                     final RestTemplate _restTemplate)
    {
        this.config = _config;
        this.restTemplate = _restTemplate;
    }

    @SuppressWarnings("rawtypes")
    public void login()
    {
        LOG.debug("Getting new Token from SSO");
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        final MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        this.config.getSso().getPostValues().forEach((key, value) -> {
            map.add(key, value);
        });

        final HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        final ResponseEntity<Map> response = this.restTemplate.postForEntity(this.config.getSso().getUrl(), request,
                        Map.class);
        this.token = (String) response.getBody().get("access_token");
        final Integer expiresIn = (Integer) response.getBody().get("expires_in");
        this.expires = LocalDateTime.now().plusSeconds(expiresIn - 10);
    }

    public String getToken()
    {
        if (this.expires == null || !LocalDateTime.now().isBefore(this.expires)) {
            login();
        }
        return this.token;
    }
}
