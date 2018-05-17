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
package org.efaps.pos.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import org.efaps.pos.dto.CompanyDto;
import org.efaps.pos.dto.ContactDto;
import org.efaps.pos.dto.DocStatus;
import org.efaps.pos.dto.DocType;
import org.efaps.pos.dto.PosInvoiceDto;
import org.efaps.pos.dto.PosReceiptDto;
import org.efaps.pos.dto.PosTicketDto;
import org.efaps.pos.entity.AbstractDocument;
import org.efaps.pos.entity.AbstractDocument.TaxEntry;
import org.efaps.pos.entity.Invoice;
import org.efaps.pos.entity.Order;
import org.efaps.pos.entity.Pos;
import org.efaps.pos.entity.Receipt;
import org.efaps.pos.entity.Ticket;
import org.efaps.pos.interfaces.IInvoiceListener;
import org.efaps.pos.interfaces.IPos;
import org.efaps.pos.interfaces.IReceiptListener;
import org.efaps.pos.interfaces.ITicketListener;
import org.efaps.pos.respository.InvoiceRepository;
import org.efaps.pos.respository.OrderRepository;
import org.efaps.pos.respository.ReceiptRepository;
import org.efaps.pos.respository.TicketRepository;
import org.efaps.pos.util.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.serviceloader.ServiceListFactoryBean;
import org.springframework.stereotype.Service;


@Service
public class DocumentService
{
    private static final Logger LOG = LoggerFactory.getLogger(DocumentService.class);

    private final ServiceListFactoryBean receiptListeners;
    private final ServiceListFactoryBean invoiceListeners;
    private final ServiceListFactoryBean ticketListeners;
    private final PosService posService;
    private final SequenceService sequenceService;
    private final ContactService contactService;
    private final OrderRepository orderRepository;
    private final ReceiptRepository receiptRepository;
    private final InvoiceRepository invoiceRepository;
    private final TicketRepository ticketRepository;

    @Autowired
    public DocumentService(@Qualifier("receiptListeners") final ServiceListFactoryBean _receiptListeners,
                           @Qualifier("invoiceListeners") final ServiceListFactoryBean _invoiceListeners,
                           @Qualifier("ticketListeners") final ServiceListFactoryBean _ticketListeners,
                           final PosService _posService, final SequenceService _sequenceService,
                           final ContactService _contactService, final OrderRepository _orderRepository,
                           final ReceiptRepository _receiptRepository, final InvoiceRepository _invoiceRepository,
                           final TicketRepository _ticketRepository)
    {
        this.receiptListeners = _receiptListeners;
        this.invoiceListeners = _invoiceListeners;
        this.ticketListeners = _ticketListeners;
        this.posService = _posService;
        this.sequenceService = _sequenceService;
        this.orderRepository = _orderRepository;
        this.receiptRepository = _receiptRepository;
        this.contactService = _contactService;
        this.invoiceRepository = _invoiceRepository;
        this.ticketRepository = _ticketRepository;
    }

    public List<Order> getOrders() {
        return this.orderRepository.findAll();
    }

    public Order createOrder(final Order _order)
    {
        _order.setNumber(this.sequenceService.getNextOrder());
        verifyDocument(_order);
        return this.orderRepository.insert(_order);
    }

    public Order updateOrder(final Order _order)
    {
        verifyDocument(_order);
        return this.orderRepository.save(_order);
    }

    private void verifyDocument(final AbstractDocument<?> _document) {
        final BigDecimal netTotal = _document.getNetTotal().setScale(2, RoundingMode.HALF_UP);
        _document.setNetTotal(netTotal);
        BigDecimal taxTotal = BigDecimal.ZERO;
        for (final TaxEntry taxEntry : _document.getTaxes()) {
            final BigDecimal amount = taxEntry.getAmount().setScale(2, RoundingMode.HALF_UP);
            taxEntry.setAmount(amount);
            taxTotal = taxTotal.add(amount);
        }
        _document.setCrossTotal(netTotal.add(taxTotal).setScale(2, RoundingMode.HALF_UP));
    }

    public void deleteOrder(final String _orderId)
    {
        final Optional<Order> opt = this.orderRepository.findById(_orderId);
        if (opt.isPresent()) {
            final Order order = opt.get();
            if (DocStatus.OPEN.equals(order.getStatus())) {
                order.setStatus(DocStatus.CANCELED);
                this.orderRepository.save(order);
            }
        }
    }

    public Receipt createReceipt(final String _workspaceOid, final Receipt _receipt)
    {
        validateContact(_workspaceOid, _receipt);
        _receipt.setNumber(this.sequenceService.getNext(_workspaceOid, DocType.RECEIPT));
        Receipt ret = this.receiptRepository.insert(_receipt);
        try {
            @SuppressWarnings("unchecked")
            final List<IReceiptListener> listeners =  (List<IReceiptListener>) this.receiptListeners.getObject();
            if (listeners != null) {
               PosReceiptDto dto = Converter.toDto(ret);
                for (final IReceiptListener listener  :listeners) {
                    dto = (PosReceiptDto) listener.onCreate(
                                    getPos(this.posService.getPos4Workspace(_workspaceOid)), dto);
                }
                ret = this.receiptRepository.save(Converter.toEntity(dto));
            }
        } catch (final Exception e) {
            LOG.error("Wow that should not happen", e);
        }
        return ret;
    }

    public Invoice createInvoice(final String _workspaceOid, final Invoice _invoice)
    {
        validateContact(_workspaceOid, _invoice);
        _invoice.setNumber(this.sequenceService.getNext(_workspaceOid, DocType.INVOICE));
        Invoice ret = this.invoiceRepository.insert(_invoice);
        try {
            @SuppressWarnings("unchecked")
            final List<IInvoiceListener> listeners = (List<IInvoiceListener>) this.invoiceListeners.getObject();
            if (listeners != null) {
                PosInvoiceDto dto = Converter.toDto(ret);
                for (final IInvoiceListener listener : listeners) {
                    dto = (PosInvoiceDto) listener.onCreate(getPos(this.posService.getPos4Workspace(_workspaceOid)),
                                    dto);
                }
                ret = this.invoiceRepository.save(Converter.toEntity(dto));
            }
        } catch (final Exception e) {
            LOG.error("Wow that should not happen", e);
        }
        return ret;
    }

    public Ticket createTicket(final String _workspaceOid, final Ticket _ticket)
    {
        validateContact(_workspaceOid, _ticket);
        _ticket.setNumber(this.sequenceService.getNext(_workspaceOid, DocType.TICKET));
        Ticket ret = this.ticketRepository.insert(_ticket);
        try {
            @SuppressWarnings("unchecked")
            final List<ITicketListener> listeners = (List<ITicketListener>) this.ticketListeners.getObject();
            if (listeners != null) {
                PosTicketDto dto = Converter.toDto(ret);
                for (final ITicketListener listener : listeners) {
                    dto = (PosTicketDto) listener.onCreate(getPos(this.posService.getPos4Workspace(_workspaceOid)),
                                    dto);
                }
                ret = this.ticketRepository.save(Converter.toEntity(dto));
            }
        } catch (final Exception e) {
            LOG.error("Wow that should not happen", e);
        }
        return ret;
    }

    private void validateContact(final String _workspaceOid, final AbstractDocument<?> _document)
    {
        if (_document.getContactOid() == null) {
            final Pos pos = this.posService.getPos4Workspace(_workspaceOid);
            if (pos != null) {
                _document.setContactOid(pos.getDefaultContactOid());
            }
        }
    }

    private IPos getPos(final Pos _pos)
    {
        final String name = _pos.getName();
        final String currency = _pos.getCurrency();
        final CompanyDto companyDto = CompanyDto.builder()
                        .withName(_pos.getCompany().getName())
                        .withTaxNumber(_pos.getCompany().getTaxNumber())
                        .build();
        final ContactDto contactDto = Converter.toDto(this.contactService.get(_pos.getDefaultContactOid()));

        return new IPos()
        {

            @Override
            public String getName()
            {
                return name;
            }

            @Override
            public String getCurrency()
            {
                return currency;
            }

            @Override
            public CompanyDto getCompany()
            {
                return companyDto;
            }

            @Override
            public ContactDto getDefaultContact()
            {
                return contactDto;
            }
        };
    }
}
