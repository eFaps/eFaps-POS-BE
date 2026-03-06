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
package org.efaps.pos.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VersionUtil
{

    public static String VERSIONFILE = "VERSION";

    private static final Logger LOG = LoggerFactory.getLogger(VersionUtil.class);

    private VersionUtil()
    {
    }

    public static String evalVersion()
    {
        String version = null;
        final var currentFolder = FileSystems.getDefault()
                        .getPath("")
                        .toAbsolutePath();
        final var path = searchVersionFile(currentFolder);
        if (path != null) {
            version = readContent(path);
        }
        return version;
    }

    public static Path searchVersionFile(final Path startFolder)
    {
        LOG.debug("Start searching for version-file in {}", startFolder);
        Path versionFilePath = null;

        final var firstCheck = startFolder.resolve(VERSIONFILE);
        // firstCheck.e
        if (Files.exists(firstCheck)) {
            LOG.info("Found version-file in {}", firstCheck);
            versionFilePath = firstCheck;
        }
        if (versionFilePath == null) {
            LOG.debug("Searching in all child folders");
            try {
                final var fileOpt = Files.walk(startFolder)
                                .filter(Files::isRegularFile)
                                .filter(path -> path.getFileName().toString().equals(VERSIONFILE))
                                .findFirst();
                if (fileOpt.isPresent()) {
                    versionFilePath = fileOpt.get();
                    LOG.info("Found version-file in {}", versionFilePath);
                }
            } catch (final IOException e) {
                LOG.error("Catched error during search for Version file in {}", startFolder);
            }
        }
        if (versionFilePath == null) {
            final var parentFolder = startFolder.getParent();
            LOG.debug("Searching in parent folder: {}", parentFolder);
            final var parentCheck = parentFolder.resolve(VERSIONFILE);
            if (Files.exists(parentCheck)) {
                LOG.info("Found version-file in {}", parentCheck);
                versionFilePath = parentCheck;
            }
        }
        if (versionFilePath == null) {
            LOG.info("No version-file found");
        }
        return versionFilePath;
    }

    public static String readContent(final Path path)
    {
        String content = "";
        try {
            content = Files.readString(path, StandardCharsets.UTF_8);
        } catch (final IOException e) {
            LOG.error("Catched error during reading of Version file: {}", path);
        }
        return content;
    }

    public static void createVersionFile(final Path path,
                                         final String version)
        throws IOException
    {
        final var targetFile = path.resolve(VERSIONFILE);
        Files.write(targetFile, version.getBytes(StandardCharsets.UTF_8));
    }

}
