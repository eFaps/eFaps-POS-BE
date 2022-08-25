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
package org.efaps.pos.config;

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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
        http.csrf()
            .disable()
            .httpBasic()
            .disable()
            .exceptionHandling()
            .authenticationEntryPoint(unauthorizedHandler)
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers(HttpMethod.POST, IApi.BASEPATH + "authenticate", IApi.BASEPATH + "refreshauth",
                            IApi.BASEPATH + "logs")
            .permitAll()
            .antMatchers(getIgnore())
            .permitAll()
            .anyRequest()
            .authenticated();

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
        return (web) -> web.ignoring().antMatchers(HttpMethod.POST, IApi.BASEPATH + "authenticate")
                        .and()
                        .ignoring().antMatchers(HttpMethod.POST, IApi.BASEPATH + "refreshauth")
                        .and()
                        .ignoring().antMatchers(HttpMethod.POST, IApi.BASEPATH + "logs")
                        .and()
                        .ignoring().antMatchers(HttpMethod.GET, IApi.BASEPATH + "users")
                        .and()
                        .ignoring().antMatchers(HttpMethod.GET, IApi.BASEPATH + "companies")
                        .and()
                        .ignoring().antMatchers(HttpMethod.GET, IApi.BASEPATH + "health")
                        .and()
                        .ignoring().antMatchers(HttpMethod.GET, getIgnore())
                        .and()
                        .ignoring().antMatchers(HttpMethod.OPTIONS)
                        .and()
                        .ignoring().antMatchers("/socket/**");
    }

    private String[] getIgnore()
    {
        return configProperties.getStaticWeb().getIgnore().stream().toArray(String[]::new);
    }
}
