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

import org.efaps.pos.service.DemoService;
import org.efaps.pos.service.SyncService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(value = { "!test" })
public class ApplicationStartup
    implements ApplicationListener<ApplicationReadyEvent>
{
    private final ConfigProperties config;
    private final SyncService service;
    private final DemoService demoService;

    public ApplicationStartup(final ConfigProperties _config, final SyncService _service,
                              final DemoService _demoService)
    {
        this.config = _config;
        this.service = _service;
        this.demoService = _demoService;
    }

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent _event)
    {
        if (this.config.isDemoMode()) {
            this.demoService.init();
        } else {
            this.service.syncProducts();
        }
    }
}
