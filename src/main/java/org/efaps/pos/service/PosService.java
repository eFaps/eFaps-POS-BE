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
import java.util.stream.Collectors;

import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.entity.Pos;
import org.efaps.pos.entity.SyncInfo;
import org.efaps.pos.repository.PosRepository;
import org.efaps.pos.util.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PosService
{

    private static final Logger LOG = LoggerFactory.getLogger(PosService.class);

    private final PosRepository posRepository;

    private final EFapsClient eFapsClient;

    private final WorkspaceService workspaceService;

    @Autowired
    public PosService(final PosRepository posRepository,
                      final EFapsClient eFapsClient,
                      final WorkspaceService workspaceService)
    {
        this.eFapsClient = eFapsClient;
        this.posRepository = posRepository;
        this.workspaceService = workspaceService;
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

    public boolean syncPOSs(SyncInfo syncInfo)
    {
        LOG.info("Syncing POSs");
        final List<Pos> poss = eFapsClient.getPOSs().stream()
                        .map(Converter::toEntity)
                        .collect(Collectors.toList());
        if (!poss.isEmpty()) {
            final List<Pos> existingPoss = posRepository.findAll();
            existingPoss.forEach(existing -> {
                if (!poss.stream()
                                .filter(pos -> pos.getOid().equals(existing.getOid())).findFirst()
                                .isPresent()) {
                    posRepository.delete(existing);
                }
            });
            poss.forEach(pos -> posRepository.save(pos));
        }
        return true;
    }
}
