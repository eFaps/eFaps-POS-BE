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

package org.efaps.pos;

import org.apache.commons.lang3.ArrayUtils;
import org.efaps.pos.service.DemoService;
import org.efaps.pos.service.SyncService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Profile(value = { "!test" })
public class ApplicationStartup
    implements ApplicationListener<ApplicationReadyEvent>
{
    private final SyncService service;
    private final DemoService demoService;
    private final Environment env;

    public ApplicationStartup(final Environment _env,
                              final SyncService _service,
                              final DemoService _demoService)
    {
        this.env = _env;
        this.service = _service;
        this.demoService = _demoService;
    }

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent _event)
    {
        if (ArrayUtils.contains(this.env.getActiveProfiles(), "demo")) {
            this.demoService.init();
        } else {
            this.service.syncProducts();
            this.service.syncCategories();
            this.service.syncPOSs();
            this.service.syncWorkspaces();
            this.service.syncUsers();
            this.service.syncReceipts();
            this.service.syncInvoices();
            this.service.syncTickets();
            this.service.syncImages();
            this.service.syncSequences();
            this.service.syncContacts();
            this.service.syncWarehouses();
            this.service.syncInventory();
            this.service.syncPrinters();;
        }
    }
}
