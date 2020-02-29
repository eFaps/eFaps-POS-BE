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
package org.efaps.pos.client;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.efaps.pos.ConfigProperties;
import org.efaps.pos.context.Context;
import org.efaps.pos.dto.BalanceDto;
import org.efaps.pos.dto.CategoryDto;
import org.efaps.pos.dto.ContactDto;
import org.efaps.pos.dto.InventoryEntryDto;
import org.efaps.pos.dto.InvoiceDto;
import org.efaps.pos.dto.OrderDto;
import org.efaps.pos.dto.PosDto;
import org.efaps.pos.dto.PrinterDto;
import org.efaps.pos.dto.ProductDto;
import org.efaps.pos.dto.ReceiptDto;
import org.efaps.pos.dto.SequenceDto;
import org.efaps.pos.dto.TicketDto;
import org.efaps.pos.dto.UserDto;
import org.efaps.pos.dto.WarehouseDto;
import org.efaps.pos.dto.WorkspaceDto;
import org.efaps.pos.entity.Identifier;
import org.efaps.pos.sso.SSOClient;
import org.efaps.pos.util.IdentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.RequestEntity.HeadersBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class EFapsClient
{

    private static final Logger LOG = LoggerFactory.getLogger(EFapsClient.class);
    private final MongoTemplate mongoTemplate;
    private final ConfigProperties config;
    private final RestTemplate restTemplate;
    private final SSOClient ssoClient;

    @Autowired
    public EFapsClient(final MongoTemplate _mongoTemplate,
                       final ConfigProperties _config,
                       final RestTemplateBuilder _restTemplateBuilder,
                       final SSOClient _ssoClient)
    {
        mongoTemplate = _mongoTemplate;
        config = _config;
        restTemplate = _restTemplateBuilder.build();
        ssoClient = _ssoClient;
    }

    public Map<String, String> getProperties() {
        Map<String, String> ret = new HashMap<>();
        try {
            final RequestEntity<?> requestEntity = get(config.getEFaps().getConfigPath());
            final ResponseEntity<Map<String, String>> response = restTemplate.exchange(requestEntity,
                            new ParameterizedTypeReference<Map<String, String>>()
                            {
                            });
            ret = response.getBody();
        } catch (final RestClientException | IdentException e) {
            LOG.error("Catched error during retrieval of properties", e);
        }
        return ret;
    }

    public List<ProductDto> getProducts()
    {
        List<ProductDto> ret = new ArrayList<>();
        try {
            final RequestEntity<?> requestEntity = get(config.getEFaps().getProductPath());
            final ResponseEntity<List<ProductDto>> response = restTemplate.exchange(requestEntity,
                            new ParameterizedTypeReference<List<ProductDto>>()
                            {
                            });
            ret = response.getBody();
        } catch (final RestClientException | IdentException e) {
            LOG.error("Catched error during retrieval of products", e);
        }
        return ret;
    }

    public List<CategoryDto> getCategories()
    {
        List<CategoryDto> ret = new ArrayList<>();
        try {
            final RequestEntity<?> requestEntity = get(config.getEFaps().getCategoryPath());
            final ResponseEntity<List<CategoryDto>> response = restTemplate.exchange(requestEntity,
                            new ParameterizedTypeReference<List<CategoryDto>>()
                            {
                            });
            ret = response.getBody();
        } catch (final RestClientException | IdentException e) {
            LOG.error("Catched error during retrieval of categories", e);
        }
        return ret;
    }

    public List<WorkspaceDto> getWorkspaces()
    {
        List<WorkspaceDto> ret = new ArrayList<>();
        try {
            final RequestEntity<?> requestEntity = get(config.getEFaps().getWorkspacePath());
            final ResponseEntity<List<WorkspaceDto>> response = restTemplate.exchange(requestEntity,
                            new ParameterizedTypeReference<List<WorkspaceDto>>()
                            {
                            });
            ret = response.getBody();
        } catch (final RestClientException | IdentException e) {
            LOG.error("Catched error during retrieval of workspaces", e);
        }
        return ret;
    }

    public List<WarehouseDto> getWarehouses()
    {
        List<WarehouseDto> ret = new ArrayList<>();
        try {
            final RequestEntity<?> requestEntity = get(config.getEFaps().getWarehousePath());
            final ResponseEntity<List<WarehouseDto>> response = restTemplate.exchange(requestEntity,
                            new ParameterizedTypeReference<List<WarehouseDto>>()
                            {
                            });
            ret = response.getBody();
        } catch (final RestClientException | IdentException e) {
            LOG.error("Catched error during retrieval of workspaces", e);
        }
        return ret;
    }

    public List<PrinterDto> getPrinters()
    {
        List<PrinterDto> ret = new ArrayList<>();
        try {
            final RequestEntity<?> requestEntity = get(config.getEFaps().getPrinterPath());
            final ResponseEntity<List<PrinterDto>> response = restTemplate.exchange(requestEntity,
                            new ParameterizedTypeReference<List<PrinterDto>>()
                            {
                            });
            ret = response.getBody();
        } catch (final RestClientException | IdentException e) {
            LOG.error("Catched error during retrieval of workspaces", e);
        }
        return ret;
    }

    public List<InventoryEntryDto> getInventory()
    {
        List<InventoryEntryDto> ret = new ArrayList<>();
        try {
            final RequestEntity<?> requestEntity = get(config.getEFaps().getInventoryPath());
            final ResponseEntity<List<InventoryEntryDto>> response = restTemplate.exchange(requestEntity,
                            new ParameterizedTypeReference<List<InventoryEntryDto>>()
                            {
                            });
            ret = response.getBody();
        } catch (final RestClientException | IdentException e) {
            LOG.error("Catched error during retrieval of workspaces", e);
        }
        return ret;
    }

    public List<PosDto> getPOSs()
    {
        List<PosDto> ret = new ArrayList<>();
        try {
            final RequestEntity<?> requestEntity = get(config.getEFaps().getPosPath());
            final ResponseEntity<List<PosDto>> response = restTemplate.exchange(requestEntity,
                            new ParameterizedTypeReference<List<PosDto>>()
                            {
                            });
            ret = response.getBody();
        } catch (final RestClientException | IdentException e) {
            LOG.error("Catched error during retrieval of poss", e);
        }
        return ret;
    }

    public List<UserDto> getUsers()
    {
        List<UserDto> ret = new ArrayList<>();
        try {
            final RequestEntity<?> requestEntity = get(config.getEFaps().getUserPath());
            final ResponseEntity<List<UserDto>> response = restTemplate.exchange(requestEntity,
                            new ParameterizedTypeReference<List<UserDto>>()
                            {
                            });
            ret = response.getBody();
        } catch (final RestClientException | IdentException e) {
            LOG.error("Catched error during retrieval of users", e);
        }
        return ret;
    }

    public List<SequenceDto> getSequences()
    {
        List<SequenceDto> ret = new ArrayList<>();
        try {
            final RequestEntity<?> requestEntity = get(config.getEFaps().getSequencePath());
            final ResponseEntity<List<SequenceDto>> response = restTemplate.exchange(requestEntity,
                            new ParameterizedTypeReference<List<SequenceDto>>()
                            {
                            });
            ret = response.getBody();
        } catch (final RestClientException | IdentException e) {
            LOG.error("Catched error during retrieval of users", e);
        }
        return ret;
    }

    public List<ContactDto> getContacts()
    {
        List<ContactDto> ret = new ArrayList<>();
        try {
            final RequestEntity<?> requestEntity = get(config.getEFaps().getContactPath());
            final ResponseEntity<List<ContactDto>> response = restTemplate.exchange(requestEntity,
                            new ParameterizedTypeReference<List<ContactDto>>()
                            {
                            });
            ret = response.getBody();
        } catch (final RestClientException | IdentException e) {
            LOG.error("Catched error during retrieval of users", e);
        }
        return ret;
    }

    public BalanceDto postBalance(final BalanceDto _balance)
    {
        BalanceDto ret = _balance;
        try {
            final RequestEntity<BalanceDto> requestEntity = post(config.getEFaps().getBalancePath(), _balance);
            final ResponseEntity<BalanceDto> response = restTemplate.exchange(requestEntity, BalanceDto.class);
            ret = response.getBody();
        } catch (final RestClientException | IdentException e) {
            LOG.error("Catched error during post for Receipts", e);
        }
        return ret;
    }

    public boolean putBalance(final BalanceDto _balance)
    {
        boolean ret = false;
        try {
            final RequestEntity<BalanceDto> requestEntity = put(config.getEFaps().getBalancePath()
                            + "/" + _balance.getOid(), _balance);
            final ResponseEntity<Void> response = restTemplate.exchange(requestEntity, Void.class);
            ret = response.getStatusCode().is2xxSuccessful();
        } catch (final RestClientException | IdentException e) {
            LOG.error("Catched error during post for Balance update", e);
        }
        return ret;
    }

    public ReceiptDto postReceipt(final ReceiptDto _receipt)
    {
        ReceiptDto ret = _receipt;
        try {
            final RequestEntity<ReceiptDto> requestEntity = post(config.getEFaps().getReceiptPath(), _receipt);
            final ResponseEntity<ReceiptDto> response = restTemplate.exchange(requestEntity, ReceiptDto.class);
            ret = response.getBody();
        } catch (final RestClientException | IdentException e) {
            LOG.error("Catched error during post for Receipts", e);
        }
        return ret;
    }

    public InvoiceDto postInvoice(final InvoiceDto _invoice)
    {
        InvoiceDto ret = _invoice;
        try {
            final RequestEntity<InvoiceDto> requestEntity = post(config.getEFaps().getInvoicePath(), _invoice);
            final ResponseEntity<InvoiceDto> response = restTemplate.exchange(requestEntity, InvoiceDto.class);
            ret = response.getBody();
        } catch (final RestClientException | IdentException e) {
            LOG.error("Catched error during post for Invoices", e);
        }
        return ret;
    }

    public TicketDto postTicket(final TicketDto _ticket)
    {
        TicketDto ret = _ticket;
        try {
            final RequestEntity<TicketDto> requestEntity = post(config.getEFaps().getTicketPath(), _ticket);
            final ResponseEntity<TicketDto> response = restTemplate.exchange(requestEntity, TicketDto.class);
            ret = response.getBody();
        } catch (final RestClientException | IdentException e) {
            LOG.error("Catched error during post for Tickets", e);
        }
        return ret;
    }

    public OrderDto postOrder(final OrderDto _order)
    {
        OrderDto ret = _order;
        try {
            final RequestEntity<OrderDto> requestEntity = post(config.getEFaps().getOrderPath(), _order);
            final ResponseEntity<OrderDto> response = restTemplate.exchange(requestEntity, OrderDto.class);
            ret = response.getBody();
        } catch (final RestClientException | IdentException e) {
            LOG.error("Catched error during post for Orders", e);
        }
        return ret;
    }

    public ContactDto postContact(final ContactDto _contact)
    {
        ContactDto ret = _contact;
        try {
            final RequestEntity<ContactDto> requestEntity = post(config.getEFaps().getContactPath(), _contact);
            final ResponseEntity<ContactDto> response = restTemplate.exchange(requestEntity, ContactDto.class);
            ret = response.getBody();
        } catch (final RestClientException | IdentException e) {
            LOG.error("Catched error during post for Invoices", e);
        }
        return ret;
    }

    public Checkout checkout(final String _oid)
    {
        Checkout ret = null;
        try {
            final URI uri = UriComponentsBuilder.fromUri(config.getEFaps().getBaseUrl()).path(config
                            .getEFaps().getCheckoutPath()).queryParam("oid", _oid).build().toUri();
            final RequestEntity<?> requestEntity = get(uri);
            final ResponseEntity<byte[]> response = restTemplate.exchange(requestEntity, byte[].class);
            if (response.getBody() != null) {
                ret = new Checkout();
                ret.setContent(response.getBody());
                ret.setContentType(response.getHeaders().getContentType());
                ret.setFilename(response.getHeaders().getContentDisposition().getFilename());
            }
        } catch (final RestClientException e) {
            LOG.error("Catched error during checkout for oid: '{}'", _oid);
        }
        return ret;
    }

    private <T extends HeadersBuilder<T>> HeadersBuilder<T> addHeader(final HeadersBuilder<T> _builder) {
      HeadersBuilder<T> ret = _builder.header("Authorization", getAuth());
      if (Context.get().getCompany() != null) {
        ret = ret.header("X-CONTEXT-COMPANY", Context.get().getCompany().getKey());
      }
      return ret;
    }

    private String getAuth()
    {
        String auth = "";
        if (config.getSso() != null && StringUtils.isNotEmpty(config.getSso().getUrl())) {
            auth = "Bearer " + ssoClient.getToken();
        } else if (config.getAuth() != null) {
            auth = "Basic " + Base64.getEncoder().encodeToString((config.getAuth().getUser() + ":" + config
                            .getAuth().getPassword()).getBytes(StandardCharsets.UTF_8));
        }
        return auth;
    }

    private UriComponents getUriComponent(final String _path)
        throws IdentException
    {
        final String ident = getIdentifier();
        if (ident == null) {
            throw new IdentException();
        }
        final Map<String, String> map = new HashMap<>();
        map.put("identifier", ident);
        return UriComponentsBuilder.fromUri(config.getEFaps().getBaseUrl()).path(_path)
                        .buildAndExpand(map);
    }

    public RequestEntity<?> get(final URI _uri)
    {
      return addHeader(RequestEntity.get(_uri)).build();
    }

    public <T> RequestEntity<T> post(final URI _uri, final T _body)
    {
      return addHeader(RequestEntity.post(_uri))
                      .accept(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN)
                      .body(_body);
    }

    private RequestEntity<?> get(final String _path)
        throws IdentException
    {
        return get(getUriComponent(_path).toUri());
    }

    private <T> RequestEntity<T> post(final String _path, final T _body)
        throws IdentException
    {
        return addHeader(RequestEntity.post(getUriComponent(_path).toUri()))
                        .accept(MediaType.APPLICATION_JSON).body(_body);
    }

    private <T> RequestEntity<T> put(final String _path, final T _body)
        throws IdentException
    {
        return addHeader(RequestEntity.put(getUriComponent(_path).toUri()))
            .accept(MediaType.APPLICATION_JSON).body(_body);
    }

    private String getIdentifier() {
        String ret = null;
        final Identifier identifier = mongoTemplate.findById(Identifier.KEY, Identifier.class);
        if (identifier == null) {
            try {
                final UriComponents uriComp = UriComponentsBuilder.fromUri(config.getEFaps().getBaseUrl())
                                    .path(config.getEFaps().getBackendPath())
                                    .path("/identifier")
                                    .build();
                final RequestEntity<?> requestEntity = get(uriComp.toUri());
                final ResponseEntity<String> response = restTemplate.exchange(requestEntity,
                                new ParameterizedTypeReference<String>()
                                {
                                });
                final String ident = response.getBody();
                if (!ident.contains("deactivated")) {
                    ret = ident;
                    final Identifier newIdentifier = new Identifier()
                                        .setId(Identifier.KEY)
                                        .setCreated(LocalDateTime.now())
                                        .setIdentifier(ident);
                    mongoTemplate.save(newIdentifier);
                }
            } catch (final RestClientException e) {
                LOG.error("Catched error during retrieval of identifier", e);
            }
        } else {
            ret = identifier.getIdentifier();
        }
        return ret;
    }
}
