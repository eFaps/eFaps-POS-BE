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
import org.efaps.pos.dto.EmployeeDto;
import org.efaps.pos.service.EmployeeService;
import org.efaps.pos.util.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(IApi.BASEPATH)
public class EmployeeController
{

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService)
    {
        this.employeeService = employeeService;
    }

    @GetMapping(path = "employees", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmployeeDto> getEmployees()
    {
        return employeeService.getEmployees().stream()
                        .map(_order -> Converter.toDto(_order))
                        .collect(Collectors.toList());
    }

    @GetMapping(path = "employees/{oid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmployeeDto> getEmployee(@PathVariable("oid") final String oid)
    {
        final var employeeOpt = employeeService.getEmployee(oid);
        ResponseEntity<EmployeeDto> ret;
        if (employeeOpt.isPresent()) {
            ret = new ResponseEntity<>(Converter.toDto(employeeOpt.get()), HttpStatus.OK);
        } else {
            ret = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ret;
    }
}
