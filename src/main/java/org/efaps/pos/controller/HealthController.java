package org.efaps.pos.controller;

import org.efaps.pos.config.IApi;
import org.efaps.pos.dto.HealthDto;
import org.efaps.pos.dto.HealthStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(IApi.BASEPATH + "health")
public class HealthController
{

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public HealthDto getHealth()
    {
        return HealthDto.builder()
                        .withStatus(HealthStatus.GREEN)
                        .build();
    }

}
