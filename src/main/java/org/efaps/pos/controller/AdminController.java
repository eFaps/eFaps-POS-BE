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
package org.efaps.pos.controller;

import java.util.List;

import org.efaps.pos.config.ConfigProperties;
import org.efaps.pos.config.IApi;
import org.efaps.pos.dto.PosVersionsDto;
import org.efaps.pos.service.ConfigService;
import org.efaps.pos.service.SyncService;
import org.efaps.pos.service.SyncService.SyncDirective;
import org.efaps.pos.util.SyncServiceDeactivatedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(IApi.BASEPATH + "admin")
public class AdminController
{

    private final ConfigService configService;
    /** The sync service. */
    private final SyncService syncService;

    private final ConfigProperties properties;

    @Autowired
    public AdminController(final ConfigService configService,
                           final ConfigProperties _properties,
                           final SyncService _syncService)
    {
        this.configService = configService;
        syncService = _syncService;
        properties = _properties;
    }

    @GetMapping(path = "/sync")
    public void sync(@RequestParam(name = "syncDirective", required = false, defaultValue = "ALL") List<SyncDirective> syncDirectives)
        throws SyncServiceDeactivatedException
    {
        syncService.syncManual(syncDirectives);
    }

    @GetMapping(path = "/versions")
    public PosVersionsDto version()
    {
        final String remote = configService.getOrDefault("org.efaps.pos.Version", "0.0.0");
        return PosVersionsDto.builder()
                        .withRemote(remote)
                        .withLocal(properties.getBeInst().getVersion()).build();
    }
}
