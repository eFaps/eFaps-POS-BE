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

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import org.efaps.pos.ConfigProperties;
import org.efaps.pos.dto.CategoryDto;
import org.efaps.pos.dto.PosDto;
import org.efaps.pos.dto.ProductDto;
import org.efaps.pos.dto.UserDto;
import org.efaps.pos.dto.WorkspaceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
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

    @Autowired
    public EFapsClient(final ConfigProperties _config,
                       final RestTemplate _restTemplate)
    {
        this.config = _config;
        this.restTemplate = _restTemplate;
    }

    private String getAuth()
    {
        String auth = "";
        if (this.config.getAuth() != null) {
            auth = "Basic " + Base64.getEncoder().encodeToString((this.config.getAuth().getUser() + ":" + this.config
                            .getAuth().getPassword()).getBytes(StandardCharsets.UTF_8));
        }
        return auth;
    }

    public List<ProductDto> getProducts()
    {
        final UriComponents uriComponents = UriComponentsBuilder
                        .fromUri(this.config.getEFaps().getRestUrl())
                        .path(this.config.getEFaps().getProductPath())
                        .build();

        final RequestEntity<?> requestEntity = RequestEntity.get(uriComponents.toUri()).header("Authorization",
                        getAuth()).build();
        final ResponseEntity<List<ProductDto>> ret = this.restTemplate.exchange(requestEntity,
                        new ParameterizedTypeReference<List<ProductDto>>()
                        {
                        });
        return ret.getBody();
    }

    public List<CategoryDto> getCategories()
    {
        final UriComponents uriComponents = UriComponentsBuilder
                        .fromUri(this.config.getEFaps().getRestUrl())
                        .path(this.config.getEFaps().getCategoryPath())
                        .build();

        final RequestEntity<?> requestEntity = RequestEntity.get(uriComponents.toUri()).header("Authorization",
                        getAuth()).build();
        final ResponseEntity<List<CategoryDto>> ret = this.restTemplate.exchange(requestEntity,
                        new ParameterizedTypeReference<List<CategoryDto>>()
                        {
                        });
        return ret.getBody();
    }

    public List<WorkspaceDto> getWorkspaces()
    {
        final UriComponents uriComponents = UriComponentsBuilder
                        .fromUri(this.config.getEFaps().getRestUrl())
                        .path(this.config.getEFaps().getWorkspacePath())
                        .build();

        final RequestEntity<?> requestEntity = RequestEntity.get(uriComponents.toUri()).header("Authorization",
                        getAuth()).build();
        final ResponseEntity<List<WorkspaceDto>> ret = this.restTemplate.exchange(requestEntity,
                        new ParameterizedTypeReference<List<WorkspaceDto>>()
                        {
                        });
        return ret.getBody();
    }

    public List<PosDto> getPOSs()
    {
        final UriComponents uriComponents = UriComponentsBuilder
                        .fromUri(this.config.getEFaps().getRestUrl())
                        .path(this.config.getEFaps().getPosPath())
                        .build();

        final RequestEntity<?> requestEntity = RequestEntity.get(uriComponents.toUri()).header("Authorization",
                        getAuth()).build();
        final ResponseEntity<List<PosDto>> ret = this.restTemplate.exchange(requestEntity,
                        new ParameterizedTypeReference<List<PosDto>>()
                        {
                        });
        return ret.getBody();
    }

    public List<UserDto> getUsers()
    {
        final UriComponents uriComponents = UriComponentsBuilder
                        .fromUri(this.config.getEFaps().getRestUrl())
                        .path(this.config.getEFaps().getUserPath())
                        .build();

        final RequestEntity<?> requestEntity = RequestEntity.get(uriComponents.toUri()).header("Authorization",
                        getAuth()).build();
        final ResponseEntity<List<UserDto>> ret = this.restTemplate.exchange(requestEntity,
                        new ParameterizedTypeReference<List<UserDto>>()
                        {
                        });
        return ret.getBody();
    }
}
