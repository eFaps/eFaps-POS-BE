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

import org.efaps.pos.ConfigProperties;
import org.efaps.pos.dto.TaxpayerDto;
import org.efaps.pos.sso.SSOClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class TaxpayerRegistryClient
    extends AbstractRestClient
{
    private static final Logger LOG = LoggerFactory.getLogger(TaxpayerRegistryClient.class);

    public TaxpayerRegistryClient(final RestTemplateBuilder _restTemplateBuilder,
                                  final ConfigProperties _config,
                                  final SSOClient _ssoClient)
    {
        super(_restTemplateBuilder, _config, _ssoClient);
    }

    public TaxpayerDto getTaxpayer(final String _id)
    {
        TaxpayerDto ret = null;
        try {
            final var uri = UriComponentsBuilder.fromUri(getConfig().getTaxpayerRegistry().getBaseUrl())
                .path(getConfig().getTaxpayerRegistry().getQueryPath())
                .queryParam("id", _id)
                .build()
                .toUri();

            final var requestEntity = addHeader(RequestEntity.get(uri)).build();

            final ResponseEntity<TaxpayerDto> response = getRestTemplate()
                            .exchange(requestEntity, new ParameterizedTypeReference<TaxpayerDto>() {});
            ret = response.getBody();
        } catch (final RestClientException e) {
            LOG.error("Catched error during retrieval of taxpayer", e);
        }
        return ret;
    }
}
