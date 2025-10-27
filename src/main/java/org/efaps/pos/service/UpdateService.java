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
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.examples.Expander;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.dto.UpdateConfirmationDto;
import org.efaps.pos.dto.UpdateDto;
import org.efaps.pos.dto.UpdateStatus;
import org.efaps.pos.util.VersionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UpdateService
{

    private static final Logger LOG = LoggerFactory.getLogger(UpdateService.class);
    private final EFapsClient eFapsClient;
    private final ConfigService configService;

    public UpdateService(final EFapsClient eFapsClient,
                         final ConfigService configService)
    {
        this.eFapsClient = eFapsClient;
        this.configService = configService;
    }

    public void check4Update()
    {
        final var active = BooleanUtils.toBoolean(configService.getSystemConfig(ConfigService.UPDATE_ACTIVATE));
        if (active) {
            final var updateDto = eFapsClient.getUpdate();
            LOG.info("updateDto: {}", updateDto);
            if (verify(updateDto)) {
                try {
                    final var tempFolder = adhereInstructions(updateDto);
                    final var targetFolder = moveToTarget(tempFolder, updateDto);
                    VersionUtil.createVersionFile(targetFolder, updateDto.getVersion());
                    eFapsClient.confirm(UpdateConfirmationDto.builder()
                                    .withStatus(UpdateStatus.DOWNLOADED)
                                    .withVersion(updateDto.getVersion())
                                    .build());
                } catch (final IOException e) {
                    LOG.error("Update preperation failed", e);
                }
            }
        }
    }

    protected boolean verify(final UpdateDto updateDto)
    {
        boolean ret = false;
        if (updateDto != null) {
            final var currentVersion = VersionUtil.evalVersion();
            if (!updateDto.getVersion().equalsIgnoreCase(currentVersion)) {
                ret = true;
                Path targetFolder;
                if (updateDto.getTargetFolder() != null) {
                    targetFolder = FileSystems.getDefault()
                                    .getPath(updateDto.getTargetFolder())
                                    .toAbsolutePath();
                } else {
                    targetFolder = getTempFolder(updateDto).toPath();
                }
                final var path = VersionUtil.searchVersionFile(targetFolder);
                if (path != null) {
                    final var version = VersionUtil.readContent(path);
                    if (updateDto.getVersion().equals(version)) {
                        ret = false;
                        LOG.info("Found Version: {} in {}", version, path);
                    }
                }
            }
        }
        return ret;
    }

    protected File getTempFolder(final UpdateDto updateDto)
    {
        return new File(FileUtils.getTempDirectory().getPath(), "pos-update-" + updateDto.getVersion());
    }

    protected Path adhereInstructions(final UpdateDto update)
        throws IOException, ArchiveException
    {
        final var tempFolder = getTempFolder(update);
        FileUtils.forceMkdir(tempFolder);
        LOG.info("UpdateFolder: {}", tempFolder);
        final var basePath = tempFolder.toPath();
        for (final var instruction : update.getInstructions()) {
            if (instruction.getFileOid() != null) {
                final var checkout = eFapsClient.checkout(instruction.getFileOid());
                if (checkout != null) {
                    final var targetPath = basePath.resolve(instruction.getTargetPath()).normalize();
                    LOG.info("targetPath: {}", targetPath);
                    final var localFile = new File(targetPath.toFile(), checkout.getFilename());
                    if (!localFile.exists()) {
                        FileUtils.createParentDirectories(localFile);
                        Files.createFile(localFile.toPath());
                        Files.write(localFile.toPath(), checkout.getContent());
                    }
                    if (instruction.isExpand()) {
                        new Expander().expand(localFile.toPath(), targetPath);
                        Files.delete(localFile.toPath());
                    }
                }
            }
        }
        return tempFolder.toPath();
    }

    protected Path moveToTarget(final Path current,
                                final UpdateDto dto)
        throws IOException
    {
        Path target = null;
        if (StringUtils.isNotEmpty(dto.getTargetFolder())) {
            target = FileSystems.getDefault()
                            .getPath(dto.getTargetFolder())
                            .toAbsolutePath();
            if (Files.exists(target)) {
                FileUtils.deleteDirectory(target.toFile());
            }
            FileUtils.moveDirectory(current.toFile(), target.toFile());
        }
        return target == null ? current : target;
    }
}
