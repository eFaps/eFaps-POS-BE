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

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.examples.Expander;
import org.apache.commons.io.FileUtils;
import org.efaps.pos.Application;
import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.dto.UpdateDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UpdateService
{

    private static final Logger LOG = LoggerFactory.getLogger(UpdateService.class);


    private final EFapsClient eFapsClient;

    public UpdateService(final EFapsClient eFapsClient)
    {
        this.eFapsClient = eFapsClient;
    }


    public void check4Update() {
        final var updateDto = eFapsClient.getUpdate();
        LOG.info("updateDto: {}", updateDto);
        try {
            createStructure(updateDto);
        } catch (final IOException e) {
            LOG.error("Well ..no", e);
        }

    }

    public void createStructure(final UpdateDto update)
        throws IOException, ArchiveException
    {
        final var tempFolder = new File(FileUtils.getTempDirectory().getPath(), "pos-update-" + update.getVersion());
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
    }

    public void restart()
    {
        Application.restart();
    }
}
