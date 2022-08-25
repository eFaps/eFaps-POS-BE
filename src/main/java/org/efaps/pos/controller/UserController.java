/*
 * Copyright 2003 - 2022 The eFaps Team
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
package org.efaps.pos.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.efaps.pos.config.IApi;
import org.efaps.pos.dto.PosUserDto;
import org.efaps.pos.entity.User;
import org.efaps.pos.service.UserService;
import org.efaps.pos.util.Converter;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(IApi.BASEPATH + "users")
public class UserController
{

    private final UserService service;

    public UserController(final UserService _service)
    {
        service = _service;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PosUserDto> getUsers()
    {
        return service.getUsers().stream()
                        .map(user -> Converter.toDto(user))
                        .collect(Collectors.toList());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/current")
    public PosUserDto getCurrentUser(final Authentication _authentication)
    {
        final var user = (User) _authentication.getPrincipal();
        return Converter.toDto(user);
    }
}
