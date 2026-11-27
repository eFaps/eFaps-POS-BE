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

import java.net.URI;

import org.efaps.pos.config.ConfigProperties;
import org.efaps.pos.dto.DNIDto;
import org.efaps.pos.dto.DistrictDto;
import org.efaps.pos.dto.RUCDto;
import org.efaps.pos.sso.SSOClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class EnquiryClient
    extends AbstractRestClient
{

    private static final Logger LOG = LoggerFactory.getLogger(EnquiryClient.class);

    public EnquiryClient(final RestClient.Builder restClientBuilder,
                         final ConfigProperties config,
                         final SSOClient ssoClient)
    {
        super(restClientBuilder, config, ssoClient);
    }

    public DNIDto getDNI(final String number)
    {
        DNIDto ret = null;
        try {
            final var uri = UriComponentsBuilder.fromUri(getConfig().getEnquiry().getBaseUrl())
                            .pathSegment(getConfig().getEnquiry().getDniPath(), "{number}")
                            .buildAndExpand(number)
                            .toUri();
            final var responseSpec = getRestClient().get().uri(uri).retrieve();
            ret = responseSpec.body(DNIDto.class);
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

            final var responseSpec = getRestClient().get().uri(uri).retrieve();
            ret = responseSpec.body(RUCDto.class);
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
            final var responseSpec = getRestClient().get().uri(uri).retrieve();
            ret = responseSpec.body(new ParameterizedTypeReference<ClientPage<RUCDto>>()
            {
            });
        } catch (final RestClientException e) {
            LOG.error("Catched error during retrieval of taxpayer", e);
        }
        return ret;
    }

    public DistrictDto getDistrict(Double lat,
                                   Double lon)
    {
        DistrictDto ret = null;
        try {
            final var uri = UriComponentsBuilder.fromUri(getConfig().getEnquiry().getBaseUrl())
                            .pathSegment(getConfig().getEnquiry().getDistrictPath())
                            .queryParam("lat", lat)
                            .queryParam("lon", lon)
                            .build()
                            .toUri();

            final var responseSpec = getRestClient().get().uri(uri).retrieve();
            ret = responseSpec.body(DistrictDto.class);

        } catch (final HttpStatusCodeException e) {
            if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                LOG.info("Not found for lat: {}, lon: {}", lat, lon);
            }
        } catch (final RestClientException e) {
            LOG.error("Catched error during retrieval of DistrictDto", e);
        }
        return ret;
    }

    @Override
    protected URI getBaseUrl()
    {
        return getConfig().getEnquiry().getBaseUrl();
    }
}
