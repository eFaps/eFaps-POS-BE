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
import org.efaps.pos.dto.AuthenticationRefreshDto;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(IApi.BASEPATH)
public class AuthenticationController
{

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public AuthenticationController(final AuthenticationManager _authenticationManager, final UserService _userService,
                                    final JwtTokenUtil _jwtTokenUtil)
    {
        authenticationManager = _authenticationManager;
        userService = _userService;
        jwtTokenUtil = _jwtTokenUtil;
    }

    @PostMapping(path = "authenticate", produces = MediaType.APPLICATION_JSON_VALUE)
    public AuthenticationResponseDto authenticate(@RequestBody final AuthenticationRequestDto _authenticationRequest)
    {
        authenticate(_authenticationRequest.getUserName(), _authenticationRequest.getPassword());
        return getAuthResponse(_authenticationRequest.getUserName());
    }

    private void authenticate(final String _userName, final String _password)
    {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(_userName, _password));
    }

    private AuthenticationResponseDto getAuthResponse(final String _userName) {
        final UserDetails userDetails = userService.loadUserByUsername(_userName);
        final String accessToken = jwtTokenUtil.generateAccessToken(userDetails);
        final String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);
        return AuthenticationResponseDto.builder()
                        .withAccessToken(accessToken)
                        .withRefreshToken(refreshToken)
                        .build();
    }

    @PostMapping(path = "refreshauth", produces = MediaType.APPLICATION_JSON_VALUE)
    public AuthenticationResponseDto refresh(@RequestBody final AuthenticationRefreshDto _authenticationRefresh)
    {
        final String userName = jwtTokenUtil.getUsernameFromRefreshToken(_authenticationRefresh.getRefreshToken());
        return getAuthResponse(userName);
    }
}
