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

package org.efaps.pos;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
public class ConfigProperties
{

    private String version;

    private String name;

    private String mongoClientURI;

    private final SSO sso = new SSO();

    private final EFaps efaps = new EFaps();

    private final BasicAuth auth = new BasicAuth();

    private List<Company> companies = new ArrayList<>();

    public String getVersion()
    {
        return version;
    }

    public void setVersion(final String version)
    {
        this.version = version;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String _name)
    {
        name = _name;
    }

    public SSO getSso()
    {
        return sso;
    }

    public EFaps getEFaps()
    {
        return efaps;
    }

    public BasicAuth getAuth()
    {
        return auth;
    }

    public List<Company> getCompanies()
    {
        return companies;
    }

    public void setCompanies(final List<Company> _companies)
    {
        companies = _companies;
    }

    public String getMongoClientURI()
    {
        return mongoClientURI;
    }

    public void setMongoClientURI(final String mongoClientURI)
    {
        this.mongoClientURI = mongoClientURI;
    }

    public static class BasicAuth
    {

        private String user;
        private String password;

        public String getPassword()
        {
            return password;
        }

        public void setPassword(final String _password)
        {
            password = _password;
        }

        public String getUser()
        {
            return user;
        }

        public void setUser(final String _user)
        {
            user = _user;
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
        private String orderPath;
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
            return baseUrl;
        }

        public void setBaseUrl(final URI _baseUrl)
        {
            baseUrl = _baseUrl;
        }

        public String getProductPath()
        {
            return productPath;
        }

        public void setProductPath(final String _productPath)
        {
            productPath = _productPath;
        }

        public String getCategoryPath()
        {
            return categoryPath;
        }

        public void setCategoryPath(final String _categoryPath)
        {
            categoryPath = _categoryPath;
        }

        public String getWorkspacePath()
        {
            return workspacePath;
        }

        public void setWorkspacePath(final String _workspacePath)
        {
            workspacePath = _workspacePath;
        }

        public String getPosPath()
        {
            return posPath;
        }

        public void setPosPath(final String _posPath)
        {
            posPath = _posPath;
        }

        public String getUserPath()
        {
            return userPath;
        }

        public void setUserPath(final String _userPath)
        {
            userPath = _userPath;
        }

        public String getOrderPath()
        {
            return orderPath;
        }

        public void setOrderPath(final String _orderPath)
        {
            orderPath = _orderPath;
        }

        public String getReceiptPath()
        {
            return receiptPath;
        }

        public void setReceiptPath(final String _receiptPath)
        {
            receiptPath = _receiptPath;
        }

        public String getCheckoutPath()
        {
            return checkoutPath;
        }

        public void setCheckoutPath(final String _checkoutPath)
        {
            checkoutPath = _checkoutPath;
        }

        public String getSequencePath()
        {
            return sequencePath;
        }

        public void setSequencePath(final String _sequencePath)
        {
            sequencePath = _sequencePath;
        }

        public String getContactPath()
        {
            return contactPath;
        }

        public void setContactPath(final String _contactPath)
        {
            contactPath = _contactPath;
        }

        public String getInvoicePath()
        {
            return invoicePath;
        }

        public void setInvoicePath(final String _invoicePath)
        {
            invoicePath = _invoicePath;
        }

        public String getTicketPath()
        {
            return ticketPath;
        }

        public void setTicketPath(final String _ticketPath)
        {
            ticketPath = _ticketPath;
        }

        public String getWarehousePath()
        {
            return warehousePath;
        }

        public void setWarehousePath(final String _warehousePath)
        {
            warehousePath = _warehousePath;
        }

        public String getInventoryPath()
        {
            return inventoryPath;
        }

        public void setInventoryPath(final String _inventoryPath)
        {
            inventoryPath = _inventoryPath;
        }

        public String getPrinterPath()
        {
            return printerPath;
        }

        public void setPrinterPath(final String _printerPath)
        {
            printerPath = _printerPath;
        }

        public String getConfigPath()
        {
            return configPath;
        }

        public void setConfigPath(final String _configPath)
        {
            configPath = _configPath;
        }

        public String getBalancePath()
        {
            return balancePath;
        }

        public void setBalancePath(final String _balancePath)
        {
            balancePath = _balancePath;
        }

        public String getBackendPath()
        {
            return backendPath;
        }

        public void setBackendPath(final String _backendPath)
        {
            backendPath = _backendPath;
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
            return url;
        }

        public void setUrl(final String _url)
        {
            url = _url;
        }

        public String getClientId()
        {
            return clientId;
        }

        public void setClientId(final String _clientId)
        {
            clientId = _clientId;
        }

        public String getClientSecret()
        {
            return clientSecret;
        }

        public void setClientSecret(final String _clientSecret)
        {
            clientSecret = _clientSecret;
        }

        public String getUsername()
        {
            return username;
        }

        public void setUsername(final String _username)
        {
            username = _username;
        }

        public String getPassword()
        {
            return password;
        }

        public void setPassword(final String _password)
        {
            password = _password;
        }
    }

    public static class Company
    {

        private String label;
        private String key;
        private String tenant;

        public String getLabel()
        {
            return label;
        }

        public void setLabel(final String _label)
        {
            label = _label;
        }

        public String getKey()
        {
            return key;
        }

        public void setKey(final String _key)
        {
            key = _key;
        }

        public String getTenant()
        {
            return tenant;
        }

        public void setTenant(final String _tenant)
        {
            tenant = _tenant;
        }
    }
}
