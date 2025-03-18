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
package org.efaps.pos;

import org.apache.commons.lang3.ArrayUtils;
import org.efaps.pos.config.ConfigProperties;
import org.efaps.pos.config.ConfigProperties.Company;
import org.efaps.pos.context.Context;
import org.efaps.pos.service.DemoService;
import org.efaps.pos.service.SyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Component
@Profile(value = { "!test" })
public class ApplicationStartup
    implements ApplicationListener<ApplicationReadyEvent>
{

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationStartup.class);

    private final Environment env;
    private final ConfigProperties configProperties;
    private final TaskExecutor taskExecutor;
    private final SyncService syncService;
    private DemoService demoService;

    public ApplicationStartup(final Environment env,
                              final ConfigProperties configProperties,
                              final TaskExecutor taskExecutor,
                              final SyncService syncService)
    {
        this.env = env;
        this.configProperties = configProperties;
        this.syncService = syncService;
        this.taskExecutor = taskExecutor;
    }

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent _event)
    {
        if (ArrayUtils.contains(env.getActiveProfiles(), "demo")) {
            syncService.setDeactivated(true);
            demoService.init();
        } else if (configProperties.getBeInst().isSyncOnStartup()) {
            if (configProperties.getCompanies().size() > 0) {
                for (final Company company : configProperties.getCompanies()) {
                    Context.get().setCompany(company);
                    MDC.put("company", company.getTenant());
                    sync();
                }
            } else {
                sync();
            }
        }
    }

    private void sync()
    {
      final var company = Context.get().getCompany();
      taskExecutor.execute(() -> {
            try {
                if (company != null) {
                    Context.get().setCompany(company);
                }
                syncService.syncProperties();
                syncService.syncUsers();
                syncService.syncExchangeRates();
                syncService.syncPOSs();
                syncService.syncWorkspaces();
                syncService.syncAllProducts();
                syncService.syncCategories();
                syncService.syncPromotions();
                syncService.syncBalance();
                syncService.syncReceipts();
                syncService.syncInvoices();
                syncService.syncTickets();
                syncService.syncSequences();
                syncService.syncWarehouses();
                syncService.syncInventory();
                syncService.syncPrinters();
                syncService.syncImages();
                syncService.syncReports();
                syncService.syncOrders();
                syncService.syncAllContacts();
                syncService.syncPosFiles();
                syncService.check4Update();
            } catch (final Exception e) {
                LOG.error("Catched error during startup sync", e);
            }
        });
    }

    @Autowired(required = false)
    public void setDemoService(final DemoService _demoService)
    {
        demoService = _demoService;
    }
}
