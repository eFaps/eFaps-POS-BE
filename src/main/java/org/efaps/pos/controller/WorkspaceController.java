/*
 * Copyright 2003 - 2019 The eFaps Team
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
package org.efaps.pos.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.efaps.pos.config.IApi;
import org.efaps.pos.dto.WorkspaceDto;
import org.efaps.pos.entity.User;
import org.efaps.pos.service.WorkspaceService;
import org.efaps.pos.util.Converter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The Class ProductController.
 */
@RestController
@RequestMapping(IApi.BASEPATH + "workspaces")
public class WorkspaceController
{
    private final WorkspaceService service;

    public WorkspaceController(final WorkspaceService _service) {
        this.service = _service;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<WorkspaceDto> getWorkspaces(final Authentication _authentication) {
        return this.service.getWorkspaces((User) _authentication.getPrincipal()).stream()
                        .map(ws -> Converter.toDto(ws))
                        .collect(Collectors.toList());
    }

    @GetMapping(path = "/{oid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WorkspaceDto> getWorkspace(final Authentication _authentication,
                                                     @PathVariable("oid") final String _oid) {
        return Optional.ofNullable(this.service.getWorkspace((User) _authentication.getPrincipal(), _oid))
                        .map(ws -> ResponseEntity.ok().body(Converter.toDto(ws)))
                        .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
