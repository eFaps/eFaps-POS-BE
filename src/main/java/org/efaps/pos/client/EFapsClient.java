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
package org.efaps.pos.client;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import org.efaps.pos.ConfigProperties;
import org.efaps.pos.dto.CategoryDto;
import org.efaps.pos.dto.PosDto;
import org.efaps.pos.dto.ProductDto;
import org.efaps.pos.dto.ReceiptDto;
import org.efaps.pos.dto.UserDto;
import org.efaps.pos.dto.WorkspaceDto;
import org.efaps.pos.sso.SSOClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class EFapsClient
{

    private final ConfigProperties config;
    private final RestTemplate restTemplate;
    private final SSOClient ssoClient;

    @Autowired
    public EFapsClient(final ConfigProperties _config,
                       final RestTemplate _restTemplate,
                       final SSOClient _ssoClient)
    {
        this.config = _config;
        this.restTemplate = _restTemplate;
        this.ssoClient = _ssoClient;
    }

    public List<ProductDto> getProducts()
    {
        final RequestEntity<?> requestEntity = get(this.config.getEFaps().getProductPath());
        final ResponseEntity<List<ProductDto>> ret = this.restTemplate.exchange(requestEntity,
                        new ParameterizedTypeReference<List<ProductDto>>()
                        {
                        });
        return ret.getBody();
    }

    public List<CategoryDto> getCategories()
    {
        final RequestEntity<?> requestEntity = get(this.config.getEFaps().getCategoryPath());
        final ResponseEntity<List<CategoryDto>> ret = this.restTemplate.exchange(requestEntity,
                        new ParameterizedTypeReference<List<CategoryDto>>()
                        {
                        });
        return ret.getBody();
    }

    public List<WorkspaceDto> getWorkspaces()
    {
        final RequestEntity<?> requestEntity = get(this.config.getEFaps().getWorkspacePath());
        final ResponseEntity<List<WorkspaceDto>> ret = this.restTemplate.exchange(requestEntity,
                        new ParameterizedTypeReference<List<WorkspaceDto>>()
                        {
                        });
        return ret.getBody();
    }

    public List<PosDto> getPOSs()
    {
        final RequestEntity<?> requestEntity = get(this.config.getEFaps().getPosPath());
        final ResponseEntity<List<PosDto>> ret = this.restTemplate.exchange(requestEntity,
                        new ParameterizedTypeReference<List<PosDto>>()
                        {
                        });
        return ret.getBody();
    }

    public List<UserDto> getUsers()
    {
        final RequestEntity<?> requestEntity = get(this.config.getEFaps().getUserPath());
        final ResponseEntity<List<UserDto>> ret = this.restTemplate.exchange(requestEntity,
                        new ParameterizedTypeReference<List<UserDto>>()
                        {
                        });
        return ret.getBody();
    }

    public ReceiptDto postReceipt(final ReceiptDto _receipt) {
        final RequestEntity<ReceiptDto> requestEntity = post(this.config.getEFaps().getReceiptPath(), _receipt);

        final ResponseEntity<ReceiptDto> ret = this.restTemplate.exchange(requestEntity, ReceiptDto.class);
        return ret.getBody();
    }

    public Checkout checkout(final String _oid) {
        final URI uri = UriComponentsBuilder
            .fromUri(this.config.getEFaps().getBaseUrl())
            .path(this.config.getEFaps().getCheckoutPath())
            .queryParam("oid", _oid)
            .build()
            .toUri();
        final RequestEntity<?> requestEntity = get(uri);
        final ResponseEntity<byte[]> response = this.restTemplate.exchange(requestEntity, byte[].class);
        final Checkout ret = new Checkout();
        ret.setContent(response.getBody());
        ret.setContentType(response.getHeaders().getContentType());
        ret.setFilename(response.getHeaders().getContentDisposition().getFilename());
        return ret;
    }

    private String getAuth()
    {
        String auth = "";
        if (this.config.getSso() != null && this.config.getSso().getUrl() != null) {
            auth = "Bearer " + this.ssoClient.getToken();
        } else if (this.config.getAuth() != null) {
            auth = "Basic " + Base64.getEncoder()
                .encodeToString((this.config.getAuth().getUser() + ":" + this.config.getAuth().getPassword())
                                .getBytes(StandardCharsets.UTF_8));
        }
        return auth;
    }

    private UriComponents getUriComponent(final String _path)
    {
        return UriComponentsBuilder
                        .fromUri(this.config.getEFaps().getBaseUrl())
                        .path(_path)
                        .build();
    }

    private RequestEntity<?> get(final URI _uri)
    {
        return RequestEntity.get(_uri)
                        .header("Authorization", getAuth())
                        .build();
    }

    private RequestEntity<?> get(final String _path)
    {
        return get(getUriComponent(_path).toUri());
    }

    private <T> RequestEntity<T> post(final String _path, final T _body)
    {
        return RequestEntity.post(getUriComponent(_path).toUri())
                        .header("Authorization", getAuth())
                        .accept(MediaType.APPLICATION_JSON)
                        .body(_body);
    }
}
