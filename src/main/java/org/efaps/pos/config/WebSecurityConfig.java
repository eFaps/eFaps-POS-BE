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

import org.efaps.pos.JwtAuthorizationTokenFilter;
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
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig
    extends WebSecurityConfigurerAdapter
{

    private final JwtAuthenticationEntryPoint unauthorizedHandler;

    private final JwtTokenUtil jwtTokenUtil;

    private final UserService userService;

    @Autowired
    public WebSecurityConfig(final JwtAuthenticationEntryPoint _unauthorizedHandler,
                             final JwtTokenUtil _jwtTokenUtil,
                             final UserService _userService)
    {
        this.unauthorizedHandler = _unauthorizedHandler;
        this.jwtTokenUtil = _jwtTokenUtil;
        this.userService = _userService;
    }

    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder auth)
        throws Exception
    {
        auth.userDetailsService(this.userService).passwordEncoder(this.userService.getPasswordEncoder());
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
        _httpSecurity.csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(this.unauthorizedHandler)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/authenticate/**").permitAll().anyRequest().authenticated();

        // Custom JWT based security filter
        final JwtAuthorizationTokenFilter authenticationTokenFilter = new JwtAuthorizationTokenFilter(
                        userDetailsService(), this.jwtTokenUtil);
        _httpSecurity.addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(final WebSecurity web)
        throws Exception
    {
        web.ignoring().antMatchers(HttpMethod.POST, IApi.BASEPATH + "authenticate")
            .and()
            .ignoring().antMatchers(HttpMethod.GET, IApi.BASEPATH + "users/**")
            .and()
            .ignoring().antMatchers(HttpMethod.GET, "/pos", "/login", "/products", "/workspaces")
            .and()
            .ignoring().antMatchers(HttpMethod.GET, "/", "/*.html", "/favicon.ico", "/**/*.html",
                                        "/**/*.css", "/**/*.js", "/**/*.json", "/**/*.svg")
            .and()
            .ignoring().antMatchers(HttpMethod.OPTIONS)
            .and()
            .ignoring().antMatchers("/socket/**");
    }
}
