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

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties
public class ConfigProperties
{

    private static final Logger LOG = LoggerFactory.getLogger(ConfigProperties.class);

    private final BEInst beInst = new BEInst();

    private final SSO sso = new SSO();

    private final EFaps efaps = new EFaps();

    private final BasicAuth auth = new BasicAuth();

    private List<Company> companies = new ArrayList<>();

    private final StaticWeb staticWeb = new StaticWeb();

    private final Enquiry enquiry = new Enquiry();

    private final List<Extension> extensions = new ArrayList<>();

    private final List<LogToken> logTokens = new ArrayList<>();

    private final Proxy proxy = new Proxy();

    public BEInst getBeInst()
    {
        return beInst;
    }

    public Proxy getProxy()
    {
        return proxy;
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

    public StaticWeb getStaticWeb()
    {
        return staticWeb;
    }

    public Enquiry getEnquiry()
    {
        return enquiry;
    }

    public List<Extension> getExtensions()
    {
        return extensions;
    }

    public List<LogToken> getLogTokens()
    {
        return logTokens;
    }

    public static class BEInst
    {

        private String version;

        private String name;

        private String mongoClientURI;

        private boolean syncOnStartup;

        private int maxSearchResult;

        private boolean skipProductsMissingPrice = false;

        private String timeZone;

        private Order order = new Order();

        private FileConfig fileConfig = new FileConfig();

        public void setOrderFormat(String orderFormat)
        {
            LOG.error("Property 'beInst.orderFormat' was moved to 'beInst.order.numberFormat'");
            throw new RuntimeException();
        }

        public FileConfig getFileConfig()
        {
            return fileConfig;
        }

        public void setFileConfig(FileConfig fileConfig)
        {
            this.fileConfig = fileConfig;
        }

        public Order getOrder()
        {
            return order;
        }

        public void setOrder(Order order)
        {
            this.order = order;
        }

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

        public String getTimeZone()
        {
            return timeZone;
        }

        public void setTimeZone(final String timeZone)
        {
            this.timeZone = timeZone;
        }

        public String getMongoClientURI()
        {
            return mongoClientURI;
        }

        public void setMongoClientURI(final String mongoClientURI)
        {
            this.mongoClientURI = mongoClientURI;
        }

        public boolean isSyncOnStartup()
        {
            return syncOnStartup;
        }

        public void setSyncOnStartup(final boolean syncOnStartup)
        {
            this.syncOnStartup = syncOnStartup;
        }

        public int getMaxSearchResult()
        {
            return maxSearchResult;
        }

        public void setMaxSearchResult(final int maxResult)
        {
            maxSearchResult = maxResult;
        }

        public boolean isSkipProductsMissingPrice()
        {
            return skipProductsMissingPrice;
        }

        public void setSkipProductsMissingPrice(boolean skipProductsMissingPrice)
        {
            this.skipProductsMissingPrice = skipProductsMissingPrice;
        }

    }

    public static class Order
    {

        private String numberFormat;
        private boolean skipCalcOnCreate = false;
        private boolean allowSetUnitPrice = false;

        public String getNumberFormat()
        {
            return numberFormat;
        }

        public void setNumberFormat(String numberFormat)
        {
            this.numberFormat = numberFormat;
        }

        public boolean isSkipCalcOnCreate()
        {
            return skipCalcOnCreate;
        }

        public void setSkipCalcOnCreate(boolean skipCalcOnCreate)
        {
            this.skipCalcOnCreate = skipCalcOnCreate;
        }

        public boolean isAllowSetUnitPrice()
        {
            return allowSetUnitPrice;
        }

        public void setAllowSetUnitPrice(boolean allowSetUnitPrice)
        {
            this.allowSetUnitPrice = allowSetUnitPrice;
        }
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
        private int productLimit;
        private String categoryPath;
        private String workspacePath;
        private String posPath;
        private String userPath;
        private String orderPath;
        private String receiptPath;
        private String invoicePath;
        private String ticketPath;
        private String creditnotePath;
        private String checkoutPath;
        private String sequencePath;
        private String contactPath;
        private int contactLimit;
        private String warehousePath;
        private String inventoryPath;
        private String printerPath;
        private String configPath;
        private String balancePath;
        private String exchangeRatePath;
        private String employeePath;
        private String stocktakingPath;
        private String logEntryPath;
        private String promotionPath;
        private String promotionInfoPath;
        private String filePath;
        private String updatePath;
        private String storePath;
        private String reportToBasePath;

        public String getFilePath()
        {
            return filePath;
        }

        public void setFilePath(String filePath)
        {
            this.filePath = filePath;
        }

        public String getPromotionInfoPath()
        {
            return promotionInfoPath;
        }

        public void setPromotionInfoPath(String promotionInfoPath)
        {
            this.promotionInfoPath = promotionInfoPath;
        }

        public String getReportToBasePath()
        {
            return reportToBasePath;
        }

        public void setReportToBasePath(String reportToBasePath)
        {
            this.reportToBasePath = reportToBasePath;
        }

        public String getPromotionPath()
        {
            return promotionPath;
        }

        public void setPromotionPath(String promotionPath)
        {
            this.promotionPath = promotionPath;
        }

        public String getLogEntryPath()
        {
            return logEntryPath;
        }

        public void setLogEntryPath(String logEntryPath)
        {
            this.logEntryPath = logEntryPath;
        }

        public String getStocktakingPath()
        {
            return stocktakingPath;
        }

        public void setStocktakingPath(final String stocktakingPath)
        {
            this.stocktakingPath = stocktakingPath;
        }

        public String getEmployeePath()
        {
            return employeePath;
        }

        public void setEmployeePath(final String employeePath)
        {
            this.employeePath = employeePath;
        }

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

        public int getProductLimit()
        {
            return productLimit;
        }

        public void setProductLimit(int productLimit)
        {
            this.productLimit = productLimit;
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

        public int getContactLimit()
        {
            return contactLimit;
        }

        public void setContactLimit(int contactLimit)
        {
            this.contactLimit = contactLimit;
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

        public String getExchangeRatePath()
        {
            return exchangeRatePath;
        }

        public void setExchangeRatePath(final String exchangeRatePath)
        {
            this.exchangeRatePath = exchangeRatePath;
        }

        public String getCreditnotePath()
        {
            return creditnotePath;
        }

        public void setCreditnotePath(final String creditnotePath)
        {
            this.creditnotePath = creditnotePath;
        }

        public String getUpdatePath()
        {
            return updatePath;
        }

        public void setUpdatePath(final String updatePath)
        {
            this.updatePath = updatePath;
        }


        public String getStorePath()
        {
            return storePath;
        }


        public void setStorePath(String storePath)
        {
            this.storePath = storePath;
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

    public static class StaticWeb
    {

        private final List<String> ignore = new ArrayList<>();
        private final Map<String, String> views = new HashMap<>();
        private final Map<String, String> resources = new HashMap<>();

        public Map<String, String> getResources()
        {
            return resources;
        }

        public Map<String, String> getViews()
        {
            return views;
        }

        public List<String> getIgnore()
        {
            return ignore;
        }
    }

    public static class Extension
    {

        private String key;
        private String tag;
        private String url;

        public String getKey()
        {
            return key;
        }

        public void setKey(final String key)
        {
            this.key = key;
        }

        public String getTag()
        {
            return tag;
        }

        public void setTag(final String tag)
        {
            this.tag = tag;
        }

        public String getUrl()
        {
            return url;
        }

        public void setUrl(final String url)
        {
            this.url = url;
        }
    }

    public static class LogToken
    {

        private String ident;
        private String token;

        public String getIdent()
        {
            return ident;
        }

        public void setIdent(final String ident)
        {
            this.ident = ident;
        }

        public String getToken()
        {
            return token;
        }

        public void setToken(final String token)
        {
            this.token = token;
        }
    }

    public static class Proxy
    {

        private String uri;
        private List<String> ignoreHostNames;

        public List<String> getIgnoreHostNames()
        {
            return ignoreHostNames;
        }

        public void setIgnoreHostNames(List<String> ignoreHostNames)
        {
            this.ignoreHostNames = ignoreHostNames;
        }

        public String getUri()
        {
            return uri;
        }

        public void setUri(String uri)
        {
            this.uri = uri;
        }
    }

    public static class Enquiry
    {

        private URI baseUrl;
        private String dniPath;
        private String rucPath;

        public String getRucPath()
        {
            return rucPath;
        }

        public void setRucPath(String rucPath)
        {
            this.rucPath = rucPath;
        }

        public String getDniPath()
        {
            return dniPath;
        }

        public void setDniPath(String dniPath)
        {
            this.dniPath = dniPath;
        }

        public URI getBaseUrl()
        {
            return baseUrl;
        }

        public void setBaseUrl(final URI _baseUrl)
        {
            baseUrl = _baseUrl;
        }
    }

    public static class FileConfig
    {

        private URI locationUri;
        private String path;

        public String getPath()
        {
            return path;
        }

        public void setPath(String path)
        {
            this.path = path;
        }

        public URI getLocationUri()
        {
            return locationUri;
        }

        public void setLocationUri(URI locationUri)
        {
            this.locationUri = locationUri;
        }
    }

}
