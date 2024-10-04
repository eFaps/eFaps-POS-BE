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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.BooleanUtils;
import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.config.ConfigProperties;
import org.efaps.pos.dto.FileDto;
import org.efaps.pos.entity.PosFile;
import org.efaps.pos.repository.PosFileRepository;
import org.efaps.pos.util.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PosFileService
{

    private static final Logger LOG = LoggerFactory.getLogger(PosFileService.class);

    private final ConfigProperties configProperties;
    private final PosFileRepository posFileRepository;
    private final EFapsClient eFapsClient;
    private final ConfigService configService;

    public PosFileService(final ConfigProperties configProperties,
                          final PosFileRepository posFileRepository,
                          final EFapsClient eFapsClient,
                          final ConfigService configService)
    {
        this.configProperties = configProperties;
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
            files.forEach(this::ensureFile);
        }
    }

    protected void ensureFile(final FileDto dto)
    {
        final var posFile = posFileRepository.save(Converter.toEntity(dto));
        if (configProperties.getBeInst().getFileConfig().getLocationUri() != null) {
            final var fileName = evalFileName(posFile);
            final var folderUri = configProperties.getBeInst().getFileConfig().getLocationUri();
            try {
                final var localFile = new File(folderUri.toURL().getFile() + fileName);
                LOG.debug("Checking file: {}", localFile);
                if (!localFile.exists()) {
                    final var checkout = eFapsClient.checkout(posFile.getOid());
                    if (checkout != null) {
                        Files.createFile(localFile.toPath());
                        Files.write(localFile.toPath(), checkout.getContent());
                    }
                }
            } catch (final IOException e) {
                LOG.error("Something went wrong", e);
            }
        }
    }

    public Optional<PosFile> getFile(String oid)
    {
        return posFileRepository.findById(oid);
    }

    public List<PosFile> findByTag(final String tag,
                                   final String valueRegex)
    {
        return posFileRepository.findByTag(tag, valueRegex);
    }

    public static String evalFileName(final PosFile posFile)
    {
        final var prefix = posFile.getOid().split("\\.")[1];
        return prefix + "_" + posFile.getFileName();
    }
}
