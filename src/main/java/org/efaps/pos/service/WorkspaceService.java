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

package org.efaps.pos.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.efaps.pos.entity.User;
import org.efaps.pos.entity.Workspace;
import org.efaps.pos.repository.WorkspaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkspaceService
{

    private final WorkspaceRepository workspaceRepository;

    @Autowired
    public WorkspaceService(final WorkspaceRepository _workspaceRepository)
    {
        workspaceRepository = _workspaceRepository;
    }

    public List<Workspace> getWorkspaces(final User _user)
    {
        return workspaceRepository.findAll().stream()
                        .filter(ws -> _user.getWorkspaceOids().contains(ws.getOid()))
                        .collect(Collectors.toList());
    }

    public Workspace getWorkspace(final User _user, final String _oid)
    {
        return workspaceRepository.findById(_oid)
                        .filter(ws -> _user.getWorkspaceOids().contains(ws.getOid()))
                        .orElse(null);
    }

    protected Workspace getWorkspace(final String _oid)
    {
        return workspaceRepository.findById(_oid).orElse(null);
    }

    public Optional<String> getWarehouseOid4Workspace(final String _oid)
    {
        final var workspace = getWorkspace(_oid);
        return workspace == null ? Optional.empty() : Optional.ofNullable(workspace.getWarehouseOid());
    }
}
