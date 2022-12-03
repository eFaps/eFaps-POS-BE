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

package org.efaps.pos.filters;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.efaps.pos.util.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.ServletException;

public class JwtAuthorizationTokenFilterTest
{

    private UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();

    @BeforeEach
    public void prepare()
    {
        final var secret = StringUtils.repeat("secret", 20);
        ReflectionTestUtils.setField(jwtTokenUtil, "secret", secret);
        ReflectionTestUtils.setField(jwtTokenUtil, "expiration", 1000L);
    }

    @Test
    public void testIgnore()
        throws ServletException, IOException
    {
        final JwtAuthorizationTokenFilter filter = new JwtAuthorizationTokenFilter(userDetailsService,
                        jwtTokenUtil);
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final MockFilterChain filterChain = new MockFilterChain();
        filter.doFilterInternal(request, response, filterChain);
    }

    @Test
    public void testWrongHeader()
        throws ServletException, IOException
    {
        final JwtAuthorizationTokenFilter filter = new JwtAuthorizationTokenFilter(userDetailsService,
                        jwtTokenUtil);
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Wrong Header");
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final MockFilterChain filterChain = new MockFilterChain();
        filter.doFilterInternal(request, response, filterChain);
    }

    @Test
    public void testWrongBearerHeader()
        throws ServletException, IOException
    {
        final JwtAuthorizationTokenFilter filter = new JwtAuthorizationTokenFilter(userDetailsService,
                        jwtTokenUtil);
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer blablba");
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final MockFilterChain filterChain = new MockFilterChain();

        assertThrows(MalformedJwtException.class, () -> {
            filter.doFilterInternal(request, response, filterChain);
        });
    }
}
