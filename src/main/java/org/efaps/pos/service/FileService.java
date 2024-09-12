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

import org.apache.commons.lang3.BooleanUtils;
import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.repository.PosFileRepository;
import org.efaps.pos.util.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FileService
{

    private static final Logger LOG = LoggerFactory.getLogger(FileService.class);

    private final PosFileRepository posFileRepository;
    private final EFapsClient eFapsClient;
    private final ConfigService configService;

    public FileService(final PosFileRepository posFileRepository,
                       final EFapsClient eFapsClient,
                       final ConfigService configService)
    {
        this.posFileRepository = posFileRepository;
        this.eFapsClient = eFapsClient;
        this.configService = configService;
    }

    public void syncFiles()
    {
        final var active = BooleanUtils.toBoolean(configService.getSystemConfig(ConfigService.FILE_ACTIVATE));
        if (active) {
            LOG.info("Syncing Files");
            final var files = eFapsClient.getFiles();
            files.forEach(file -> posFileRepository.save(Converter.toEntity(file)));
        }

    }

}
