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
package org.efaps.pos.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.efaps.pos.dto.WorkspaceDto;
import org.efaps.pos.service.WorkspaceService;
import org.efaps.pos.util.Converter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The Class ProductController.
 */
@RestController
@RequestMapping("workspaces")
public class WorkspaceController
{
    private final WorkspaceService service;

    public WorkspaceController(final WorkspaceService _service) {
        this.service = _service;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<WorkspaceDto> getWorkspaces() {
        return this.service.getWorkspaces().stream()
                        .map(product -> Converter.toDto(product))
                        .collect(Collectors.toList());
    }
}
