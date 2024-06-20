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

    public Optional<AbstractDocument<?>> getDocument(final String documentId)
    {
        Optional<AbstractDocument<?>> ret = getOrder(documentId).map(this::toAbstract);
        if (ret.isEmpty()) {
            ret = getPayable(documentId).map(doc -> ((AbstractDocument<?>) doc));
        }
        return ret;
    }

    private AbstractDocument<?> toAbstract(AbstractDocument<?> doc)
    {
        return doc;
    }

    public Optional<AbstractPayableDocument<?>> getPayable(final String documentId)
    {
        Optional<AbstractPayableDocument<?>> ret = getReceipt(documentId).map(this::toPayable);
        if (ret.isEmpty()) {
            ret = getInvoice(documentId).map(this::toPayable);
        }
        if (ret.isEmpty()) {
            ret = getTicket(documentId).map(this::toPayable);
        }
        if (ret.isEmpty()) {
            ret = getCreditNote(documentId).map(this::toPayable);
        }
        return ret;
    }

    private AbstractPayableDocument<?> toPayable(AbstractPayableDocument<?> doc)
    {
        return doc;
    }

    public Optional<Order> getOrder(final String orderid)
    {
        return orderRepository.findById(orderid);
    }

    public Optional<Receipt> getReceipt(final String orderid)
    {
        return receiptRepository.findById(orderid);
    }

    public Optional<Invoice> getInvoice(final String orderid)
    {
        return invoiceRepository.findById(orderid);
    }

    public Optional<Ticket> getTicket(final String orderid)
    {
        return ticketRepository.findById(orderid);
    }

    public Optional<CreditNote> getCreditNote(final String orderid)
    {
        return creditNoteRepository.findById(orderid);
    }

}
