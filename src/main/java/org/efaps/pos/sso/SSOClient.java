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
package org.efaps.pos.sso;

import java.time.LocalDateTime;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.efaps.pos.ConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class SSOClient
{

    private static final Logger LOG = LoggerFactory.getLogger(SSOClient.class);
    private final ConfigProperties config;
    private final RestTemplate restTemplate;
    private String accessToken;
    private String refreshToken;
    private LocalDateTime accessTokenExpires;
    private LocalDateTime refreshTokenExpires;

    @Autowired
    public SSOClient(final ConfigProperties _config,
                     final RestTemplateBuilder _restTemplateBuilder)
    {
        config = _config;
        restTemplate = _restTemplateBuilder.build();
    }

    @SuppressWarnings("rawtypes")
    public void login()
    {
        LOG.debug("Getting new Token from SSO");
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        final MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.set("client_id", config.getSso().getClientId());
        if (StringUtils.isNotEmpty(config.getSso().getClientSecret())) {
            map.set("client_secret", config.getSso().getClientSecret());
        }
        if (refreshToken == null || !LocalDateTime.now().isBefore(refreshTokenExpires)) {
            LOG.debug("  Using Username/Password");
            map.set("grant_type", "password");
            map.set("username", config.getSso().getUsername());
            map.set("password", config.getSso().getPassword());
        } else {
            LOG.debug("  Using RefreshToken");
            map.set("grant_type", "refresh_token");
            map.set("refresh_token", refreshToken);
        }

        final HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<Map> response = null;
        try {
            response = restTemplate.postForEntity(config.getSso().getUrl(), request, Map.class);
        } catch (final RestClientException e) {
            LOG.error("Catched error during retrieval of properties", e);
        } finally {
            if (response == null) {
                accessToken = null;
                refreshToken = null;
                accessTokenExpires = null;
                refreshTokenExpires = null;
            } else {
                accessToken = (String) response.getBody().get("access_token");
                refreshToken = (String) response.getBody().get("refresh_token");

                final Integer expiresIn = (Integer) response.getBody().get("expires_in");
                final Integer refreshExpiresIn = (Integer) response.getBody().get("refresh_expires_in");

                accessTokenExpires = LocalDateTime.now().plusSeconds(expiresIn - 10);
                refreshTokenExpires = LocalDateTime.now().plusSeconds(refreshExpiresIn - 10);
            }
        }
    }

    public String getToken()
    {
        if (accessTokenExpires == null || !LocalDateTime.now().isBefore(accessTokenExpires)) {
            login();
        }
        return accessToken;
    }
}
