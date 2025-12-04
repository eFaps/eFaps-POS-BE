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
package org.efaps.pos.controller;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.efaps.pos.config.IApi;
import org.efaps.pos.dto.CreateDocumentDto;
import org.efaps.pos.dto.DocStatus;
import org.efaps.pos.dto.PayableHeadDto;
import org.efaps.pos.dto.PosCreditNoteDto;
import org.efaps.pos.dto.PosInvoiceDto;
import org.efaps.pos.dto.PosOrderDto;
import org.efaps.pos.dto.PosReceiptDto;
import org.efaps.pos.dto.PosTicketDto;
import org.efaps.pos.dto.ValidateForCreditNoteDto;
import org.efaps.pos.dto.ValidateForCreditNoteResponseDto;
import org.efaps.pos.entity.CreditNote;
import org.efaps.pos.entity.Order;
import org.efaps.pos.entity.Ticket;
import org.efaps.pos.entity.User;
import org.efaps.pos.error.NotFoundException;
import org.efaps.pos.error.PreconditionException;
import org.efaps.pos.projection.PayableHead;
import org.efaps.pos.service.DocumentService;
import org.efaps.pos.util.Converter;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(IApi.BASEPATH)
public class DocumentController
{

    private final DocumentService documentService;

    public DocumentController(final DocumentService _service)
    {
        documentService = _service;
    }

    @PostMapping(path = "receipts", produces = MediaType.APPLICATION_JSON_VALUE)
    public PosReceiptDto createReceipt(@RequestParam(name = "orderId") final String orderId,
                                       @RequestParam(name = "workspaceOid") final String workspaceOid,
                                       @RequestBody final PosReceiptDto receiptDto)
        throws PreconditionException
    {
        return Converter.toDto(documentService.createReceipt(workspaceOid, orderId, Converter.toEntity(receiptDto)));
    }

    @PostMapping(path = "invoices", produces = MediaType.APPLICATION_JSON_VALUE)
    public PosInvoiceDto createInvoice(@RequestParam(name = "orderId") final String orderId,
                                       @RequestParam(name = "workspaceOid") final String workspaceOid,
                                       @RequestBody final PosInvoiceDto invoiceDto)
        throws PreconditionException
    {
        return Converter.toDto(documentService.createInvoice(workspaceOid, orderId, Converter.toEntity(invoiceDto)));
    }

    @PostMapping(path = "tickets", produces = MediaType.APPLICATION_JSON_VALUE)
    public PosTicketDto createTicket(@RequestParam(name = "orderId") final String orderId,
                                     @RequestParam(name = "workspaceOid") final String workspaceOid,
                                     @RequestBody final PosTicketDto ticketDto)
        throws PreconditionException
    {
        return Converter.toDto(documentService.createTicket(workspaceOid, orderId, Converter.toEntity(ticketDto)));
    }

    @PostMapping(path = "creditnotes", produces = MediaType.APPLICATION_JSON_VALUE)
    public PosCreditNoteDto createCreditNote(@RequestParam(name = "workspaceOid") final String workspaceOid,
                                             @RequestBody final PosCreditNoteDto ticketDto)
    {
        return Converter.toDto(documentService.createCreditNote(workspaceOid, Converter.toEntity(ticketDto)));
    }

    @PostMapping(path = "orders", produces = MediaType.APPLICATION_JSON_VALUE)
    public PosOrderDto createOrder(@RequestParam(name = "workspaceOid") final String workspaceOid,
                                   @RequestBody final PosOrderDto orderDto)
    {
        return Converter.toDto(documentService.createOrder(workspaceOid, Converter.toEntity(orderDto)));
    }

    @PutMapping(path = "orders/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PosOrderDto updateOrder(@PathVariable(name = "orderId") final String orderId,
                                   @RequestParam(name = "workspaceOid") final String workspaceOid,
                                   @RequestBody final PosOrderDto orderDto)
    {
        return Converter.toDto(documentService.updateOrder(workspaceOid, orderId, orderDto));
    }

    @PutMapping(path = { "orders/{orderId}/contact/{contactId}",
                    "documents/orders/{orderId}/contact/{contactId}" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public PosOrderDto updateOrderWithContact(@PathVariable(name = "orderId") final String orderId,
                                              @PathVariable(name = "contactId") final String contactId)
    {
        return Converter.toDto(documentService.updateOrderWithContact(orderId, contactId));
    }

    @PutMapping(path = { "orders/{orderId}/loyaltyContact/{contactId}",
                    "documents/orders/{orderId}/loyaltyContact/{contactId}" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public PosOrderDto updateOrderWithLoyaltyContact(@PathVariable(name = "orderId") final String orderId,
                                                     @PathVariable(name = "contactId") final String contactId)
    {
        return Converter.toDto(documentService.updateOrderWithLoyaltyContact(orderId, contactId));
    }

    @GetMapping(path = { "orders/{orderId}", "documents/orders/{orderId}" })
    public PosOrderDto getOrder(@PathVariable(name = "orderId") final String orderId)
    {
        return Converter.toDto(documentService.getOrderById(orderId));
    }

    @DeleteMapping(path = { "orders/{orderId}", "documents/orders/{orderId}" })
    public void deleteOrder(@PathVariable(name = "orderId") final String orderId)
    {
        documentService.deleteOrder(orderId);
    }

    @GetMapping(path = { "orders", "documents/orders" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PosOrderDto> getOrders(@RequestParam(name = "spot", required = false) final boolean _spots)
    {
        final Collection<Order> orders = _spots
                        ? documentService.getOrders4Spots()
                        : documentService.getOrders();
        return orders.stream()
                        .map(Converter::toDto)
                        .collect(Collectors.toList());
    }

    @GetMapping(path = { "orders", "documents/orders" }, produces = MediaType.APPLICATION_JSON_VALUE, params = {
                    "status" })
    public List<PosOrderDto> getOrders(@RequestParam(name = "status") final DocStatus _status)
    {
        return documentService.getOrders(_status).stream()
                        .map(Converter::toDto)
                        .collect(Collectors.toList());
    }

    @GetMapping(path = { "orders", "documents/orders" }, produces = MediaType.APPLICATION_JSON_VALUE, params = {
                    "term" })
    public List<PosOrderDto> findOrders(@RequestParam(name = "term") final String _term)
    {
        return documentService.findOrders(_term).stream()
                        .map(Converter::toDto)
                        .collect(Collectors.toList());
    }

    @GetMapping(path = { "receipts", "documents/receipts" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PayableHeadDto> getReceipts4Balance(@RequestParam(name = "balanceOid") final String _balanceOid)
    {
        final Collection<PayableHead> receipts = documentService.getReceiptHeads4Balance(_balanceOid);
        return receipts.stream()
                        .map(Converter::toDto)
                        .collect(Collectors.toList());
    }

    @GetMapping(path = { "receipts", "documents/receipts" }, produces = MediaType.APPLICATION_JSON_VALUE, params = {
                    "term" })
    public List<PayableHeadDto> findReceipts(@RequestParam(name = "term") final String _term)
    {
        final Collection<PayableHead> receipts = documentService.findReceipts(_term);
        return receipts.stream()
                        .map(Converter::toDto)
                        .collect(Collectors.toList());
    }

    @GetMapping(path = { "receipts", "documents/receipts" }, produces = MediaType.APPLICATION_JSON_VALUE, params = {
                    "ident" })
    public PosReceiptDto getReceiptByIdent(@RequestParam(name = "ident") final String ident,
                                           @RequestParam(name = "remote", required = false) final Boolean remote)
        throws NotFoundException
    {
        final var receipt = documentService.findReceipt(ident, remote)
                        .orElseThrow(() -> new NotFoundException("Receipt not found"));
        return Converter.toDto(receipt);
    }

    @GetMapping(path = { "receipts/{id}", "documents/receipts/{id}" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public PosReceiptDto getReceipt(@PathVariable("id") final String _id)
        throws NotFoundException
    {
        final var receipt = documentService.getReceiptById(_id);
        if (receipt == null) {
            throw new NotFoundException("Receipt not found");
        }
        return Converter.toDto(receipt);
    }

    @GetMapping(path = { "receipts" }, produces = MediaType.APPLICATION_JSON_VALUE, params = { "number" })
    public List<PosReceiptDto> retrieveReceipts(@RequestParam(name = "number") final String number)
    {
        return documentService.retrieveReceipts(number).stream().map(Converter::toDto).toList();
    }

    @GetMapping(path = { "invoices/{id}", "documents/invoices/{id}" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public PosInvoiceDto getInvoice(@PathVariable("id") final String _id)
        throws NotFoundException
    {
        final var invoice = documentService.getInvoiceById(_id);
        if (invoice == null) {
            throw new NotFoundException("Invoice not found");
        }
        return Converter.toDto(invoice);
    }

    @GetMapping(path = { "invoices", "documents/invoices" }, produces = MediaType.APPLICATION_JSON_VALUE, params = {
                    "ident" })
    public PosInvoiceDto getInvoiceByIdent(@RequestParam(name = "ident") final String ident,
                                           @RequestParam(name = "remote", required = false) final Boolean remote)
        throws NotFoundException
    {
        final var invoice = documentService.findInvoice(ident, remote)
                        .orElseThrow(() -> new NotFoundException("Receipt not found"));
        return Converter.toDto(invoice);
    }

    @GetMapping(path = { "invoices" }, produces = MediaType.APPLICATION_JSON_VALUE, params = { "number" })
    public List<PosInvoiceDto> retrieveInvoices(@RequestParam(name = "number") final String number)
    {
        return documentService.retrieveInvoices(number).stream().map(Converter::toDto).toList();
    }

    @GetMapping(path = { "tickets/{id}", "documents/tickets/{id}" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public PosTicketDto getTicket(@PathVariable("id") final String _id)
        throws NotFoundException
    {
        final Ticket ticket = documentService.getTicketById(_id);
        if (ticket == null) {
            throw new NotFoundException("Ticket not found");
        }
        return Converter.toDto(ticket);
    }

    @GetMapping(path = { "tickets" }, produces = MediaType.APPLICATION_JSON_VALUE, params = { "number" })
    public List<PosTicketDto> retrieveTickets(@RequestParam(name = "number") final String number)
    {
        return documentService.retrieveTickets(number).stream().map(Converter::toDto).toList();
    }

    @GetMapping(path = { "creditnotes/{id}",
                    "documents/creditnotes/{id}" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public PosCreditNoteDto getCreditNote(@PathVariable("id") final String _id)
        throws NotFoundException
    {
        final var creditNote = documentService.getCreditNoteById(_id);
        if (creditNote == null) {
            throw new NotFoundException("CreditNote not found");
        }
        return Converter.toDto(creditNote);
    }

    @GetMapping(path = { "invoices", "documents/invoices" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PayableHeadDto> getInvoices4Balance(@RequestParam(name = "balanceOid") final String _balanceOid)
    {
        final Collection<PayableHead> receipts = documentService.getInvoiceHeads4Balance(_balanceOid);
        return receipts.stream()
                        .map(Converter::toDto)
                        .collect(Collectors.toList());
    }

    @GetMapping(path = { "invoices", "documents/invoices" }, produces = MediaType.APPLICATION_JSON_VALUE, params = {
                    "term" })
    public List<PayableHeadDto> findInvoices(@RequestParam(name = "term") final String _term)
    {
        final Collection<PayableHead> invoices = documentService.findInvoices(_term);
        return invoices.stream()
                        .map(Converter::toDto)
                        .collect(Collectors.toList());
    }

    @GetMapping(path = { "tickets", "documents/tickets" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PayableHeadDto> getTickets4Balance(@RequestParam(name = "balanceOid") final String _balanceOid)
    {
        final Collection<PayableHead> receipts = documentService.getTicketHeads4Balance(_balanceOid);
        return receipts.stream()
                        .map(Converter::toDto)
                        .collect(Collectors.toList());
    }

    @GetMapping(path = { "tickets", "documents/tickets" }, produces = MediaType.APPLICATION_JSON_VALUE, params = {
                    "term" })
    public List<PayableHeadDto> findTickets(@RequestParam(name = "term") final String _term)
    {
        final Collection<PayableHead> tickets = documentService.findTickets(_term);
        return tickets.stream()
                        .map(Converter::toDto)
                        .collect(Collectors.toList());
    }

    @GetMapping(path = { "creditnotes",
                    "documents/creditnotes" }, produces = MediaType.APPLICATION_JSON_VALUE, params = { "term" })
    public List<PayableHeadDto> findCreditNotes(@RequestParam(name = "term") final String _term)
    {
        final var creditNotes = documentService.findCreditNotes(_term);
        return creditNotes.stream()
                        .map(Converter::toDto)
                        .collect(Collectors.toList());
    }

    @GetMapping(path = { "creditnotes",
                    "documents/creditnotes" }, produces = MediaType.APPLICATION_JSON_VALUE, params = { "balanceOid" })
    public List<PayableHeadDto> getCreditNotes4Balance(@RequestParam(name = "balanceOid") final String _balanceOid)
    {
        final Collection<PayableHead> receipts = documentService.getCreditNoteHeads4Balance(_balanceOid);
        return receipts.stream()
                        .map(Converter::toDto)
                        .collect(Collectors.toList());
    }

    @GetMapping(path = { "creditnotes",
                    "documents/creditnotes" }, produces = MediaType.APPLICATION_JSON_VALUE, params = { "sourceDocOid" })
    public List<PosCreditNoteDto> getCreditNotes4SourceDocument(@RequestParam(name = "sourceDocOid") final String _sourceDocOid)
    {
        final Collection<CreditNote> creditnotes = documentService.getCreditNotes4SourceDocument(_sourceDocOid);
        return creditnotes.stream()
                        .map(Converter::toDto)
                        .collect(Collectors.toList());
    }

    @GetMapping(path = { "creditnotes" }, produces = MediaType.APPLICATION_JSON_VALUE, params = { "number" })
    public List<PosCreditNoteDto> retrieveCreditNotes(@RequestParam(name = "number") final String number)
    {
        return documentService.retrieveCreditNotes(number).stream().map(Converter::toDto).toList();
    }

    @PostMapping(path = { "creditnotes/validate" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ValidateForCreditNoteResponseDto validateForCreditNote(@RequestBody final ValidateForCreditNoteDto dto)
    {
        return documentService.validateForCreditNote(dto);
    }

    // mobile
    @PostMapping(path = "workspaces/{oid}/documents/orders", produces = MediaType.APPLICATION_JSON_VALUE)
    public PosOrderDto createOrder(final Authentication authentication,
                                   @PathVariable("oid") final String oid,
                                   @RequestBody final CreateDocumentDto createOrderDto)
    {
        return Converter.toDto(documentService.createOrder((User) authentication.getPrincipal(), oid, createOrderDto));
    }

    @PutMapping(path = "workspaces/{oid}/documents/orders/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PosOrderDto updateOrder(final Authentication authentication,
                                   @PathVariable("oid") final String oid,
                                   @PathVariable("orderId") final String orderId,
                                   @RequestBody final CreateDocumentDto createOrderDto)
    {
        return Converter.toDto(documentService.updateOrder((User) authentication.getPrincipal(), oid, orderId,
                        createOrderDto));
    }

}
