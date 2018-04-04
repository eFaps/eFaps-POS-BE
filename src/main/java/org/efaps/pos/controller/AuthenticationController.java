/*
 * Copyright 2003 - 2018 The eFaps Team
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

import org.efaps.pos.dto.AuthenticationRequestDto;
import org.efaps.pos.dto.AuthenticationResponseDto;
import org.efaps.pos.service.UserService;
import org.efaps.pos.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController
{

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public AuthenticationController(final AuthenticationManager _authenticationManager, final UserService _userService,
                                    final JwtTokenUtil _jwtTokenUtil)
    {
        this.authenticationManager = _authenticationManager;
        this.userService = _userService;
        this.jwtTokenUtil = _jwtTokenUtil;
    }

    @PostMapping(path = "authenticate", produces = MediaType.APPLICATION_JSON_VALUE)
    public AuthenticationResponseDto authenticate(@RequestBody final AuthenticationRequestDto _authenticationRequest)
    {
        authenticate(_authenticationRequest.getUserName(), _authenticationRequest.getPassword());
        final UserDetails userDetails = this.userService.loadUserByUsername(_authenticationRequest.getUserName());
        final String token = this.jwtTokenUtil.generateToken(userDetails);
        return AuthenticationResponseDto.builder().withToken(token).build();
    }

    private void authenticate(final String _userName, final String _password)
    {
        this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(_userName, _password));
    }
}
