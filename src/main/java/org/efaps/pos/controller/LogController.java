package org.efaps.pos.controller;

import org.efaps.pos.ConfigProperties;
import org.efaps.pos.config.IApi;
import org.efaps.pos.dto.LogDto;
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
    public void log(@RequestHeader("X-LOG-TOKEN") final String token, @RequestBody final LogDto logDto)
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
