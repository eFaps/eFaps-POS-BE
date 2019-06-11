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

import org.efaps.pos.config.IApi;
import org.efaps.pos.service.SyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(IApi.BASEPATH + "admin")
public class AdminController
{

    /** The sync service. */
    private final SyncService syncService;

    @Autowired
    public AdminController(final SyncService _syncService)
    {
        this.syncService = _syncService;
    }

    @GetMapping(path = "/sync")
    public void sync()
    {
        this.syncService.syncProducts();
        this.syncService.syncCategories();
        this.syncService.syncPOSs();
        this.syncService.syncWorkspaces();
        this.syncService.syncUsers();
        this.syncService.syncSequences();
        this.syncService.syncContacts();
        this.syncService.syncWarehouses();
        this.syncService.syncInventory();
        this.syncService.syncPrinters();
        this.syncService.syncImages();
        this.syncService.syncReports();
    }

    @GetMapping(path = "/version")
    public String version() {
        return org.efaps.pos.util.Version.VERSION;
    }

}
