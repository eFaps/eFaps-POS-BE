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
import org.efaps.pos.entity.CreditNote;
import org.efaps.pos.entity.Order;
import org.efaps.pos.entity.Ticket;
import org.efaps.pos.error.NotFoundException;
import org.efaps.pos.error.PreconditionException;
import org.efaps.pos.projection.PayableHead;
import org.efaps.pos.service.DocumentService;
import org.efaps.pos.util.Converter;
import org.springframework.http.MediaType;
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

    @PostMapping(path = "workspaces/{oid}/documents/receipts", produces = MediaType.APPLICATION_JSON_VALUE)
    public PosReceiptDto createReceipt(@PathVariable("oid") final String _oid,
                                       @RequestParam(name = "orderId") final String _orderId,
                                       @RequestBody final PosReceiptDto _receiptDto)
        throws PreconditionException
    {
        return Converter.toDto(documentService.createReceipt(_oid, _orderId, Converter.toEntity(_receiptDto)));
    }

    @PostMapping(path = "workspaces/{oid}/documents/invoices", produces = MediaType.APPLICATION_JSON_VALUE)
    public PosInvoiceDto createInvoice(@PathVariable("oid") final String _oid,
                                       @RequestParam(name = "orderId") final String _orderId,
                                       @RequestBody final PosInvoiceDto _invoiceDto)
        throws PreconditionException
    {
        return Converter.toDto(documentService.createInvoice(_oid, _orderId, Converter.toEntity(_invoiceDto)));
    }

    @PostMapping(path = "workspaces/{oid}/documents/tickets", produces = MediaType.APPLICATION_JSON_VALUE)
    public PosTicketDto createTicket(@PathVariable("oid") final String _oid,
                                     @RequestParam(name = "orderId") final String _orderId,
                                     @RequestBody final PosTicketDto _ticketDto)
        throws PreconditionException
    {
        return Converter.toDto(documentService.createTicket(_oid, _orderId, Converter.toEntity(_ticketDto)));
    }

    @PostMapping(path = "workspaces/{oid}/documents/creditnotes", produces = MediaType.APPLICATION_JSON_VALUE)
    public PosCreditNoteDto createCreditNote(@PathVariable("oid") final String _oid,
                                             @RequestBody final PosCreditNoteDto _ticketDto)
    {
        return Converter.toDto(documentService.createCreditNote(_oid, Converter.toEntity(_ticketDto)));
    }

    @PostMapping(path = "documents/orders", produces = MediaType.APPLICATION_JSON_VALUE)
    public PosOrderDto createOrder(@RequestBody final PosOrderDto _orderDto)
    {
        return Converter.toDto(documentService.createOrder(Converter.toEntity(_orderDto)));
    }

    @PostMapping(path = "workspaces/{oid}/documents/orders", produces = MediaType.APPLICATION_JSON_VALUE)
    public PosOrderDto createOrder(@PathVariable("oid") final String oid,
                                   @RequestBody final CreateDocumentDto createOrderDto)
    {
        return Converter.toDto(documentService.createOrder(oid, createOrderDto));
    }

    @PutMapping(path = "workspaces/{oid}/documents/orders/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PosOrderDto updateOrder(@PathVariable("oid") final String oid,
                                   @PathVariable("orderId") final String orderId,
                                   @RequestBody final CreateDocumentDto createOrderDto)
    {
        return Converter.toDto(documentService.updateOrder(oid, orderId, createOrderDto));
    }


    @PutMapping(path = "documents/orders/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PosOrderDto updateOrder(@PathVariable(name = "orderId") final String _orderId,
                                   @RequestBody final PosOrderDto _orderDto)
    {
        return Converter.toDto(documentService.updateOrder(_orderId, _orderDto));
    }

    @PutMapping(path = "documents/orders/{orderId}/contact/{contactId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PosOrderDto updateOrderWithContact(@PathVariable(name = "orderId") final String orderId,
                                              @PathVariable(name = "contactId") final String contactId)
    {
        return Converter.toDto(documentService.updateOrderWithContact(orderId, contactId));
    }

    @GetMapping(path = "documents/orders/{orderId}")
    public PosOrderDto getOrder(@PathVariable(name = "orderId") final String orderId)
    {
        return Converter.toDto(documentService.getOrder(orderId));
    }

    @DeleteMapping(path = "documents/orders/{orderId}")
    public void deleteOrder(@PathVariable(name = "orderId") final String _orderId)
    {
        documentService.deleteOrder(_orderId);
    }

    @GetMapping(path = "documents/orders", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PosOrderDto> getOrders(@RequestParam(name = "spot", required = false) final boolean _spots)
    {
        final Collection<Order> orders = _spots
                        ? documentService.getOrders4Spots()
                        : documentService.getOrders();
        return orders.stream()
                        .map(Converter::toDto)
                        .collect(Collectors.toList());
    }

    @GetMapping(path = "documents/orders", produces = MediaType.APPLICATION_JSON_VALUE, params = { "status" })
    public List<PosOrderDto> getOrders(@RequestParam(name = "status") final DocStatus _status)
    {
        return documentService.getOrders(_status).stream()
                        .map(Converter::toDto)
                        .collect(Collectors.toList());
    }

    @GetMapping(path = "documents/orders", produces = MediaType.APPLICATION_JSON_VALUE, params = { "term" })
    public List<PosOrderDto> findOrders(@RequestParam(name = "term") final String _term)
    {
        return documentService.findOrders(_term).stream()
                        .map(Converter::toDto)
                        .collect(Collectors.toList());
    }

    @GetMapping(path = "documents/receipts", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PayableHeadDto> getReceipts4Balance(@RequestParam(name = "balanceOid") final String _balanceOid)
    {
        final Collection<PayableHead> receipts = documentService.getReceiptHeads4Balance(_balanceOid);
        return receipts.stream()
                        .map(Converter::toDto)
                        .collect(Collectors.toList());
    }

    @GetMapping(path = "documents/receipts", produces = MediaType.APPLICATION_JSON_VALUE, params = { "term" })
    public List<PayableHeadDto> findReceipts(@RequestParam(name = "term") final String _term)
    {
        final Collection<PayableHead> receipts = documentService.findReceipts(_term);
        return receipts.stream()
                        .map(Converter::toDto)
                        .collect(Collectors.toList());
    }

    @GetMapping(path = "documents/receipts", produces = MediaType.APPLICATION_JSON_VALUE, params = { "ident" })
    public PosReceiptDto getReceiptByIdent(@RequestParam(name = "ident") final String ident)
        throws NotFoundException
    {
        final var receipt = documentService.findReceipt(ident)
                        .orElseThrow(() -> new NotFoundException("Receipt not found"));
        return Converter.toDto(receipt);
    }

    @GetMapping(path = "documents/receipts/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PosReceiptDto getReceipt(@PathVariable("id") final String _id)
        throws NotFoundException
    {
        final var receipt = documentService.getReceipt(_id);
        if (receipt == null) {
            throw new NotFoundException("Receipt not found");
        }
        return Converter.toDto(receipt);
    }

    @GetMapping(path = "documents/invoices/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PosInvoiceDto getInvoice(@PathVariable("id") final String _id)
        throws NotFoundException
    {
        final var invoice = documentService.getInvoice(_id);
        if (invoice == null) {
            throw new NotFoundException("Invoice not found");
        }
        return Converter.toDto(invoice);
    }

    @GetMapping(path = "documents/invoices", produces = MediaType.APPLICATION_JSON_VALUE, params = { "ident" })
    public PosInvoiceDto getInvoiceByIdent(@RequestParam(name = "ident") final String ident)
        throws NotFoundException
    {
        final var invoice = documentService.findInvoice(ident)
                        .orElseThrow(() -> new NotFoundException("Receipt not found"));
        return Converter.toDto(invoice);
    }

    @GetMapping(path = "documents/tickets/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PosTicketDto getTicket(@PathVariable("id") final String _id)
        throws NotFoundException
    {
        final Ticket ticket = documentService.getTicket(_id);
        if (ticket == null) {
            throw new NotFoundException("Ticket not found");
        }
        return Converter.toDto(ticket);
    }

    @GetMapping(path = "documents/creditnotes/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PosCreditNoteDto getCreditNote(@PathVariable("id") final String _id)
        throws NotFoundException
    {
        final var creditNote = documentService.getCreditNote(_id);
        if (creditNote == null) {
            throw new NotFoundException("CreditNote not found");
        }
        return Converter.toDto(creditNote);
    }

    @GetMapping(path = "documents/invoices", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PayableHeadDto> getInvoices4Balance(@RequestParam(name = "balanceOid") final String _balanceOid)
    {
        final Collection<PayableHead> receipts = documentService.getInvoiceHeads4Balance(_balanceOid);
        return receipts.stream()
                        .map(Converter::toDto)
                        .collect(Collectors.toList());
    }

    @GetMapping(path = "documents/invoices", produces = MediaType.APPLICATION_JSON_VALUE, params = { "term" })
    public List<PayableHeadDto> findInvoices(@RequestParam(name = "term") final String _term)
    {
        final Collection<PayableHead> invoices = documentService.findInvoices(_term);
        return invoices.stream()
                        .map(Converter::toDto)
                        .collect(Collectors.toList());
    }

    @GetMapping(path = "documents/tickets", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PayableHeadDto> getTickets4Balance(@RequestParam(name = "balanceOid") final String _balanceOid)
    {
        final Collection<PayableHead> receipts = documentService.getTicketHeads4Balance(_balanceOid);
        return receipts.stream()
                        .map(Converter::toDto)
                        .collect(Collectors.toList());
    }

    @GetMapping(path = "documents/tickets", produces = MediaType.APPLICATION_JSON_VALUE, params = { "term" })
    public List<PayableHeadDto> findTickets(@RequestParam(name = "term") final String _term)
    {
        final Collection<PayableHead> tickets = documentService.findTickets(_term);
        return tickets.stream()
                        .map(Converter::toDto)
                        .collect(Collectors.toList());
    }

    @GetMapping(path = "documents/creditnotes", produces = MediaType.APPLICATION_JSON_VALUE, params = { "term" })
    public List<PayableHeadDto> findCreditNotes(@RequestParam(name = "term") final String _term)
    {
        final var creditNotes = documentService.findCreditNotes(_term);
        return creditNotes.stream()
                        .map(Converter::toDto)
                        .collect(Collectors.toList());
    }

    @GetMapping(path = "documents/creditnotes", produces = MediaType.APPLICATION_JSON_VALUE, params = { "balanceOid" })
    public List<PayableHeadDto> getCreditNotes4Balance(@RequestParam(name = "balanceOid") final String _balanceOid)
    {
        final Collection<PayableHead> receipts = documentService.getCreditNoteHeads4Balance(_balanceOid);
        return receipts.stream()
                        .map(Converter::toDto)
                        .collect(Collectors.toList());
    }

    @GetMapping(path = "documents/creditnotes", produces = MediaType.APPLICATION_JSON_VALUE, params = {
                    "sourceDocOid" })
    public List<PosCreditNoteDto> getCreditNotes4SourceDocument(@RequestParam(name = "sourceDocOid") final String _sourceDocOid)
    {
        final Collection<CreditNote> creditnotes = documentService.getCreditNotes4SourceDocument(_sourceDocOid);
        return creditnotes.stream()
                        .map(Converter::toDto)
                        .collect(Collectors.toList());
    }
}
