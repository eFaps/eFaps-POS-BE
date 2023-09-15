/*
 * Copyright 2003 - 2023 The eFaps Team
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
package org.efaps.pos.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.efaps.pos.ConfigProperties.LogToken;
import org.efaps.pos.dto.LogEntryDto;
import org.efaps.pos.dto.LogLevel;
import org.efaps.pos.entity.LogEntry;
import org.efaps.pos.listener.ILogListener;
import org.efaps.pos.repository.LogEntryRepository;
import org.springframework.stereotype.Service;

@Service
public class LogService
{

    private final LogEntryRepository logEntryRepository;
    private final List<ILogListener> logListeners;

    public LogService(final LogEntryRepository logEntryRepository,
                      final Optional<List<ILogListener>> logListeners)
    {
        this.logEntryRepository = logEntryRepository;
        this.logListeners = logListeners.isPresent() ? logListeners.get() : Collections.emptyList();
    }

    public void register(final LogToken logToken,
                         final LogEntryDto logDto)
    {
        logEntryRepository.save(new LogEntry()
                        .setIdent(logToken.getIdent())
                        .setKey(logDto.getKey())
                        .setValue(logDto.getValue())
                        .setLevel(logDto.getLevel()));
    }

    public void error(final String ident,
                      final String key,
                      final String value,
                      final Map<String, String> info)
    {
        logEntryRepository.save(new LogEntry()
                        .setIdent(ident)
                        .setKey(key)
                        .setValue(value)
                        .setLevel(LogLevel.ERROR)
                        .setInfo(info));
    }

    public void error(final CollectorException e) {
        logEntryRepository.save(new LogEntry()
                        .setIdent(e.getIdent())
                        .setKey(e.getKey())
                        .setValue(e.getMessage())
                        .setLevel(LogLevel.ERROR)
                        .setInfo(e.getInfo()));
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
}
