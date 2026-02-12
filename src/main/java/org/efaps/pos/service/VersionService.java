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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.efaps.pos.listener.IVersionListener;
import org.efaps.pos.util.VersionUtil;
import org.springframework.stereotype.Service;

@Service
public class VersionService
{
    private static String REMOTE = "remote";
    private static String LOCAL = "local";

    private final ConfigService configService;
    private final List<IVersionListener> versionListeners;

    public VersionService(final ConfigService configService,
                          final Optional<List<IVersionListener>> versionListeners)
    {
        this.configService = configService;
        this.versionListeners = versionListeners.isPresent() ? versionListeners.get() : Collections.emptyList();
    }

    public Map<String, String> getVersions()
    {
        final var map = new HashMap<String, String>();
        final String remote = configService.getOrDefault("org.efaps.pos.Version", "0.0.0");
        map.put(REMOTE, remote);
        final var local = VersionUtil.evalVersion();
        map.put(LOCAL, local);

        for (final var listener : versionListeners) {
            listener.add2Versions(map);
        }
        return map;
    }

}
