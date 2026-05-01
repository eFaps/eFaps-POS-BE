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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.entity.Employee;
import org.efaps.pos.entity.SyncInfo;
import org.efaps.pos.repository.EmployeeRepository;
import org.efaps.pos.util.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService
{

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeRepository employeeRepository;
    private final EFapsClient eFapsClient;

    public EmployeeService(final EmployeeRepository employeeRepository,
                           final EFapsClient eFapsClient)
    {
        this.employeeRepository = employeeRepository;
        this.eFapsClient = eFapsClient;
    }

    public List<Employee> getEmployees()
    {
        return employeeRepository.findAll();
    }

    public Optional<Employee> getEmployee(String oid)
    {
        return employeeRepository.findById(oid);
    }

    public boolean syncEmployees(SyncInfo syncInfo)

    {
        LOG.info("Syncing Employees");
        final List<Employee> employees = eFapsClient.getEmployees().stream()
                        .map(Converter::toEntity)
                        .collect(Collectors.toList());
        if (!employees.isEmpty()) {
            final List<Employee> existingEmployees = employeeRepository.findAll();
            existingEmployees.forEach(existing -> {
                if (!employees.stream().filter(employee -> employee.getOid().equals(existing.getOid())).findFirst()
                                .isPresent()) {
                    employeeRepository.delete(existing);
                }
            });
            employees.forEach(entity -> employeeRepository.save(entity));
        }
        return true;
    }
}
