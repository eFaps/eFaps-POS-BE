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
package org.efaps.pos.client;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.efaps.pos.config.ConfigProperties;
import org.efaps.pos.config.ConfigProperties.EFaps;
import org.efaps.pos.dto.BalanceDto;
import org.efaps.pos.dto.CategoryDto;
import org.efaps.pos.dto.ContactDto;
import org.efaps.pos.dto.CreditNoteDto;
import org.efaps.pos.dto.DumpDto;
import org.efaps.pos.dto.EmitValidityDto;
import org.efaps.pos.dto.EmitValidityResponseDto;
import org.efaps.pos.dto.EmployeeDto;
import org.efaps.pos.dto.ExchangeRateDto;
import org.efaps.pos.dto.FileDto;
import org.efaps.pos.dto.InventoryEntryDto;
import org.efaps.pos.dto.InvoiceDto;
import org.efaps.pos.dto.LogEntryDto;
import org.efaps.pos.dto.LoyaltyPointsBalanceDto;
import org.efaps.pos.dto.OrderDto;
import org.efaps.pos.dto.PosDto;
import org.efaps.pos.dto.PrinterDto;
import org.efaps.pos.dto.ProductDto;
import org.efaps.pos.dto.PromoInfoSyncDto;
import org.efaps.pos.dto.ReceiptDto;
import org.efaps.pos.dto.RedeemValidityDto;
import org.efaps.pos.dto.ReportStartupDto;
import org.efaps.pos.dto.ReportToBaseDto;
import org.efaps.pos.dto.SequenceDto;
import org.efaps.pos.dto.StocktakingDto;
import org.efaps.pos.dto.StoreStatusRequestDto;
import org.efaps.pos.dto.StoreStatusResponseDto;
import org.efaps.pos.dto.TicketDto;
import org.efaps.pos.dto.UpdateConfirmationDto;
import org.efaps.pos.dto.UpdateDto;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class EFapsClient
    extends AbstractRestClient
{

    private static final Logger LOG = LoggerFactory.getLogger(EFapsClient.class);
    private final MongoTemplate mongoTemplate;

    @Autowired
    public EFapsClient(final RestClient.Builder restClientBuilder,
                       final MongoTemplate _mongoTemplate,
                       final ConfigProperties _config,
                       final SSOClient _ssoClient)
    {
        super(restClientBuilder, _config, _ssoClient);
        mongoTemplate = _mongoTemplate;
    }

    public Map<String, String> getProperties()
    {
        Map<String, String> ret = new HashMap<>();
        try {
            final var responseSpec = getRestClient().get()
                            .uri(uriBuilder(getEFapsConfig().getConfigPath()))
                            .retrieve();
            ret = responseSpec.body(new ParameterizedTypeReference<Map<String, String>>()
            {
            });
        } catch (final RestClientException e) {
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
            final var responseSpec = getRestClient().get()
                            .uri(uriBuilder(getEFapsConfig().getProductPath(), params))
                            .retrieve();
            ret = responseSpec.body(new ParameterizedTypeReference<List<ProductDto>>()
            {
            });
        } catch (final RestClientException e) {
            LOG.error("Catched error during retrieval of products", e);
        }
        return ret;
    }

    public ProductDto getProduct(final String productOid)
    {
        ProductDto ret = null;
        try {
            final var responseSpec = getRestClient().get()
                            .uri(uriBuilder(getEFapsConfig().getProductPath() + "/" + productOid))
                            .retrieve();
            ret = responseSpec.body(ProductDto.class);
        } catch (final RestClientException e) {
            LOG.error("Catched error during retrieval of a product", e);
        }
        return ret;
    }

    public DumpDto getProductDump()
    {
        DumpDto ret = null;
        try {
            ret = getRestClient().get()
                            .uri(uriBuilder(getEFapsConfig().getProductPath() + "/dump"))
                            .retrieve()
                            .onStatus(HttpStatusCode::isError, (request,
                                                                response) -> {
                                LOG.warn("No dump retrieved");
                            }).body(DumpDto.class);
        } catch (final RestClientException e) {
            LOG.error("Catched error during retrieval of products", e);
        }
        return ret;
    }

    public DumpDto getContactsDump()
    {
        DumpDto ret = null;
        try {
            ret = getRestClient().get()
                            .uri(uriBuilder(getEFapsConfig().getContactPath() + "/dump"))
                            .retrieve()
                            .onStatus(HttpStatusCode::isError, (request,
                                                                response) -> {
                                LOG.warn("No dump retrieved");
                            }).body(DumpDto.class);

        } catch (final RestClientException e) {
            LOG.error("Catched error during retrieval of products", e);
        }
        return ret;
    }

    public List<CategoryDto> getCategories()
    {
        List<CategoryDto> ret = new ArrayList<>();
        try {
            ret = getRestClient().get()
                            .uri(uriBuilder(getEFapsConfig().getCategoryPath()))
                            .retrieve()
                            .body(new ParameterizedTypeReference<List<CategoryDto>>()
                            {
                            });
        } catch (final RestClientException e) {
            LOG.error("Catched error during retrieval of categories", e);
        }
        return ret;
    }

    public List<WorkspaceDto> getWorkspaces()
    {
        List<WorkspaceDto> ret = new ArrayList<>();
        try {
            ret = getRestClient().get()
                            .uri(uriBuilder(getEFapsConfig().getWorkspacePath()))
                            .retrieve()
                            .body(new ParameterizedTypeReference<List<WorkspaceDto>>()
                            {
                            });

        } catch (final RestClientException e) {
            LOG.error("Catched error during retrieval of workspaces", e);
        }
        return ret;
    }

    public List<WarehouseDto> getWarehouses()
    {
        List<WarehouseDto> ret = new ArrayList<>();
        try {
            ret = getRestClient().get()
                            .uri(uriBuilder(getEFapsConfig().getWarehousePath()))
                            .retrieve()
                            .body(new ParameterizedTypeReference<List<WarehouseDto>>()
                            {
                            });

        } catch (final RestClientException e) {
            LOG.error("Catched error during retrieval of workspaces", e);
        }
        return ret;
    }

    public List<PrinterDto> getPrinters()
    {
        List<PrinterDto> ret = new ArrayList<>();
        try {
            ret = getRestClient().get()
                            .uri(uriBuilder(getEFapsConfig().getPrinterPath()))
                            .retrieve()
                            .body(new ParameterizedTypeReference<List<PrinterDto>>()
                            {
                            });
        } catch (final RestClientException e) {
            LOG.error("Catched error during retrieval of workspaces", e);
        }
        return ret;
    }

    public List<InventoryEntryDto> getInventory(final String... productOids)
    {
        List<InventoryEntryDto> ret = new ArrayList<>();
        try {
            MultiValueMap<String, String> params = null;
            if (productOids != null && productOids.length > 0) {
                params = new LinkedMultiValueMap<>();
                params.put("productOid", List.of(productOids));
            }
            ret = getRestClient().get()
                            .uri(uriBuilder(getEFapsConfig().getPrinterPath(), params))
                            .retrieve()
                            .body(new ParameterizedTypeReference<List<InventoryEntryDto>>()
                            {
                            });

        } catch (final RestClientException e) {
            LOG.error("Catched error during retrieval of inventory", e);
        }
        return ret;
    }

    public List<PosDto> getPOSs()
    {
        List<PosDto> ret = new ArrayList<>();
        try {
            ret = getRestClient().get()
                            .uri(uriBuilder(getEFapsConfig().getPosPath()))
                            .retrieve()
                            .body(new ParameterizedTypeReference<List<PosDto>>()
                            {
                            });

        } catch (final RestClientException e) {
            LOG.error("Catched error during retrieval of poss", e);
        }
        return ret;
    }

    public List<UserDto> getUsers()
    {
        List<UserDto> ret = null;
        try {

            ret = getRestClient().get()
                            .uri(uriBuilder(getEFapsConfig().getUserPath()))
                            .retrieve()
                            .body(new ParameterizedTypeReference<List<UserDto>>()
                            {
                            });

        } catch (final RestClientException e) {
            LOG.error("Catched error during retrieval of users", e);
        }
        return ret;
    }

    public List<SequenceDto> getSequences()
    {
        List<SequenceDto> ret = new ArrayList<>();
        try {
            ret = getRestClient().get()
                            .uri(uriBuilder(getEFapsConfig().getSequencePath()))
                            .retrieve()
                            .body(new ParameterizedTypeReference<List<SequenceDto>>()
                            {
                            });

        } catch (final RestClientException e) {
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

            ret = getRestClient().get()
                            .uri(uriBuilder(getEFapsConfig().getContactPath(), params))
                            .retrieve()
                            .body(new ParameterizedTypeReference<List<ContactDto>>()
                            {
                            });

        } catch (final RestClientException e) {
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

            ret = getRestClient().get()
                            .uri(uriBuilder(getEFapsConfig().getContactPath() + "/{oid}", null,
                                            new HashMap<>(Map.of("oid", oid))))
                            .retrieve()
                            .body(ContactDto.class);

        } catch (final RestClientException e) {
            LOG.error("Catched error during retrieval of users", e);
        }
        return ret;
    }

    public List<EmployeeDto> getEmployees()
    {
        List<EmployeeDto> ret = new ArrayList<>();
        try {
            ret = getRestClient().get()
                            .uri(uriBuilder(getEFapsConfig().getEmployeePath()))
                            .retrieve()
                            .body(new ParameterizedTypeReference<List<EmployeeDto>>()
                            {
                            });

        } catch (final RestClientException e) {
            LOG.error("Catched error during retrieval of users", e);
        }
        return ret;
    }

    public List<ExchangeRateDto> getExchangeRates()
    {
        List<ExchangeRateDto> ret = null;
        try {

            ret = getRestClient().get()
                            .uri(uriBuilder(getEFapsConfig().getExchangeRatePath()))
                            .retrieve()
                            .body(new ParameterizedTypeReference<List<ExchangeRateDto>>()
                            {
                            });

        } catch (final RestClientException e) {
            LOG.error("Catched error during get for ExchangeRateDto", e);
        }
        return ret;
    }

    public List<Promotion> getPromotions()
    {
        List<Promotion> ret = null;
        try {
            ret = getRestClient().get()
                            .uri(uriBuilder(getEFapsConfig().getPromotionPath()))
                            .retrieve()
                            .body(new ParameterizedTypeReference<List<Promotion>>()
                            {
                            });

        } catch (final RestClientException e) {
            LOG.error("Catched error during get for ExchangeRateDto", e);
        }
        return ret;
    }

    public List<FileDto> getFiles()
    {
        List<FileDto> ret = null;
        try {
            ret = getRestClient().get()
                            .uri(uriBuilder(getEFapsConfig().getFilePath()))
                            .retrieve()
                            .body(new ParameterizedTypeReference<List<FileDto>>()
                            {
                            });
        } catch (final RestClientException e) {
            LOG.error("Catched error during get for ExchangeRateDto", e);
        }
        return ret;
    }

    public UpdateDto getUpdate()
    {
        UpdateDto ret = null;
        try {
            ret = getRestClient().get()
                            .uri(uriBuilder(getEFapsConfig().getUpdatePath()))
                            .retrieve()
                            .body(UpdateDto.class);

        } catch (final RestClientException e) {
            LOG.error("Catched error during  for UpdateDto", e);
        }
        return ret;
    }

    public boolean confirm(final UpdateConfirmationDto dto)
    {
        boolean ret = false;
        try {

            final var response = getRestClient().post()
                            .uri(uriBuilder(getEFapsConfig().getUpdatePath() + "/confirm"))
                            .retrieve().toBodilessEntity();
            ret = response.getStatusCode().is2xxSuccessful();
        } catch (final RestClientException e) {
            LOG.error("Catched error during get for UpdateDto", e);
        }
        return ret;
    }

    public String getFilledInTemplate(final String oid)
        throws IdentException, RestClientException
    {
        return getRestClient().get()
                        .uri(uriBuilder(getEFapsConfig().getUpdatePath() + "/filled-in-template/" + oid))
                        .retrieve()
                        .body(String.class);
    }

    public BalanceDto postBalance(final BalanceDto balance)
    {
        BalanceDto ret = balance;
        try {

            ret = getRestClient().post()
                            .uri(uriBuilder(getEFapsConfig().getBalancePath()))
                            .body(balance)
                            .retrieve()
                            .body(BalanceDto.class);

        } catch (final RestClientException e) {
            LOG.error("Catched error during post for Receipts", e);
        }
        return ret;
    }

    public boolean putBalance(final BalanceDto balance)
    {
        boolean ret = false;
        try {
            final var response = getRestClient().put()
                            .uri(uriBuilder(getEFapsConfig().getBalancePath() + "/" + balance.getOid()))
                            .body(balance)
                            .retrieve().toBodilessEntity();

            ret = response.getStatusCode().is2xxSuccessful();
        } catch (final RestClientException e) {
            LOG.error("Catched error during post for Balance update", e);
        }
        return ret;
    }

    public ReceiptDto postReceipt(final ReceiptDto receipt)
    {
        ReceiptDto ret = receipt;
        try {
            ret = getRestClient().post()
                            .uri(uriBuilder(getEFapsConfig().getReceiptPath()))
                            .body(receipt)
                            .retrieve()
                            .body(ReceiptDto.class);

        } catch (final RestClientException e) {
            LOG.error("Catched error during post for Receipts", e);
        }
        return ret;
    }

    public InvoiceDto postInvoice(final InvoiceDto invoice)
    {
        InvoiceDto ret = invoice;
        try {
            ret = getRestClient().post()
                            .uri(uriBuilder(getEFapsConfig().getInvoicePath()))
                            .body(invoice)
                            .retrieve()
                            .body(InvoiceDto.class);

        } catch (final RestClientException e) {
            LOG.error("Catched error during post for Invoices", e);
        }
        return ret;
    }

    public TicketDto postTicket(final TicketDto ticket)
    {
        TicketDto ret = ticket;
        try {
            ret = getRestClient().post()
                            .uri(uriBuilder(getEFapsConfig().getTicketPath()))
                            .body(ticket)
                            .retrieve()
                            .body(TicketDto.class);

        } catch (final RestClientException e) {
            LOG.error("Catched error during post for Tickets", e);
        }
        return ret;
    }

    public CreditNoteDto postCreditNote(final CreditNoteDto creditNote)
    {
        CreditNoteDto ret = creditNote;
        try {

            ret = getRestClient().post()
                            .uri(uriBuilder(getEFapsConfig().getCreditnotePath()))
                            .body(creditNote)
                            .retrieve()
                            .body(CreditNoteDto.class);

        } catch (final RestClientException e) {
            LOG.error("Catched error during post for CreditNotes", e);
        }
        return ret;
    }

    public OrderDto postOrder(final OrderDto order)
    {
        OrderDto ret = order;
        try {
            ret = getRestClient().post()
                            .uri(uriBuilder(getEFapsConfig().getOrderPath()))
                            .body(order)
                            .retrieve()
                            .body(OrderDto.class);

        } catch (final RestClientException e) {
            LOG.error("Catched error during post for Orders", e);
        }
        return ret;
    }

    public ContactDto postContact(final ContactDto contact)
    {
        ContactDto ret = contact;
        try {

            ret = getRestClient().post()
                            .uri(uriBuilder(getEFapsConfig().getContactPath()))
                            .body(contact)
                            .retrieve()
                            .body(ContactDto.class);

        } catch (final RestClientException e) {
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

            ret = getRestClient().put()
                            .uri(uriBuilder(getEFapsConfig().getContactPath() + "/{oid}"))
                            .body(contact)
                            .retrieve()
                            .body(ContactDto.class);

        } catch (final RestClientException e) {
            LOG.error("Catched error during put for Contact", e);
        }
        return ret;
    }

    public String postStocktaking(final StocktakingDto stocktaking)
    {
        String ret = null;
        try {

            ret = getRestClient().post()
                            .uri(uriBuilder(getEFapsConfig().getStocktakingPath()))
                            .body(stocktaking)
                            .retrieve()
                            .body(String.class);

        } catch (final RestClientException e) {
            LOG.error("Catched error during post for Stocktaking", e);
        }
        return ret;
    }

    public String postLogEntry(final LogEntryDto dto)
    {
        String ret = null;
        try {

            ret = getRestClient().post()
                            .uri(uriBuilder(getEFapsConfig().getLogEntryPath()))
                            .body(dto)
                            .retrieve()
                            .body(String.class);

        } catch (final RestClientException e) {
            LOG.error("Catched error during post for LogEntry", e);
        }
        return ret;
    }

    public boolean postReportToBase(final ReportToBaseDto dto)
    {
        boolean ret = false;
        try {

            final var response = getRestClient().post()
                            .uri(uriBuilder(getEFapsConfig().getReportToBasePath()))
                            .body(dto)
                            .retrieve()
                            .toBodilessEntity();
            ret = response.getStatusCode().is2xxSuccessful();

        } catch (final RestClientException e) {
            LOG.error("Catched error during post for ReportToBase", e);
        }
        return ret;
    }

    public boolean postReportStartup(final ReportStartupDto dto)
    {
        boolean ret = false;
        try {
            final var response = getRestClient().post()
                            .uri(uriBuilder(getEFapsConfig().getReportToBasePath()))
                            .body(dto)
                            .retrieve()
                            .toBodilessEntity();
            ret = response.getStatusCode().is2xxSuccessful();

        } catch (final RestClientException e) {
            LOG.error("Catched error during post for ReportToBase", e);
        }
        return ret;
    }

    public String postPromotionInfo(final PromoInfoSyncDto dto)
    {
        String ret = null;
        try {

            ret = getRestClient().post()
                            .uri(uriBuilder(getEFapsConfig().getPromotionInfoPath()))
                            .body(dto)
                            .retrieve()
                            .body(String.class);

        } catch (final RestClientException e) {
            LOG.error("Catched error during post for ReportToBase", e);
        }
        return ret;
    }

    public Checkout checkout(final String oid)
        throws RestClientException
    {
        Checkout ret = null;
        final var params = new LinkedMultiValueMap<String, String>();
        params.put("oid", Collections.singletonList(String.valueOf(oid)));
        params.put("ident", Collections.singletonList(getIdentifier()));

        final var response = getRestClient().get()
                        .uri(uriBuilder(getEFapsConfig().getCheckoutPath(), params))
                        .retrieve()
                        .toEntity(byte[].class);

        if (response.getBody() != null) {
            ret = new Checkout();
            ret.setContent(response.getBody());
            ret.setContentType(response.getHeaders().getContentType());
            ret.setFilename(response.getHeaders().getContentDisposition().getFilename());
        }
        return ret;
    }

    public List<InvoiceDto> retrieveInvoices(final String number)
    {
        List<InvoiceDto> ret = null;
        try {
            final var params = new LinkedMultiValueMap<String, String>();
            params.put("number", Collections.singletonList(String.valueOf(number)));

            ret = getRestClient().get()
                            .uri(uriBuilder(getEFapsConfig().getInvoicePath(), params))
                            .retrieve()
                            .body(new ParameterizedTypeReference<List<InvoiceDto>>()
                            {
                            });

        } catch (final RestClientException e) {
            LOG.error("Catched error during retrieval of users", e);
        }
        return ret;
    }

    public List<ReceiptDto> retrieveReceipts(final String number)
    {
        List<ReceiptDto> ret = null;
        try {
            final var params = new LinkedMultiValueMap<String, String>();
            params.put("number", Collections.singletonList(String.valueOf(number)));

            ret = getRestClient().get()
                            .uri(uriBuilder(getEFapsConfig().getReceiptPath(), params))
                            .retrieve()
                            .body(new ParameterizedTypeReference<List<ReceiptDto>>()
                            {
                            });

        } catch (final RestClientException e) {
            LOG.error("Catched error during retrieval of users", e);
        }
        return ret;
    }

    public List<CreditNoteDto> retrieveCreditNotes(final String number)
    {
        List<CreditNoteDto> ret = null;
        try {
            final var params = new LinkedMultiValueMap<String, String>();
            params.put("number", Collections.singletonList(String.valueOf(number)));

            ret = getRestClient().get()
                            .uri(uriBuilder(getEFapsConfig().getCreditnotePath(), params))
                            .retrieve()
                            .body(new ParameterizedTypeReference<List<CreditNoteDto>>()
                            {
                            });

        } catch (final RestClientException e) {
            LOG.error("Catched error during retrieval of users", e);
        }
        return ret;
    }

    public List<TicketDto> retrieveTickets(final String number)
    {
        List<TicketDto> ret = null;
        try {
            final var params = new LinkedMultiValueMap<String, String>();
            params.put("number", Collections.singletonList(String.valueOf(number)));

            ret = getRestClient().get()
                            .uri(uriBuilder(getEFapsConfig().getTicketPath(), params))
                            .retrieve()
                            .body(new ParameterizedTypeReference<List<TicketDto>>()
                            {
                            });

        } catch (final RestClientException e) {
            LOG.error("Catched error during retrieval of users", e);
        }
        return ret;
    }

    public ReceiptDto getReceipt(final String oid)
    {
        ReceiptDto ret = null;
        try {

            ret = getRestClient().get()
                            .uri(uriBuilder(getEFapsConfig().getReceiptPath() + "/" + oid))
                            .retrieve()
                            .body(ReceiptDto.class);

        } catch (final RestClientException e) {
            LOG.error("Catched error during retrieval of ReceiptDto", e);
        }
        return ret;
    }

    public InvoiceDto getInvoice(final String oid)
    {
        InvoiceDto ret = null;
        try {

            ret = getRestClient().get()
                            .uri(uriBuilder(getEFapsConfig().getInvoicePath() + "/" + oid))
                            .retrieve()
                            .body(InvoiceDto.class);

        } catch (final RestClientException e) {
            LOG.error("Catched error during retrieval of InvoiceDto", e);
        }
        return ret;
    }

    public TicketDto getTicket(final String oid)
    {
        TicketDto ret = null;
        try {

            ret = getRestClient().get()
                            .uri(uriBuilder(getEFapsConfig().getTicketPath() + "/" + oid))
                            .retrieve()
                            .body(TicketDto.class);

        } catch (final RestClientException e) {
            LOG.error("Catched error during retrieval of InvoiceDto", e);
        }
        return ret;
    }

    public CreditNoteDto getCreditNote(final String oid)
    {
        CreditNoteDto ret = null;
        try {

            ret = getRestClient().get()
                            .uri(uriBuilder(getEFapsConfig().getCreditnotePath() + "/" + oid))
                            .retrieve()
                            .body(CreditNoteDto.class);
        } catch (final RestClientException e) {
            LOG.error("Catched error during retrieval of CreditNoteDto", e);
        }
        return ret;
    }

    public RedeemValidityDto getCreditNoteRedeemValidity(final String oid)
    {
        RedeemValidityDto ret = null;
        try {

            ret = getRestClient().get()
                            .uri(uriBuilder(getEFapsConfig().getCreditnotePath() + "/" + oid + "/redeem-validity"))
                            .retrieve()
                            .body(RedeemValidityDto.class);

        } catch (final RestClientException e) {
            LOG.error("Catched error during retrieval of InvoiceDto", e);
        }
        return ret;
    }

    public EmitValidityResponseDto getCreditNoteEmitValidity(final EmitValidityDto dto)
    {
        EmitValidityResponseDto ret = null;
        try {

            ret = getRestClient().post()
                            .uri(uriBuilder(getEFapsConfig().getCreditnotePath() + "/emit-validity"))
                            .body(dto)
                            .retrieve()
                            .body(EmitValidityResponseDto.class);

        } catch (final RestClientException e) {
            LOG.error("Catched error during retrieval of InvoiceDto", e);
        }
        return ret;
    }

    public StoreStatusResponseDto evalStoreStatus(final StoreStatusRequestDto dto)
    {
        StoreStatusResponseDto ret = null;
        try {

            ret = getRestClient().post()
                            .uri(uriBuilder(getEFapsConfig().getStorePath()))
                            .body(dto)
                            .retrieve()
                            .body(StoreStatusResponseDto.class);

        } catch (final RestClientException e) {
            LOG.error("Catched error during post for ReportToBase", e);
        }
        return ret;
    }

    public List<LoyaltyPointsBalanceDto> retrieveLoyaltyBalance(final String term,
                                                                final Boolean includeContact)
    {
        List<LoyaltyPointsBalanceDto> ret = null;
        try {
            final var params = new LinkedMultiValueMap<String, String>();
            params.put("contact-identifier", Collections.singletonList(String.valueOf(term)));
            params.put("include-contact", Collections.singletonList(String.valueOf(includeContact)));

            ret = getRestClient().get()
                            .uri(uriBuilder(getEFapsConfig().getLoyaltyPath() + "/balance", params))
                            .retrieve()
                            .body(new ParameterizedTypeReference<List<LoyaltyPointsBalanceDto>>()
                            {
                            });

        } catch (final HttpStatusCodeException e) {
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                ret = new ArrayList<>();
            } else {
                LOG.error("Catched error during get for LoyaltyBalance", e);
            }
        }
        return ret;
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

                final String ident = getRestClient().get().uri(uriComp.toUri()).retrieve().body(String.class);
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

    @Override
    protected URI getBaseUrl()
    {
        return getEFapsConfig().getBaseUrl();
    }

    public Function<UriBuilder, URI> uriBuilder(final String path)
    {
        return uriBuilder(path, null, new HashMap<>());
    }

    public Function<UriBuilder, URI> uriBuilder(final String path,
                                                final MultiValueMap<String, String> params)
    {
        return uriBuilder(path, params, new HashMap<>());
    }

    public Function<UriBuilder, URI> uriBuilder(final String path,
                                                final Map<String, Object> uriVariables)
    {
        return uriBuilder(path, null, uriVariables);
    }

    public Function<UriBuilder, URI> uriBuilder(final String path,
                                                final MultiValueMap<String, String> params,
                                                final Map<String, Object> uriVariables)
    {
        uriVariables.put("identifier", getIdentifier());
        return uriBuilder -> uriBuilder
                        .path(path)
                        .queryParams(params)
                        .build(uriVariables);
    }

}
