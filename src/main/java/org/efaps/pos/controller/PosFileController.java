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

import org.efaps.pos.config.IApi;
import org.efaps.pos.dto.PosFileDto;
import org.efaps.pos.error.NotFoundException;
import org.efaps.pos.service.PosFileService;
import org.efaps.pos.util.Converter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(IApi.BASEPATH + "pos-files")
public class PosFileController
{

    private final PosFileService posFileService;

    public PosFileController(final PosFileService posFileService)
    {
        this.posFileService = posFileService;
    }

    @GetMapping(path = "/{oid}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public PosFileDto getFile(@PathVariable("oid") final String oid)
        throws NotFoundException
    {
        return Converter.toDto(posFileService.getFile(oid).orElseThrow(() -> new NotFoundException("File not found")));
    }

    @GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
    public List<PosFileDto> findByTag(@RequestParam("tag") final String tag,
                                      @RequestParam("value") final String valueRegex)
        throws NotFoundException
    {
        return posFileService.findByTag(tag, valueRegex).stream().map(Converter::toDto).toList();
    }
}
