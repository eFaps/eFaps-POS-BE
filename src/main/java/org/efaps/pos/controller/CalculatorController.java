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
import org.efaps.pos.dto.CalculatorRequestDto;
import org.efaps.pos.dto.CalculatorResponseDto;
import org.efaps.pos.service.CalculatorService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(IApi.BASEPATH)
public class CalculatorController
{

    private final CalculatorService calculatorService;

    public CalculatorController(final CalculatorService calculatorService)
    {
        this.calculatorService = calculatorService;
    }

    @PostMapping(path = "workspaces/{oid}/calculator", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CalculatorResponseDto calculate(@PathVariable("oid") final String workspaceOid,
                                           @RequestBody final CalculatorRequestDto calculatorPayload)
    {
        return calculatorService.calculate(workspaceOid, calculatorPayload);
    }

}
