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
package org.efaps.pos.client;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.efaps.pos.ConfigProperties;
import org.efaps.pos.dto.CategoryDto;
import org.efaps.pos.dto.ContactDto;
import org.efaps.pos.dto.InvoiceDto;
import org.efaps.pos.dto.PosDto;
import org.efaps.pos.dto.ProductDto;
import org.efaps.pos.dto.ReceiptDto;
import org.efaps.pos.dto.SequenceDto;
import org.efaps.pos.dto.TicketDto;
import org.efaps.pos.dto.UserDto;
import org.efaps.pos.dto.WorkspaceDto;
import org.efaps.pos.sso.SSOClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
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
    private final ConfigProperties config;
    private final RestTemplate restTemplate;
    private final SSOClient ssoClient;

    @Autowired
    public EFapsClient(final ConfigProperties _config,
                       final RestTemplate _restTemplate,
                       final SSOClient _ssoClient)
    {
        this.config = _config;
        this.restTemplate = _restTemplate;
        this.ssoClient = _ssoClient;
    }

    public List<ProductDto> getProducts()
    {
        List<ProductDto> ret = new ArrayList<>();
        try {
            final RequestEntity<?> requestEntity = get(this.config.getEFaps().getProductPath());
            final ResponseEntity<List<ProductDto>> response = this.restTemplate.exchange(requestEntity,
                            new ParameterizedTypeReference<List<ProductDto>>()
                            {
                            });
            ret = response.getBody();

        } catch (final RestClientException e) {
            LOG.error("Catched error during retrieval of products", e);
        }
        return ret;
    }

    public List<CategoryDto> getCategories()
    {
        List<CategoryDto> ret = new ArrayList<>();
        try {
            final RequestEntity<?> requestEntity = get(this.config.getEFaps().getCategoryPath());
            final ResponseEntity<List<CategoryDto>> response = this.restTemplate.exchange(requestEntity,
                            new ParameterizedTypeReference<List<CategoryDto>>()
                            {
                            });
            ret = response.getBody();
        } catch (final RestClientException e) {
            LOG.error("Catched error during retrieval of categories", e);
        }
        return ret;
    }

    public List<WorkspaceDto> getWorkspaces()
    {
        List<WorkspaceDto> ret = new ArrayList<>();
        try {
            final RequestEntity<?> requestEntity = get(this.config.getEFaps().getWorkspacePath());
            final ResponseEntity<List<WorkspaceDto>> response = this.restTemplate.exchange(requestEntity,
                            new ParameterizedTypeReference<List<WorkspaceDto>>()
                            {
                            });
            ret = response.getBody();
        } catch (final RestClientException e) {
            LOG.error("Catched error during retrieval of workspaces", e);
        }
        return ret;
    }

    public List<PosDto> getPOSs()
    {
        List<PosDto> ret = new ArrayList<>();
        try {
            final RequestEntity<?> requestEntity = get(this.config.getEFaps().getPosPath());
            final ResponseEntity<List<PosDto>> response = this.restTemplate.exchange(requestEntity,
                            new ParameterizedTypeReference<List<PosDto>>()
                            {
                            });
            ret = response.getBody();
        } catch (final RestClientException e) {
            LOG.error("Catched error during retrieval of poss", e);
        }
        return ret;
    }

    public List<UserDto> getUsers()
    {
        List<UserDto> ret = new ArrayList<>();
        try {
            final RequestEntity<?> requestEntity = get(this.config.getEFaps().getUserPath());
            final ResponseEntity<List<UserDto>> response = this.restTemplate.exchange(requestEntity,
                            new ParameterizedTypeReference<List<UserDto>>()
                            {
                            });
            ret = response.getBody();
        } catch (final RestClientException e) {
            LOG.error("Catched error during retrieval of users", e);
        }
        return ret;
    }

    public List<SequenceDto> getSequences()
    {
        List<SequenceDto> ret = new ArrayList<>();
        try {
            final RequestEntity<?> requestEntity = get(this.config.getEFaps().getSequencePath());
            final ResponseEntity<List<SequenceDto>> response = this.restTemplate.exchange(requestEntity,
                            new ParameterizedTypeReference<List<SequenceDto>>()
                            {
                            });
            ret = response.getBody();
        } catch (final RestClientException e) {
            LOG.error("Catched error during retrieval of users", e);
        }
        return ret;
    }

    public List<ContactDto> getContacts()
    {
        List<ContactDto> ret = new ArrayList<>();
        try {
            final RequestEntity<?> requestEntity = get(this.config.getEFaps().getContactPath());
            final ResponseEntity<List<ContactDto>> response = this.restTemplate.exchange(requestEntity,
                            new ParameterizedTypeReference<List<ContactDto>>()
                            {
                            });
            ret = response.getBody();
        } catch (final RestClientException e) {
            LOG.error("Catched error during retrieval of users", e);
        }
        return ret;
    }

    public ReceiptDto postReceipt(final ReceiptDto _receipt)
    {
        ReceiptDto ret = _receipt;
        try {
            final RequestEntity<ReceiptDto> requestEntity = post(this.config.getEFaps().getReceiptPath(), _receipt);
            final ResponseEntity<ReceiptDto> response = this.restTemplate.exchange(requestEntity, ReceiptDto.class);
            ret = response.getBody();
        } catch (final RestClientException e) {
            LOG.error("Catched error during post for Receipts", e);
        }
        return ret;
    }

    public InvoiceDto postInvoice(final InvoiceDto _invoice)
    {
        InvoiceDto ret = _invoice;
        try {
            final RequestEntity<InvoiceDto> requestEntity = post(this.config.getEFaps().getInvoicePath(), _invoice);
            final ResponseEntity<InvoiceDto> response = this.restTemplate.exchange(requestEntity, InvoiceDto.class);
            ret = response.getBody();
        } catch (final RestClientException e) {
            LOG.error("Catched error during post for Invoices", e);
        }
        return ret;
    }

    public TicketDto postTicket(final TicketDto _ticket)
    {
        TicketDto ret = _ticket;
        try {
            final RequestEntity<TicketDto> requestEntity = post(this.config.getEFaps().getTicketPath(), _ticket);
            final ResponseEntity<TicketDto> response = this.restTemplate.exchange(requestEntity, TicketDto.class);
            ret = response.getBody();
        } catch (final RestClientException e) {
            LOG.error("Catched error during post for Tickets", e);
        }
        return ret;
    }

    public ContactDto postContact(final ContactDto _contact)
    {
        ContactDto ret = _contact;
        try {
            final RequestEntity<ContactDto> requestEntity = post(this.config.getEFaps().getContactPath(), _contact);
            final ResponseEntity<ContactDto> response = this.restTemplate.exchange(requestEntity, ContactDto.class);
            ret = response.getBody();
        } catch (final RestClientException e) {
            LOG.error("Catched error during post for Invoices", e);
        }
        return ret;
    }

    public Checkout checkout(final String _oid)
    {
        Checkout ret = null;
        try {
            final URI uri = UriComponentsBuilder.fromUri(this.config.getEFaps().getBaseUrl()).path(this.config
                            .getEFaps().getCheckoutPath()).queryParam("oid", _oid).build().toUri();
            final RequestEntity<?> requestEntity = get(uri);
            final ResponseEntity<byte[]> response = this.restTemplate.exchange(requestEntity, byte[].class);
            ret = new Checkout();
            ret.setContent(response.getBody());
            ret.setContentType(response.getHeaders().getContentType());
            ret.setFilename(response.getHeaders().getContentDisposition().getFilename());
        } catch (final RestClientException e) {
            LOG.error("Catched error during checkout for oid: '{}'", _oid);
        }
        return ret;
    }

    private String getAuth()
    {
        String auth = "";
        if (this.config.getSso() != null && this.config.getSso().getUrl() != null) {
            auth = "Bearer " + this.ssoClient.getToken();
        } else if (this.config.getAuth() != null) {
            auth = "Basic " + Base64.getEncoder().encodeToString((this.config.getAuth().getUser() + ":" + this.config
                            .getAuth().getPassword()).getBytes(StandardCharsets.UTF_8));
        }
        return auth;
    }

    private UriComponents getUriComponent(final String _path)
    {
        return UriComponentsBuilder.fromUri(this.config.getEFaps().getBaseUrl()).path(_path).build();
    }

    private RequestEntity<?> get(final URI _uri)
    {
        return RequestEntity.get(_uri).header("Authorization", getAuth()).build();
    }

    private RequestEntity<?> get(final String _path)
    {
        return get(getUriComponent(_path).toUri());
    }

    private <T> RequestEntity<T> post(final String _path, final T _body)
    {
        return RequestEntity.post(getUriComponent(_path).toUri()).header("Authorization", getAuth()).accept(
                        MediaType.APPLICATION_JSON).body(_body);
    }
}
