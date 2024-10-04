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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.config.ConfigProperties.LogToken;
import org.efaps.pos.dto.LogEntryDto;
import org.efaps.pos.dto.LogLevel;
import org.efaps.pos.entity.LogEntry;
import org.efaps.pos.listener.ILogListener;
import org.efaps.pos.repository.LogEntryRepository;
import org.efaps.pos.util.Converter;
import org.efaps.pos.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LogService
{

    private static final Logger LOG = LoggerFactory.getLogger(LogService.class);
    private final LogEntryRepository logEntryRepository;
    private final EFapsClient eFapsClient;
    private final List<ILogListener> logListeners;

    public LogService(final LogEntryRepository logEntryRepository,
                      final EFapsClient eFapsClient,
                      final Optional<List<ILogListener>> logListeners)
    {
        this.logEntryRepository = logEntryRepository;
        this.eFapsClient = eFapsClient;
        this.logListeners = logListeners.isPresent() ? logListeners.get() : Collections.emptyList();
    }

    public void register(final LogToken logToken,
                         final LogEntryDto logDto)
    {
        logEntryRepository.save(new LogEntry()
                        .setIdent(logToken.getIdent())
                        .setKey(logDto.getKey())
                        .setMessage(logDto.getMessage())
                        .setValue(logDto.getValue())
                        .setLevel(logDto.getLevel()));
    }

    public void error(final String ident,
                      final String key,
                      final String message,
                      final Map<String, String> value)
    {
        logEntryRepository.save(new LogEntry()
                        .setIdent(ident)
                        .setKey(key)
                        .setMessage(message)
                        .setLevel(LogLevel.ERROR)
                        .setValue(value));
    }

    public void error(final CollectorException e)
    {
        logEntryRepository.save(new LogEntry()
                        .setIdent(e.getIdent())
                        .setKey(e.getKey())
                        .setMessage(e.getMessage())
                        .setLevel(LogLevel.ERROR)
                        .setValue(e.getInfo()));
    }

    public Collection<LogEntry> getEntriesToBeSynced()
    {
        final var toBeSynced = new ArrayList<LogEntry>();
        final var entries = logEntryRepository.findByOidIsNull();
        for (final var entry : entries) {
            boolean sync = true;
            for (final ILogListener listener : logListeners) {
                sync = sync && !listener.vetoSync(entry);
            }
            if (sync) {
                toBeSynced.add(entry);
            }
        }
        return toBeSynced;
    }

    public LogEntry save(final LogEntry entry)
    {
        return logEntryRepository.save(entry);
    }

    public void syncLogs()
    {
        for (final LogEntry entry : getEntriesToBeSynced()) {
            LOG.debug("Syncing LogEntry: {}", entry);
            final var oid = eFapsClient.postLogEntry(Converter.toDto(entry));
            LOG.debug("received oid: {}", oid);
            if (Utils.isOid(oid)) {
                entry.setOid(oid);
                save(entry);
            }
        }
    }
}
