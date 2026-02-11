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
package org.efaps.pos.client;

import org.efaps.pos.config.ConfigProperties;
import org.efaps.pos.dto.DNIDto;
import org.efaps.pos.dto.RUCDto;
import org.efaps.pos.sso.SSOClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class EnquiryClient
    extends AbstractRestClient
{

    private static final Logger LOG = LoggerFactory.getLogger(EnquiryClient.class);

    public EnquiryClient(final RestTemplateBuilder restTemplateBuilder,
                         final ConfigProperties config,
                         final SSOClient ssoClient)
    {
        super(restTemplateBuilder, config, ssoClient);
    }

    public DNIDto getDNI(final String number)
    {
        DNIDto ret = null;
        try {
            final var uri = UriComponentsBuilder.fromUri(getConfig().getEnquiry().getBaseUrl())
                            .pathSegment(getConfig().getEnquiry().getDniPath(), "{number}")
                            .buildAndExpand(number)
                            .toUri();

            final var requestEntity = addHeader(RequestEntity.get(uri)).build();
            final ResponseEntity<DNIDto> response = getRestTemplate()
                            .exchange(requestEntity, new ParameterizedTypeReference<DNIDto>()
                            {
                            });
            ret = response.getBody();
        } catch (final RestClientException e) {
            LOG.error("Catched error during retrieval of dni", e);
        }
        return ret;
    }

    public RUCDto getRUC(final String number)
    {
        RUCDto ret = null;
        try {
            final var uri = UriComponentsBuilder.fromUri(getConfig().getEnquiry().getBaseUrl())
                            .pathSegment(getConfig().getEnquiry().getRucPath(), "{number}")
                            .buildAndExpand(number)
                            .toUri();

            final var requestEntity = addHeader(RequestEntity.get(uri)).build();
            final ResponseEntity<RUCDto> response = getRestTemplate()
                            .exchange(requestEntity, new ParameterizedTypeReference<RUCDto>()
                            {
                            });
            ret = response.getBody();
        } catch (final RestClientException e) {
            LOG.error("Catched error during retrieval of dni", e);
        }
        return ret;
    }

    public Page<RUCDto> findRUCs(final Pageable pageable,
                                 final String term)
    {
        Page<RUCDto> ret = null;
        try {
            final var uri = UriComponentsBuilder.fromUri(getConfig().getEnquiry().getBaseUrl())
                            .pathSegment(getConfig().getEnquiry().getRucPath())
                            .queryParam("term", term)
                            .queryParam("size", pageable.getPageSize())
                            .queryParam("page", pageable.getPageNumber())
                            .build()
                            .toUri();

            final var requestEntity = addHeader(RequestEntity.get(uri)).build();

            final ResponseEntity<ClientPage<RUCDto>> response = getRestTemplate()
                            .exchange(requestEntity, new ParameterizedTypeReference<ClientPage<RUCDto>>()
                            {
                            });
            ret = response.getBody();
        } catch (final RestClientException e) {
            LOG.error("Catched error during retrieval of taxpayer", e);
        }
        return ret;
    }
}
