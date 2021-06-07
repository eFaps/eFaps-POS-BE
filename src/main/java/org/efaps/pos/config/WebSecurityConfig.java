/*
 * Copyright 2003 - 2019 The eFaps Team
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
import org.efaps.pos.JwtAuthorizationTokenFilter;
import org.efaps.pos.context.ContextFilter;
import org.efaps.pos.controller.JwtAuthenticationEntryPoint;
import org.efaps.pos.service.UserService;
import org.efaps.pos.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class WebSecurityConfig
    extends WebSecurityConfigurerAdapter
{
    private final ConfigProperties configProperties;

    private final JwtAuthenticationEntryPoint unauthorizedHandler;

    private final JwtTokenUtil jwtTokenUtil;

    private final UserService userService;

    private final ContextFilter contextFilter;

    @Autowired
    public WebSecurityConfig(final ConfigProperties _configProperties,
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

    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder auth)
        throws Exception
    {
        auth.userDetailsService(userService).passwordEncoder(userService.getPasswordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean()
        throws Exception
    {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(final HttpSecurity _httpSecurity)
        throws Exception
    {
        _httpSecurity
            .csrf()
                .disable()
            .httpBasic()
                .disable()
            .exceptionHandling()
                .authenticationEntryPoint(unauthorizedHandler)
                .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .authorizeRequests()
                .antMatchers(HttpMethod.POST, IApi.BASEPATH + "authenticate", IApi.BASEPATH + "refreshauth")
                .permitAll()
                .antMatchers(getIgnore())
                .permitAll()
                .anyRequest()
                .authenticated();

        // Custom JWT based security filter
        final JwtAuthorizationTokenFilter authenticationTokenFilter = new JwtAuthorizationTokenFilter(
                        userDetailsService(), jwtTokenUtil);
        _httpSecurity.addFilterBefore(contextFilter, UsernamePasswordAuthenticationFilter.class);
        _httpSecurity.addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
      //  _httpSecurity.addFilterBefore(contextFilter, JwtAuthorizationTokenFilter.class);
    }

    @Override
    public void configure(final WebSecurity _web)
    {
        _web.ignoring().antMatchers(HttpMethod.POST, IApi.BASEPATH + "authenticate")
            .and()
            .ignoring().antMatchers(HttpMethod.POST, IApi.BASEPATH + "refreshauth")
            .and()
            .ignoring().antMatchers(HttpMethod.GET, IApi.BASEPATH + "users/**")
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

    private String[] getIgnore() {
        return configProperties.getStaticWeb().getIgnore().stream().toArray(String[]::new);
    }
}
