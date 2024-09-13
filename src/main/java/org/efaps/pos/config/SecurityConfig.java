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
package org.efaps.pos.config;

import java.util.ArrayList;
import java.util.List;

import org.efaps.pos.ConfigProperties;
import org.efaps.pos.context.ContextFilter;
import org.efaps.pos.controller.JwtAuthenticationEntryPoint;
import org.efaps.pos.filters.JwtAuthorizationTokenFilter;
import org.efaps.pos.service.UserService;
import org.efaps.pos.util.JwtTokenUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
public class SecurityConfig
{

    private final ConfigProperties configProperties;
    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;
    private final ContextFilter contextFilter;

    public SecurityConfig(final ConfigProperties _configProperties,
                          final JwtAuthenticationEntryPoint _unauthorizedHandler,
                          final JwtTokenUtil _jwtTokenUtil,
                          final UserService _userService,
                          final ContextFilter _contextFilter)
    {
        configProperties = _configProperties;
        unauthorizedHandler = _unauthorizedHandler;
        jwtTokenUtil = _jwtTokenUtil;
        userService = _userService;
        contextFilter = _contextFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager()
    {
        final var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(userService.getPasswordEncoder());
        return new ProviderManager(provider);
    }

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http)
        throws Exception
    {
        http.csrf(CsrfConfigurer::disable)
                        .httpBasic(HttpBasicConfigurer::disable)
                        .exceptionHandling(exceptionHandling -> exceptionHandling
                                        .authenticationEntryPoint(unauthorizedHandler))
                        .sessionManagement(sessionManagement -> sessionManagement
                                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .authorizeHttpRequests(authz -> authz
                                        .requestMatchers(
                                                        AntPathRequestMatcher.antMatcher(HttpMethod.POST,
                                                                        IApi.BASEPATH + "authenticate"),
                                                        AntPathRequestMatcher.antMatcher(HttpMethod.POST,
                                                                        IApi.BASEPATH + "refreshauth"),
                                                        AntPathRequestMatcher.antMatcher(HttpMethod.POST,
                                                                        IApi.BASEPATH + "logs"))
                                        .permitAll()
                                        .requestMatchers(getIgnore()).permitAll()
                                        .anyRequest().authenticated());

        // Custom JWT based security filter
        final JwtAuthorizationTokenFilter authenticationTokenFilter = new JwtAuthorizationTokenFilter(
                        userService, jwtTokenUtil);
        http.addFilterBefore(contextFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer()
    {
        return web -> web.ignoring()
                        .requestMatchers(HttpMethod.POST,
                                        IApi.BASEPATH + "authenticate",
                                        IApi.BASEPATH + "refreshauth",
                                        IApi.BASEPATH + "logs")
                        .and()
                        .ignoring()
                        .requestMatchers(HttpMethod.GET,
                                        IApi.BASEPATH + "users",
                                        IApi.BASEPATH + "companies",
                                        IApi.BASEPATH + "health",
                                        IApi.BASEPATH + "pos-files",
                                        IApi.BASEPATH + "pos-files/*")
                        .and()
                        .ignoring()
                        .requestMatchers(HttpMethod.GET, getIgnorePaths())
                        .and()
                        .ignoring()
                        .requestMatchers(HttpMethod.OPTIONS)
                        .and()
                        .ignoring()
                        .requestMatchers("/socket");
    }

    private RequestMatcher[] getIgnore()
    {
        return getIgnorePatterns().stream()
                        .map(AntPathRequestMatcher::antMatcher)
                        .toArray(RequestMatcher[]::new);
    }

    private String[] getIgnorePaths()
    {
        return getIgnorePatterns().stream()
                        .toArray(String[]::new);
    }

    private List<String> getIgnorePatterns()
    {
        final var ret = new ArrayList<>(configProperties.getStaticWeb().getIgnore());
        if (configProperties.getBeInst().getFileConfig().getPath() != null) {
            ret.add(configProperties.getBeInst().getFileConfig().getPath() + "**");
        }
        return ret;
    }
}
