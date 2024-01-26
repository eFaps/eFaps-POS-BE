/*
 * Copyright 2003 - 2021 The eFaps Team
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
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.efaps.pos.ConfigProperties;
import org.efaps.pos.ConfigProperties.EFaps;
import org.efaps.pos.dto.BalanceDto;
import org.efaps.pos.dto.CategoryDto;
import org.efaps.pos.dto.ContactDto;
import org.efaps.pos.dto.CreditNoteDto;
import org.efaps.pos.dto.EmployeeDto;
import org.efaps.pos.dto.ExchangeRateDto;
import org.efaps.pos.dto.InventoryEntryDto;
import org.efaps.pos.dto.InvoiceDto;
import org.efaps.pos.dto.LogEntryDto;
import org.efaps.pos.dto.OrderDto;
import org.efaps.pos.dto.PosDto;
import org.efaps.pos.dto.PrinterDto;
import org.efaps.pos.dto.ProductDto;
import org.efaps.pos.dto.ReceiptDto;
import org.efaps.pos.dto.ReportToBaseDto;
import org.efaps.pos.dto.SequenceDto;
import org.efaps.pos.dto.StocktakingDto;
import org.efaps.pos.dto.TicketDto;
import org.efaps.pos.dto.UserDto;
import org.efaps.pos.dto.WarehouseDto;
import org.efaps.pos.dto.WorkspaceDto;
import org.efaps.pos.entity.Identifier;
import org.efaps.pos.sso.SSOClient;
import org.efaps.pos.util.IdentException;
import org.efaps.promotionengine.promotion.Promotion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class EFapsClient
    extends AbstractRestClient
{

    private static final Logger LOG = LoggerFactory.getLogger(EFapsClient.class);
    private final MongoTemplate mongoTemplate;

    @Autowired
    public EFapsClient(final RestTemplateBuilder _restTemplateBuilder,
                       final MongoTemplate _mongoTemplate,
                       final ConfigProperties _config,
                       final SSOClient _ssoClient)
    {
        super(_restTemplateBuilder, _config, _ssoClient);
        mongoTemplate = _mongoTemplate;
    }

    public Map<String, String> getProperties()
    {
        Map<String, String> ret = new HashMap<>();
        try {
            final RequestEntity<?> requestEntity = get(getEFapsConfig().getConfigPath());
            final ResponseEntity<Map<String, String>> response = getRestTemplate().exchange(requestEntity,
                            new ParameterizedTypeReference<Map<String, String>>()
                            {
                            });
            ret = response.getBody();
        } catch (final RestClientException | IdentException e) {
            LOG.error("Catched error during retrieval of properties", e);
        }
        return ret;
    }

    public List<ProductDto> getProducts(int limit,
                                        int offset,
                                        OffsetDateTime after)
    {
        List<ProductDto> ret = new ArrayList<>();
        try {
            final var params = new LinkedMultiValueMap<String, String>();
            params.put("limit", Collections.singletonList(String.valueOf(limit)));
            params.put("offset", Collections.singletonList(String.valueOf(offset)));
            if (after != null) {
                params.put("after", Collections.singletonList(after.toString()));
            }
            final RequestEntity<?> requestEntity = get(getEFapsConfig().getProductPath(), params);
            final ResponseEntity<List<ProductDto>> response = getRestTemplate().exchange(requestEntity,
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
            final RequestEntity<?> requestEntity = get(getEFapsConfig().getCategoryPath());
            final ResponseEntity<List<CategoryDto>> response = getRestTemplate().exchange(requestEntity,
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
            final RequestEntity<?> requestEntity = get(getEFapsConfig().getWorkspacePath());
            final ResponseEntity<List<WorkspaceDto>> response = getRestTemplate().exchange(requestEntity,
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
            final RequestEntity<?> requestEntity = get(getEFapsConfig().getWarehousePath());
            final ResponseEntity<List<WarehouseDto>> response = getRestTemplate().exchange(requestEntity,
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
            final RequestEntity<?> requestEntity = get(getEFapsConfig().getPrinterPath());
            final ResponseEntity<List<PrinterDto>> response = getRestTemplate().exchange(requestEntity,
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
            final RequestEntity<?> requestEntity = get(getEFapsConfig().getInventoryPath());
            final ResponseEntity<List<InventoryEntryDto>> response = getRestTemplate().exchange(requestEntity,
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
            final RequestEntity<?> requestEntity = get(getEFapsConfig().getPosPath());
            final ResponseEntity<List<PosDto>> response = getRestTemplate().exchange(requestEntity,
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
            final RequestEntity<?> requestEntity = get(getEFapsConfig().getUserPath());
            final ResponseEntity<List<UserDto>> response = getRestTemplate().exchange(requestEntity,
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
            final RequestEntity<?> requestEntity = get(getEFapsConfig().getSequencePath());
            final ResponseEntity<List<SequenceDto>> response = getRestTemplate().exchange(requestEntity,
                            new ParameterizedTypeReference<List<SequenceDto>>()
                            {
                            });
            ret = response.getBody();
        } catch (final RestClientException | IdentException e) {
            LOG.error("Catched error during retrieval of users", e);
        }
        return ret;
    }

    public List<ContactDto> getContacts(int limit,
                                        int offset,
                                        OffsetDateTime after)
    {
        List<ContactDto> ret = new ArrayList<>();
        try {
            final var params = new LinkedMultiValueMap<String, String>();
            params.put("limit", Collections.singletonList(String.valueOf(limit)));
            params.put("offset", Collections.singletonList(String.valueOf(offset)));
            if (after != null) {
                params.put("after", Collections.singletonList(after.toString()));
            }
            final RequestEntity<?> requestEntity = get(getEFapsConfig().getContactPath(), params);
            final ResponseEntity<List<ContactDto>> response = getRestTemplate().exchange(requestEntity,
                            new ParameterizedTypeReference<List<ContactDto>>()
                            {
                            });
            ret = response.getBody();
        } catch (final RestClientException | IdentException e) {
            LOG.error("Catched error during retrieval of users", e);
        }
        return ret;
    }

    public ContactDto getContact(String oid)
    {
        ContactDto ret = null;
        try {
            final var uriVariables = new HashMap<String, String>();
            uriVariables.put("oid", oid);
            final RequestEntity<?> requestEntity = get(
                            getUriComponent(getEFapsConfig().getContactPath() + "/{oid}", uriVariables, null).toUri());
            final ResponseEntity<ContactDto> response = getRestTemplate().exchange(requestEntity,
                            new ParameterizedTypeReference<ContactDto>()
                            {
                            });
            ret = response.getBody();
        } catch (final RestClientException | IdentException e) {
            LOG.error("Catched error during retrieval of users", e);
        }
        return ret;
    }

    public List<EmployeeDto> getEmployees()
    {
        List<EmployeeDto> ret = new ArrayList<>();
        try {
            final RequestEntity<?> requestEntity = get(getEFapsConfig().getEmployeePath());
            final ResponseEntity<List<EmployeeDto>> response = getRestTemplate().exchange(requestEntity,
                            new ParameterizedTypeReference<List<EmployeeDto>>()
                            {
                            });
            ret = response.getBody();
        } catch (final RestClientException | IdentException e) {
            LOG.error("Catched error during retrieval of users", e);
        }
        return ret;
    }

    public List<ExchangeRateDto> getExchangeRates()
    {
        List<ExchangeRateDto> ret = null;
        try {
            final var requestEntity = get(getEFapsConfig().getExchangeRatePath());

            final var response = getRestTemplate().exchange(requestEntity,
                            new ParameterizedTypeReference<List<ExchangeRateDto>>()
                            {
                            });
            ret = response.getBody();
        } catch (final IdentException e) {
            LOG.error("Catched error during get for ExchangeRateDto", e);
        }
        return ret;
    }

    public List<Promotion> getPromotions()
    {
        List<Promotion> ret = null;
        try {
            final var requestEntity = get(getEFapsConfig().getPromotionPath());

            final var response = getRestTemplate().exchange(requestEntity,
                            new ParameterizedTypeReference<List<Promotion>>()
                            {
                            });
            ret = response.getBody();
        } catch (final IdentException e) {
            LOG.error("Catched error during get for ExchangeRateDto", e);
        }
        return ret;
    }

    public BalanceDto postBalance(final BalanceDto _balance)
    {
        BalanceDto ret = _balance;
        try {
            final RequestEntity<BalanceDto> requestEntity = post(getEFapsConfig().getBalancePath(), _balance);
            final ResponseEntity<BalanceDto> response = getRestTemplate().exchange(requestEntity, BalanceDto.class);
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
            final RequestEntity<BalanceDto> requestEntity = put(getEFapsConfig().getBalancePath()
                            + "/" + _balance.getOid(), _balance);
            final ResponseEntity<Void> response = getRestTemplate().exchange(requestEntity, Void.class);
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
            final RequestEntity<ReceiptDto> requestEntity = post(getEFapsConfig().getReceiptPath(), _receipt);
            final ResponseEntity<ReceiptDto> response = getRestTemplate().exchange(requestEntity, ReceiptDto.class);
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
            final RequestEntity<InvoiceDto> requestEntity = post(getEFapsConfig().getInvoicePath(), _invoice);
            final ResponseEntity<InvoiceDto> response = getRestTemplate().exchange(requestEntity, InvoiceDto.class);
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
            final RequestEntity<TicketDto> requestEntity = post(getEFapsConfig().getTicketPath(), _ticket);
            final ResponseEntity<TicketDto> response = getRestTemplate().exchange(requestEntity, TicketDto.class);
            ret = response.getBody();
        } catch (final RestClientException | IdentException e) {
            LOG.error("Catched error during post for Tickets", e);
        }
        return ret;
    }

    public CreditNoteDto postCreditNote(final CreditNoteDto creditNote)
    {
        CreditNoteDto ret = creditNote;
        try {
            final RequestEntity<CreditNoteDto> requestEntity = post(getEFapsConfig().getCreditnotePath(), creditNote);
            final ResponseEntity<CreditNoteDto> response = getRestTemplate().exchange(requestEntity,
                            CreditNoteDto.class);
            ret = response.getBody();
        } catch (final RestClientException | IdentException e) {
            LOG.error("Catched error during post for CreditNotes", e);
        }
        return ret;
    }

    public OrderDto postOrder(final OrderDto _order)
    {
        OrderDto ret = _order;
        try {
            final RequestEntity<OrderDto> requestEntity = post(getEFapsConfig().getOrderPath(), _order);
            final ResponseEntity<OrderDto> response = getRestTemplate().exchange(requestEntity, OrderDto.class);
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
            final RequestEntity<ContactDto> requestEntity = post(getEFapsConfig().getContactPath(), _contact);
            final ResponseEntity<ContactDto> response = getRestTemplate().exchange(requestEntity, ContactDto.class);
            ret = response.getBody();
        } catch (final RestClientException | IdentException e) {
            LOG.error("Catched error during post for Contact", e);
        }
        return ret;
    }

    public ContactDto putContact(final ContactDto contact)
    {
        ContactDto ret = contact;
        try {
            final var uriVariables = new HashMap<String, String>();
            uriVariables.put("oid", contact.getOid());
            final RequestEntity<ContactDto> requestEntity = put(
                            getUriComponent(getEFapsConfig().getContactPath() + "/{oid}", uriVariables, null).toUri(),
                            contact);
            final ResponseEntity<ContactDto> response = getRestTemplate().exchange(requestEntity, ContactDto.class);
            ret = response.getBody();
        } catch (final RestClientException | IdentException e) {
            LOG.error("Catched error during put for Contact", e);
        }
        return ret;
    }

    public String postStocktaking(final StocktakingDto stocktaking)
    {
        String ret = null;
        try {
            final RequestEntity<StocktakingDto> requestEntity = post(getEFapsConfig().getStocktakingPath(),
                            stocktaking);
            final ResponseEntity<String> response = getRestTemplate().exchange(requestEntity,
                            String.class);
            ret = response.getBody();
        } catch (final RestClientException | IdentException e) {
            LOG.error("Catched error during post for Stocktaking", e);
        }
        return ret;
    }

    public String postLogEntry(final LogEntryDto dto)
    {
        String ret = null;
        try {
            final RequestEntity<LogEntryDto> requestEntity = post(getEFapsConfig().getLogEntryPath(), dto);
            final ResponseEntity<String> response = getRestTemplate().exchange(requestEntity,
                            String.class);
            ret = response.getBody();
        } catch (final RestClientException | IdentException e) {
            LOG.error("Catched error during post for LogEntry", e);
        }
        return ret;
    }

    public void postReportToBase(final ReportToBaseDto dto)
    {
        try {
            final RequestEntity<ReportToBaseDto> requestEntity = post(getEFapsConfig().getReportToBasePath(), dto);
            getRestTemplate().exchange(requestEntity, Void.class);
        } catch (final RestClientException | IdentException e) {
            LOG.error("Catched error during post for ReportToBase", e);
        }
    }

    public Checkout checkout(final String _oid)
    {
        Checkout ret = null;
        try {
            final URI uri = UriComponentsBuilder.fromUri(getEFapsConfig().getBaseUrl())
                            .path(getEFapsConfig().getCheckoutPath())
                            .queryParam("oid", _oid).build().toUri();
            final RequestEntity<?> requestEntity = get(uri);
            final ResponseEntity<byte[]> response = getRestTemplate().exchange(requestEntity, byte[].class);
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

    private UriComponents getUriComponent(final String _path)
        throws IdentException
    {
        return getUriComponent(_path, null);
    }

    private UriComponents getUriComponent(final String _path,
                                          MultiValueMap<String, String> params)
        throws IdentException
    {

        return getUriComponent(_path, new HashMap<>(), params);
    }

    private UriComponents getUriComponent(final String _path,
                                          Map<String, String> uriVariables,
                                          MultiValueMap<String, String> params)
        throws IdentException
    {
        final String ident = getIdentifier();
        if (ident == null) {
            throw new IdentException();
        }
        uriVariables.put("identifier", ident);
        return UriComponentsBuilder.fromUri(getEFapsConfig().getBaseUrl())
                        .path(_path)
                        .queryParams(params)
                        .buildAndExpand(uriVariables);
    }

    public RequestEntity<?> get(final URI _uri)
    {
        return addHeader(RequestEntity.get(_uri)).build();
    }

    public RequestEntity<?> get(final String _path)
        throws IdentException
    {
        return get(getUriComponent(_path).toUri());
    }

    public RequestEntity<?> get(final String _path,
                                MultiValueMap<String, String> params)
        throws IdentException
    {
        return get(getUriComponent(_path, params).toUri());
    }

    public <T> RequestEntity<T> post(final String _path,
                                     final T _body)
        throws IdentException
    {
        return post(getUriComponent(_path).toUri(), _body);
    }

    public <T> RequestEntity<T> post(final URI _uri,
                                     final T _body)
        throws IdentException
    {
        return addHeader(RequestEntity.post(_uri))
                        .accept(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN).body(_body);
    }

    public <T> RequestEntity<T> put(final String _path,
                                    final T _body)
        throws IdentException
    {
        return put(getUriComponent(_path).toUri(), _body);
    }

    public <T> RequestEntity<T> put(final URI _uri,
                                    final T _body)
        throws IdentException
    {
        return addHeader(RequestEntity.put(_uri))
                        .accept(MediaType.APPLICATION_JSON).body(_body);
    }

    public String getIdentifier()
    {
        String ret = null;
        final Identifier identifier = mongoTemplate.findById(Identifier.KEY, Identifier.class);
        if (identifier == null) {
            try {
                final UriComponents uriComp = UriComponentsBuilder.fromUri(getEFapsConfig().getBaseUrl())
                                .path(getEFapsConfig().getBackendPath())
                                .path("/identifier")
                                .build();
                final RequestEntity<?> requestEntity = get(uriComp.toUri());
                final ResponseEntity<String> response = getRestTemplate().exchange(requestEntity,
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

    protected EFaps getEFapsConfig()
    {
        return getConfig().getEFaps();
    }
}
