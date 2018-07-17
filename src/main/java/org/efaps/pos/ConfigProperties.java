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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
public class ConfigProperties
{

    private String name;

    private final SSO sso = new SSO();

    private final EFaps efaps = new EFaps();

    private final BasicAuth auth = new BasicAuth();

    public String getName()
    {
        return this.name;
    }

    public void setName(final String _name)
    {
        this.name = _name;
    }

    public SSO getSso()
    {
        return this.sso;
    }

    public EFaps getEFaps()
    {
        return this.efaps;
    }

    public BasicAuth getAuth()
    {
        return this.auth;
    }

    public static class BasicAuth
    {

        private String user;
        private String password;

        public String getPassword()
        {
            return this.password;
        }

        public void setPassword(final String _password)
        {
            this.password = _password;
        }

        public String getUser()
        {
            return this.user;
        }

        public void setUser(final String _user)
        {
            this.user = _user;
        }
    }

    public static class EFaps
    {

        private URI baseUrl;
        private String backendPath;
        private String productPath;
        private String categoryPath;
        private String workspacePath;
        private String posPath;
        private String userPath;
        private String receiptPath;
        private String invoicePath;
        private String ticketPath;
        private String checkoutPath;
        private String sequencePath;
        private String contactPath;
        private String warehousePath;
        private String inventoryPath;
        private String printerPath;
        private String configPath;
        private String balancePath;

        public URI getBaseUrl()
        {
            return this.baseUrl;
        }

        public void setBaseUrl(final URI _baseUrl)
        {
            this.baseUrl = _baseUrl;
        }

        public String getProductPath()
        {
            return this.productPath;
        }

        public void setProductPath(final String _productPath)
        {
            this.productPath = _productPath;
        }

        public String getCategoryPath()
        {
            return this.categoryPath;
        }

        public void setCategoryPath(final String _categoryPath)
        {
            this.categoryPath = _categoryPath;
        }

        public String getWorkspacePath()
        {
            return this.workspacePath;
        }

        public void setWorkspacePath(final String _workspacePath)
        {
            this.workspacePath = _workspacePath;
        }

        public String getPosPath()
        {
            return this.posPath;
        }

        public void setPosPath(final String _posPath)
        {
            this.posPath = _posPath;
        }

        public String getUserPath()
        {
            return this.userPath;
        }

        public void setUserPath(final String _userPath)
        {
            this.userPath = _userPath;
        }

        public String getReceiptPath()
        {
            return this.receiptPath;
        }

        public void setReceiptPath(final String _receiptPath)
        {
            this.receiptPath = _receiptPath;
        }

        public String getCheckoutPath()
        {
            return this.checkoutPath;
        }

        public void setCheckoutPath(final String _checkoutPath)
        {
            this.checkoutPath = _checkoutPath;
        }

        public String getSequencePath()
        {
            return this.sequencePath;
        }

        public void setSequencePath(final String _sequencePath)
        {
            this.sequencePath = _sequencePath;
        }

        public String getContactPath()
        {
            return this.contactPath;
        }

        public void setContactPath(final String _contactPath)
        {
            this.contactPath = _contactPath;
        }

        public String getInvoicePath()
        {
            return this.invoicePath;
        }

        public void setInvoicePath(final String _invoicePath)
        {
            this.invoicePath = _invoicePath;
        }

        public String getTicketPath()
        {
            return this.ticketPath;
        }

        public void setTicketPath(final String _ticketPath)
        {
            this.ticketPath = _ticketPath;
        }

        public String getWarehousePath()
        {
            return this.warehousePath;
        }

        public void setWarehousePath(final String _warehousePath)
        {
            this.warehousePath = _warehousePath;
        }

        public String getInventoryPath()
        {
            return this.inventoryPath;
        }

        public void setInventoryPath(final String _inventoryPath)
        {
            this.inventoryPath = _inventoryPath;
        }

        public String getPrinterPath()
        {
            return this.printerPath;
        }

        public void setPrinterPath(final String _printerPath)
        {
            this.printerPath = _printerPath;
        }

        public String getConfigPath()
        {
            return this.configPath;
        }

        public void setConfigPath(final String _configPath)
        {
            this.configPath = _configPath;
        }

        public String getBalancePath()
        {
            return this.balancePath;
        }

        public void setBalancePath(final String _balancePath)
        {
            this.balancePath = _balancePath;
        }

        public String getBackendPath()
        {
            return this.backendPath;
        }

        public void setBackendPath(final String _backendPath)
        {
            this.backendPath = _backendPath;
        }
    }

    public static class SSO
    {

        private String url;
        private String clientId;
        private String clientSecret;
        private String username;
        private String password;

        public String getUrl()
        {
            return this.url;
        }

        public void setUrl(final String _url)
        {
            this.url = _url;
        }

        public String getClientId()
        {
            return this.clientId;
        }

        public void setClientId(final String _clientId)
        {
            this.clientId = _clientId;
        }

        public String getClientSecret()
        {
            return this.clientSecret;
        }

        public void setClientSecret(final String _clientSecret)
        {
            this.clientSecret = _clientSecret;
        }

        public String getUsername()
        {
            return this.username;
        }

        public void setUsername(final String _username)
        {
            this.username = _username;
        }

        public String getPassword()
        {
            return this.password;
        }

        public void setPassword(final String _password)
        {
            this.password = _password;
        }

    }
}
