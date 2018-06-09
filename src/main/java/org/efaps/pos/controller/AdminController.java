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
        this.syncService.syncImages();
        this.syncService.syncSequences();
        this.syncService.syncContacts();
    }

    @GetMapping(path = "/version")
    public String version() {
        return org.efaps.pos.util.Version.VERSION;
    }

}
