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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile(value = { "static" })
public class StaticWebConfiguration
    implements WebMvcConfigurer
{
    @Value("${spring.resources.static-locations}")
    private String resourcePath;

    @Override
    public void addCorsMappings(final CorsRegistry _registry)
    {
        _registry.addMapping("/**")
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowedOrigins("*").allowedHeaders("*");
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry _registry) {
        _registry.addResourceHandler("/assets/**").addResourceLocations(this.resourcePath + "/assets/");
    }

    @Override
    public void addViewControllers(final ViewControllerRegistry _registry) {
        _registry.addViewController("/pos").setViewName("redirect:/index.html");
        _registry.addViewController("/login").setViewName("redirect:/index.html");
        _registry.addViewController("/products").setViewName("redirect:/index.html");
        _registry.addViewController("/workspaces").setViewName("redirect:/index.html");

    }
}
