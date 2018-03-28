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

package org.efaps.pos;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
public class ConfigProperties
{

    private String name;

    private SSO sso = new SSO();

    private final EFaps efaps = new EFaps();

    public SSO getSso()
    {
        return this.sso;
    }

    public void setSso(final SSO _sso)
    {
        this.sso = _sso;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(final String _name)
    {
        this.name = _name;
    }

    public EFaps getEFaps()
    {
        return this.efaps;
    }

    public static class EFaps
    {

        private URI restUrl;
        private String productPath;

        public URI getRestUrl()
        {
            return this.restUrl;
        }

        public void setRestUrl(final URI _restUrl)
        {
            this.restUrl = _restUrl;
        }

        public String getProductPath()
        {
            return this.productPath;
        }

        public void setProductPath(final String _productPath)
        {
            this.productPath = _productPath;
        }

    }

    public static class SSO
    {

        private String url;
        private final Map<String, String> postValues = new HashMap<>();

        public String getUrl()
        {
            return this.url;
        }

        public void setUrl(final String _url)
        {
            this.url = _url;
        }

        public Map<String, String> getPostValues()
        {
            return this.postValues;
        }
    }
}
