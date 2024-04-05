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

import org.efaps.pos.config.IApi;
import org.efaps.pos.dto.DNIDto;
import org.efaps.pos.dto.RUCDto;
import org.efaps.pos.service.EnquiryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(IApi.BASEPATH + "enquiry")
public class EnquiryController
{

    private final EnquiryService enquiryService;

    public EnquiryController(final EnquiryService enquiryService)
    {
        this.enquiryService = enquiryService;
    }

    @GetMapping(path = "/dni/{number}", produces = MediaType.APPLICATION_JSON_VALUE)
    public DNIDto getDNI(@PathVariable("number") final String number)
    {
        return enquiryService.getDNI(number);
    }

    @GetMapping(path = "/ruc/{number}", produces = MediaType.APPLICATION_JSON_VALUE)
    public RUCDto getRUC(@PathVariable("number") final String number)
    {
        return enquiryService.getRUC(number);
    }

    @GetMapping(path = "/ruc", params = { "term" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<RUCDto> findRUCs(final Pageable pageable,
                                 @RequestParam("term") final String term)
    {
        return enquiryService.findRUCs(pageable, term);
    }
}
