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

import java.util.List;
import java.util.stream.Collectors;

import org.efaps.pos.config.IApi;
import org.efaps.pos.dto.PosDto;
import org.efaps.pos.service.PosService;
import org.efaps.pos.util.Converter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The Class ProductController.
 */
@RestController
@RequestMapping(IApi.BASEPATH + "poss")
public class PosController
{
    private final PosService service;

    public PosController(final PosService _service) {
        this.service = _service;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PosDto> getPoss() {
        return this.service.getPoss().stream()
                        .map(product -> Converter.toDto(product))
                        .collect(Collectors.toList());
    }

    @GetMapping(path = "/{oid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PosDto getPos(@PathVariable("oid") final String _oid) {
        return Converter.toDto(this.service.getPos(_oid));
    }
}
