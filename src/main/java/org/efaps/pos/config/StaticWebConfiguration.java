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

import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final ConfigProperties configProperties;

    private static final Logger LOG = LoggerFactory.getLogger(StaticWebConfiguration.class);

    public StaticWebConfiguration(final ConfigProperties configProperties)
    {
        this.configProperties = configProperties;
    }

    @Override
    public void addCorsMappings(final CorsRegistry registry)
    {
        registry.addMapping("/**")
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowedOrigins("*").allowedHeaders("*");
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry)
    {
        for (final Entry<String, String> resource : configProperties.getStaticWeb().getResources().entrySet()) {
            LOG.info("Adding resource '{}' : '{}'", resource.getKey(), resource.getValue());
            registry.addResourceHandler(resource.getKey())
                            .addResourceLocations(resource.getValue());
        }

        if (configProperties.getBeInst().getFileConfig().getLocationUri() != null
                        && configProperties.getBeInst().getFileConfig().getPath() != null) {
            registry.addResourceHandler(configProperties.getBeInst().getFileConfig().getPath() + "**")
                            .addResourceLocations(
                                            configProperties.getBeInst().getFileConfig().getLocationUri().toString());
        }
    }

    @Override
    public void addViewControllers(final ViewControllerRegistry registry)
    {
        registry.addViewController("/pos").setViewName("redirect:/index.html");
        registry.addViewController("/login").setViewName("redirect:/index.html");
        registry.addViewController("/products").setViewName("redirect:/index.html");
        registry.addViewController("/workspaces").setViewName("redirect:/index.html");

        for (final Entry<String, String> view : configProperties.getStaticWeb().getViews().entrySet()) {
            LOG.info("Adding view '{}' : '{}'", view.getKey(), view.getValue());
            registry.addViewController(view.getKey()).setViewName(view.getValue());
        }
    }
}
