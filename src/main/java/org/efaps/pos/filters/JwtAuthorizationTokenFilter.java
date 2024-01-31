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
package org.efaps.pos.filters;

import java.io.IOException;

import org.efaps.pos.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthorizationTokenFilter
    extends OncePerRequestFilter
{

    private static final Logger LOG = LoggerFactory.getLogger(JwtAuthorizationTokenFilter.class);

    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    public JwtAuthorizationTokenFilter(final UserDetailsService _userDetailsService,
                                       final JwtTokenUtil _jwtTokenUtil)
    {
        userDetailsService = _userDetailsService;
        jwtTokenUtil = _jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest _request, final HttpServletResponse _response,
                                    final FilterChain _chain)
        throws ServletException, IOException
    {
        LOG.debug("processing authentication for '{}'", _request.getRequestURL());

        final String requestHeader = _request.getHeader("Authorization");

        String username = null;
        String authToken = null;
        if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
            authToken = requestHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromAccessToken(authToken);
            } catch (final IllegalArgumentException e) {
                LOG.error("an error occured during getting username from token", e);
            }
        } else {
            LOG.warn("couldn't find bearer string, will ignore the header");
        }

        LOG.debug("checking authentication for user '{}'", username);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            LOG.debug("security context was null, so authorizating user");

            final UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, authToken, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(_request));
                LOG.info("authorizated user '{}', setting security context", username);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        _chain.doFilter(_request, _response);
    }
}
