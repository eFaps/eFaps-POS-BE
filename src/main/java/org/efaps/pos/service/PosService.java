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
package org.efaps.pos.service;

import java.util.List;

import org.efaps.pos.entity.Pos;
import org.efaps.pos.repository.PosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PosService
{
    private final WorkspaceService workspaceService;
    private final PosRepository posRepository;

    @Autowired
    public PosService(final WorkspaceService _workspaceService, final PosRepository _posRepository)
    {
        this.posRepository = _posRepository;
        this.workspaceService = _workspaceService;
    }

    public List<Pos> getPoss()
    {
        return this.posRepository.findAll();
    }

    public Pos getPos(final String _oid)
    {
        return this.posRepository.findById(_oid).get();
    }

    public Pos getPos4Workspace(final String _workspaceOid)
    {
        return getPos(this.workspaceService.getWorkspace(_workspaceOid).getPosOid());
    }
}
