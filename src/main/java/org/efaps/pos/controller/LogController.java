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
package org.efaps.pos.controller;

import org.efaps.pos.config.ConfigProperties;
import org.efaps.pos.config.IApi;
import org.efaps.pos.dto.LogEntryDto;
import org.efaps.pos.service.LogService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@RequestMapping(IApi.BASEPATH + "logs")
public class LogController
{

    private final ConfigProperties configProperties;
    private final LogService logService;

    public LogController(final ConfigProperties configProperties,
                         final LogService logService)
    {
        this.configProperties = configProperties;
        this.logService = logService;
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public void log(@RequestHeader("X-LOG-TOKEN") final String token,
                    @RequestBody final LogEntryDto logDto)
    {
        final var logtoken = configProperties.getLogTokens().stream()
                        .filter(logToken -> logToken.getToken().equals(token))
                        .findFirst();
        if (logtoken.isEmpty()) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
        logService.register(logtoken.get(), logDto);
    }
}
