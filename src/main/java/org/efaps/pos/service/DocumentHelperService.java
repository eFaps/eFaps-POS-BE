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
package org.efaps.pos.service;

import java.util.Optional;

import org.efaps.pos.entity.AbstractDocument;
import org.efaps.pos.entity.AbstractPayableDocument;
import org.efaps.pos.entity.CreditNote;
import org.efaps.pos.entity.Invoice;
import org.efaps.pos.entity.Order;
import org.efaps.pos.entity.Receipt;
import org.efaps.pos.entity.Ticket;
import org.efaps.pos.repository.CreditNoteRepository;
import org.efaps.pos.repository.InvoiceRepository;
import org.efaps.pos.repository.OrderRepository;
import org.efaps.pos.repository.ReceiptRepository;
import org.efaps.pos.repository.TicketRepository;
import org.efaps.pos.util.Utils;
import org.springframework.stereotype.Service;

@Service
public class DocumentHelperService
{

    private final OrderRepository orderRepository;
    private final ReceiptRepository receiptRepository;
    private final InvoiceRepository invoiceRepository;
    private final TicketRepository ticketRepository;
    private final CreditNoteRepository creditNoteRepository;

    public DocumentHelperService(final OrderRepository orderRepository,
                                 final ReceiptRepository receiptRepository,
                                 final InvoiceRepository invoiceRepository,
                                 final TicketRepository ticketRepository,
                                 final CreditNoteRepository creditNoteRepository)
    {
        this.orderRepository = orderRepository;
        this.receiptRepository = receiptRepository;
        this.invoiceRepository = invoiceRepository;
        this.ticketRepository = ticketRepository;
        this.creditNoteRepository = creditNoteRepository;
    }

    public Optional<AbstractDocument<?>> getDocument(final String ident)
    {
        Optional<AbstractDocument<?>> ret = getOrder(ident).map(this::toAbstract);
        if (ret.isEmpty()) {
            ret = getPayable(ident).map(doc -> ((AbstractDocument<?>) doc));
        }
        return ret;
    }

    public Optional<AbstractPayableDocument<?>> getPayable(final String ident)
    {
        return Utils.isOid(ident) ? getPayableByOid(ident) : getPayableById(ident);
    }

    public Optional<AbstractPayableDocument<?>> getPayableByOid(final String documentId)
    {
        Optional<AbstractPayableDocument<?>> ret = getReceiptByOid(documentId).map(this::toPayable);
        if (ret.isEmpty()) {
            ret = getInvoiceByOid(documentId).map(this::toPayable);
        }
        if (ret.isEmpty()) {
            ret = getTicketByOid(documentId).map(this::toPayable);
        }
        if (ret.isEmpty()) {
            ret = getCreditNoteByOid(documentId).map(this::toPayable);
        }
        return ret;
    }

    public Optional<AbstractPayableDocument<?>> getPayableById(final String documentId)
    {
        Optional<AbstractPayableDocument<?>> ret = getReceiptById(documentId).map(this::toPayable);
        if (ret.isEmpty()) {
            ret = getInvoiceById(documentId).map(this::toPayable);
        }
        if (ret.isEmpty()) {
            ret = getTicketById(documentId).map(this::toPayable);
        }
        if (ret.isEmpty()) {
            ret = getCreditNoteById(documentId).map(this::toPayable);
        }
        return ret;
    }

    public Optional<Order> getOrder(final String ident)
    {
        return Utils.isOid(ident) ? getOrderByOid(ident) : getOrderById(ident);
    }

    public Optional<Order> getOrderByOid(final String oid)
    {
        return orderRepository.findByOid(oid);
    }

    public Optional<Order> getOrderById(final String id)
    {
        return orderRepository.findById(id);
    }

    public Optional<Receipt> getReceipt(final String ident)
    {
        return Utils.isOid(ident) ? getReceiptByOid(ident) : getReceiptById(ident);
    }

    public Optional<Receipt> getReceiptByOid(final String oid)
    {
        return receiptRepository.findByOid(oid);
    }

    public Optional<Receipt> getReceiptById(final String id)
    {
        return receiptRepository.findById(id);
    }

    public Optional<Invoice> getInvoice(final String ident)
    {
        return Utils.isOid(ident) ? getInvoiceByOid(ident) : getInvoiceById(ident);
    }

    public Optional<Invoice> getInvoiceByOid(final String oid)
    {
        return invoiceRepository.findByOid(oid);
    }

    public Optional<Invoice> getInvoiceById(final String id)
    {
        return invoiceRepository.findById(id);
    }

    public Optional<Ticket> getTicket(final String ident)
    {
        return Utils.isOid(ident) ? getTicketByOid(ident) : getTicketById(ident);
    }

    public Optional<Ticket> getTicketByOid(final String oid)
    {
        return ticketRepository.findByOid(oid);
    }

    public Optional<Ticket> getTicketById(final String id)
    {
        return ticketRepository.findById(id);
    }

    public Optional<CreditNote> getCreditNote(final String ident)
    {
        return Utils.isOid(ident) ? getCreditNoteByOid(ident) : getCreditNoteById(ident);
    }

    public Optional<CreditNote> getCreditNoteByOid(final String oid)
    {
        return creditNoteRepository.findByOid(oid);
    }

    public Optional<CreditNote> getCreditNoteById(final String id)
    {
        return creditNoteRepository.findById(id);
    }

    private AbstractDocument<?> toAbstract(AbstractDocument<?> doc)
    {
        return doc;
    }

    private AbstractPayableDocument<?> toPayable(AbstractPayableDocument<?> doc)
    {
        return doc;
    }

}
