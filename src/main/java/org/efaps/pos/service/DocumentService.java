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
package org.efaps.pos.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.efaps.pos.dto.ContactDto;
import org.efaps.pos.dto.CreateDocumentDto;
import org.efaps.pos.dto.Currency;
import org.efaps.pos.dto.DocStatus;
import org.efaps.pos.dto.DocType;
import org.efaps.pos.dto.PosCreditNoteDto;
import org.efaps.pos.dto.PosInvoiceDto;
import org.efaps.pos.dto.PosOrderDto;
import org.efaps.pos.dto.PosReceiptDto;
import org.efaps.pos.dto.PosTicketDto;
import org.efaps.pos.entity.AbstractDocument;
import org.efaps.pos.entity.AbstractDocument.Item;
import org.efaps.pos.entity.AbstractPayableDocument;
import org.efaps.pos.entity.Balance;
import org.efaps.pos.entity.CreditNote;
import org.efaps.pos.entity.Invoice;
import org.efaps.pos.entity.Order;
import org.efaps.pos.entity.Pos;
import org.efaps.pos.entity.Receipt;
import org.efaps.pos.entity.Ticket;
import org.efaps.pos.error.PreconditionException;
import org.efaps.pos.interfaces.ICreditNoteListener;
import org.efaps.pos.interfaces.IInvoiceListener;
import org.efaps.pos.interfaces.IPos;
import org.efaps.pos.interfaces.IReceiptListener;
import org.efaps.pos.interfaces.ITicketListener;
import org.efaps.pos.projection.PayableHead;
import org.efaps.pos.repository.BalanceRepository;
import org.efaps.pos.repository.CreditNoteRepository;
import org.efaps.pos.repository.InvoiceRepository;
import org.efaps.pos.repository.OrderRepository;
import org.efaps.pos.repository.ReceiptRepository;
import org.efaps.pos.repository.TicketRepository;
import org.efaps.pos.util.Converter;
import org.efaps.pos.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AddFieldsOperation;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

@Service
public class DocumentService
{

    private static final Logger LOG = LoggerFactory.getLogger(DocumentService.class);

    private final ConfigService configService;
    private final PosService posService;
    private final SequenceService sequenceService;
    private final ContactService contactService;
    private final InventoryService inventoryService;
    private final CalculatorService calculatorService;
    private final OrderRepository orderRepository;
    private final ReceiptRepository receiptRepository;
    private final InvoiceRepository invoiceRepository;
    private final TicketRepository ticketRepository;
    private final CreditNoteRepository creditNoteRepository;
    private final BalanceRepository balanceRepository;
    private final List<IReceiptListener> receiptListeners;
    private final List<IInvoiceListener> invoiceListeners;
    private final List<ITicketListener> ticketListeners;
    private final List<ICreditNoteListener> creditNoteListeners;

    private final MongoTemplate mongoTemplate;

    @Autowired
    public DocumentService(final MongoTemplate _mongoTemplate,
                           final ConfigService configService,
                           final PosService _posService,
                           final SequenceService _sequenceService,
                           final ContactService _contactService,
                           final InventoryService _inventoryService,
                           final CalculatorService calculatorService,
                           final OrderRepository _orderRepository,
                           final ReceiptRepository _receiptRepository,
                           final InvoiceRepository _invoiceRepository,
                           final TicketRepository _ticketRepository,
                           final CreditNoteRepository _creditNoteRepository,
                           final BalanceRepository _balanceRepository,
                           final Optional<List<IReceiptListener>> _receiptListeners,
                           final Optional<List<IInvoiceListener>> _invoiceListeners,
                           final Optional<List<ITicketListener>> _ticketListeners,
                           final Optional<List<ICreditNoteListener>> _creditNoteListener)
    {
        mongoTemplate = _mongoTemplate;
        this.configService = configService;
        posService = _posService;
        sequenceService = _sequenceService;
        orderRepository = _orderRepository;
        inventoryService = _inventoryService;
        this.calculatorService = calculatorService;
        receiptRepository = _receiptRepository;
        contactService = _contactService;
        invoiceRepository = _invoiceRepository;
        ticketRepository = _ticketRepository;
        creditNoteRepository = _creditNoteRepository;
        balanceRepository = _balanceRepository;
        receiptListeners = _receiptListeners.isPresent() ? _receiptListeners.get() : Collections.emptyList();
        invoiceListeners = _invoiceListeners.isPresent() ? _invoiceListeners.get() : Collections.emptyList();
        ticketListeners = _ticketListeners.isPresent() ? _ticketListeners.get() : Collections.emptyList();
        creditNoteListeners = _creditNoteListener.isPresent() ? _creditNoteListener.get() : Collections.emptyList();
    }

    public Order getOrder(final String _orderid)
    {
        return orderRepository.findById(_orderid).orElse(null);
    }

    public List<Order> getOrders()
    {
        return orderRepository.findAll();
    }

    public Collection<Order> getOrders(final DocStatus _status)
    {
        return orderRepository.findByStatus(_status);
    }

    public Collection<Order> getOrders4Spots()
    {
        return orderRepository.findBySpotIsNotNullAndStatus(DocStatus.OPEN);
    }

    public Optional<Order> getOrder4Payable(final AbstractPayableDocument<?> _payable)
    {
        Optional<Order> ret = orderRepository.findByPayableOid(_payable.getOid() == null ? _payable.getId()
                        : _payable.getOid());
        if (ret.isEmpty() && _payable.getOid() != null) {
            ret = orderRepository.findByPayableOid(_payable.getId());
        }
        return ret;
    }

    public Collection<Order> findOrders(final String _term)
    {
        return orderRepository.findByNumberLikeIgnoreCase(_term);
    }

    public Order createOrder(final Order _order)
    {
        _order.setNumber(sequenceService.getNextOrder());
        return orderRepository.insert(_order);
    }

    public Order createOrder(final String workspaceOid,
                             final CreateDocumentDto createOrderDto)
    {
        final var items = new ArrayList<Item>();
        final var counter = new AtomicInteger(1);
        createOrderDto.getItems().forEach(item -> {
            items.add(new Item().setIndex(counter.getAndIncrement())
                            .setQuantity(item.getQuantity())
                            .setProductOid(item.getProductOid()));
        });
        final var order = new Order()
                        .setDate(LocalDate.now())
                        .setCurrency(createOrderDto.getCurrency())
                        .setWorkspaceOid(workspaceOid);
        order.setNumber(sequenceService.getNextOrder());
        order.setItems(items);
        calculatorService.calculate(workspaceOid, order);
        return orderRepository.insert(order);
    }

    public Order updateOrder(final String id,
                             final PosOrderDto dto)
    {
        final var order = orderRepository.findById(id).orElseThrow();
        Converter.mapToEntity(dto, order);
        return orderRepository.save(order);
    }

    public void deleteOrder(final String _orderId)
    {
        final Optional<Order> opt = orderRepository.findById(_orderId);
        if (opt.isPresent()) {
            final Order order = opt.get();
            if (DocStatus.OPEN.equals(order.getStatus())) {
                order.setStatus(DocStatus.CANCELED);
                order.setSpot(null);
                orderRepository.save(order);
            }
        }
    }

    public Receipt createReceipt(final String _workspaceOid,
                                 final String _orderId,
                                 final Receipt _receipt)
        throws PreconditionException
    {
        validateOrder(_orderId);
        validateContact(_workspaceOid, _receipt);
        _receipt.setNumber(sequenceService.getNext(_workspaceOid, DocType.RECEIPT, null));
        Receipt ret = receiptRepository.insert(_receipt);
        try {
            if (!receiptListeners.isEmpty()) {
                PosReceiptDto dto = Converter.toDto(ret);
                for (final IReceiptListener listener : receiptListeners) {
                    dto = (PosReceiptDto) listener.onCreate(getPos(posService.getPos4Workspace(_workspaceOid)), dto,
                                    configService.getProperties());
                }
                ret = receiptRepository.save(Converter.mapToEntity(dto, ret));
            }
        } catch (final Exception e) {
            LOG.error("Wow that should not happen", e);
        }
        closeOrder(_orderId, ret.getId());
        inventoryService.removeFromInventory(_workspaceOid, ret);
        return ret;
    }

    public Invoice createInvoice(final String _workspaceOid,
                                 final String _orderId,
                                 final Invoice _invoice)
        throws PreconditionException
    {
        validateOrder(_orderId);
        validateContact(_workspaceOid, _invoice);
        _invoice.setNumber(sequenceService.getNext(_workspaceOid, DocType.INVOICE, null));
        Invoice ret = invoiceRepository.insert(_invoice);
        try {
            if (!invoiceListeners.isEmpty()) {
                PosInvoiceDto dto = Converter.toDto(ret);
                for (final IInvoiceListener listener : invoiceListeners) {
                    dto = (PosInvoiceDto) listener.onCreate(getPos(posService.getPos4Workspace(_workspaceOid)), dto,
                                    configService.getProperties());
                }
                ret = invoiceRepository.save(Converter.mapToEntity(dto, ret));
            }
        } catch (final Exception e) {
            LOG.error("Wow that should not happen", e);
        }
        closeOrder(_orderId, ret.getId());
        inventoryService.removeFromInventory(_workspaceOid, ret);
        return ret;
    }

    public Ticket createTicket(final String _workspaceOid,
                               final String _orderId,
                               final Ticket _ticket)
        throws PreconditionException
    {
        validateOrder(_orderId);
        validateContact(_workspaceOid, _ticket);
        _ticket.setNumber(sequenceService.getNext(_workspaceOid, DocType.TICKET, null));
        Ticket ret = ticketRepository.insert(_ticket);
        try {
            if (!ticketListeners.isEmpty()) {
                PosTicketDto dto = Converter.toDto(ret);
                for (final ITicketListener listener : ticketListeners) {
                    dto = (PosTicketDto) listener.onCreate(getPos(posService.getPos4Workspace(_workspaceOid)), dto,
                                    configService.getProperties());
                }
                ret = ticketRepository.save(Converter.mapToEntity(dto, ret));
            }
        } catch (final Exception e) {
            LOG.error("Wow that should not happen", e);
        }
        closeOrder(_orderId, ret.getId());
        inventoryService.removeFromInventory(_workspaceOid, ret);
        return ret;
    }

    public CreditNote createCreditNote(final String _workspaceOid,
                                       final CreditNote _creditNote)
    {
        validateContact(_workspaceOid, _creditNote);
        final var reference = ReferenceService.getReferenceByIdent(_creditNote.getSourceDocOid());
        _creditNote.setNumber(sequenceService.getNext(_workspaceOid, DocType.CREDITNOTE, reference.getDocType()));
        _creditNote.setDate(LocalDate.now());
        CreditNote ret = creditNoteRepository.insert(_creditNote);
        try {
            if (!creditNoteListeners.isEmpty()) {

                PosCreditNoteDto dto = Converter.toDto(ret);
                for (final ICreditNoteListener listener : creditNoteListeners) {
                    dto = (PosCreditNoteDto) listener.onCreate(getPos(posService.getPos4Workspace(_workspaceOid)), dto,
                                    configService.getProperties());
                }
                ret = creditNoteRepository.save(Converter.mapToEntity(dto, ret));
            }
        } catch (final Exception e) {
            LOG.error("Wow that should not happen", e);
        }
        return ret;
    }

    private void validateOrder(final String _orderId)
        throws PreconditionException
    {
        final Optional<Order> orderOpt = orderRepository.findById(_orderId);
        if (orderOpt.isEmpty()) {
            throw new PreconditionException("Order cannot be found");
        } else if (!DocStatus.OPEN.equals(orderOpt.get().getStatus())) {
            throw new PreconditionException("Order must be in state OPEN");
        }
    }

    private void closeOrder(final String _orderId,
                            final String _payableId)
    {
        final Optional<Order> orderOpt = orderRepository.findById(_orderId);
        if (orderOpt.isPresent()) {
            final Order order = orderOpt.get();
            order.setStatus(DocStatus.CLOSED);
            order.setPayableOid(_payableId);
            orderRepository.save(order);
        }
    }

    public Receipt getReceipt(final String _documentId)
    {
        return receiptRepository.findById(_documentId).orElse(null);
    }

    public Optional<Receipt> findReceipt(final String _identifier)
    {
        return receiptRepository.findOne(
                        Example.of(new Receipt().setId(_identifier).setOid(_identifier), ExampleMatcher.matchingAny()));
    }

    public Optional<CreditNote> findCreditNote(final String _identifier)
    {
        return creditNoteRepository.findOne(
                        Example.of(new CreditNote().setId(_identifier).setOid(_identifier),
                                        ExampleMatcher.matchingAny()));
    }

    public Collection<Receipt> getReceipts4Balance(final String _key)
    {
        return receiptRepository.findByBalanceOid(evalBalanceOid(_key));
    }

    public Collection<PayableHead> getReceiptHeads4Balance(final String _balanceKey)
    {
        return getPayableHeads4Balance(_balanceKey, "receipts");
    }

    public Invoice getInvoice(final String _documentId)
    {
        return invoiceRepository.findById(_documentId).orElse(null);
    }

    public Optional<Invoice> findInvoice(final String _identifier)
    {
        return invoiceRepository.findOne(
                        Example.of(new Invoice().setId(_identifier).setOid(_identifier), ExampleMatcher.matchingAny()));
    }

    public Collection<PayableHead> getInvoiceHeads4Balance(final String _balanceKey)
    {
        return getPayableHeads4Balance(_balanceKey, "invoices");
    }

    public Collection<Invoice> getInvoices4Balance(final String _key)
    {
        return invoiceRepository.findByBalanceOid(evalBalanceOid(_key));
    }

    public Ticket getTicket(final String _documentId)
    {
        return ticketRepository.findById(_documentId).orElse(null);
    }

    public Collection<Ticket> getTickets4Balance(final String _key)
    {
        return ticketRepository.findByBalanceOid(evalBalanceOid(_key));
    }

    public CreditNote getCreditNote(final String _documentId)
    {
        return creditNoteRepository.findById(_documentId).orElse(null);
    }

    public Collection<CreditNote> getCreditNotes4Balance(final String _balanceKey)
    {
        return creditNoteRepository.findByBalanceOid(evalBalanceOid(_balanceKey));
    }

    public Collection<PayableHead> getTicketHeads4Balance(final String _balanceKey)
    {
        return getPayableHeads4Balance(_balanceKey, "tickets");
    }

    public Collection<PayableHead> getCreditNoteHeads4Balance(final String _balanceKey)
    {
        return getPayableHeads4Balance(_balanceKey, "creditnotes");
    }

    private Collection<PayableHead> getPayableHeads4Balance(final String _balanceKey,
                                                            final String _collection)
    {
        final var ret = new ArrayList<PayableHead>();
        ret.addAll(mongoTemplate.aggregate(getBalanceAggregation4OpenDocs(_balanceKey), _collection, PayableHead.class)
                        .getMappedResults());
        ret.addAll(mongoTemplate.aggregate(getBalanceAggregation4ClosedDocs(_balanceKey), _collection,
                        PayableHead.class).getMappedResults());
        return ret;
    }

    private Aggregation getBalanceAggregation4ClosedDocs(final String _balanceKey)
    {
        final LookupOperation lookupOperation = LookupOperation.newLookup()
                        .from("orders")
                        .localField("oid")
                        .foreignField("payableOid")
                        .as("orders");
        return Aggregation.newAggregation(
                        Aggregation.match(Criteria.where("status").is(DocStatus.CLOSED)),
                        Aggregation.match(Criteria.where("balanceOid").is(evalBalanceOid(_balanceKey))),
                        lookupOperation);
    }

    private Aggregation getBalanceAggregation4OpenDocs(final String _balanceKey)
    {
        final var addField = AddFieldsOperation.addField("joinField").withValue(new Document("$toString", "$_id"))
                        .build();

        final LookupOperation lookupOperation = LookupOperation.newLookup()
                        .from("orders")
                        .localField("joinField")
                        .foreignField("payableOid")
                        .as("orders");

        return Aggregation.newAggregation(
                        Aggregation.match(Criteria.where("status").is(DocStatus.OPEN)),
                        Aggregation.match(Criteria.where("balanceOid").is(evalBalanceOid(_balanceKey))),
                        addField,
                        lookupOperation);
    }

    /**
     * Newly created Balances use the id as key. The UI might ask still with the
     * id instead of the OID.
     *
     * @param _key key to be check if it has to be converted
     * @return oid or id
     */
    protected String evalBalanceOid(final String _key)
    {
        String ret = _key;
        if (!Utils.isOid(_key)) {
            final Optional<Balance> balanceopt = balanceRepository.findById(_key);
            if (balanceopt.isPresent()) {
                final Balance balance = balanceopt.get();
                if (!StringUtils.isEmpty(balance.getOid())) {
                    ret = balance.getOid();
                }
            }
        }
        return ret;
    }

    public AbstractDocument<?> getDocument(final String _documentId)
    {
        AbstractDocument<?> ret = getOrder(_documentId);
        if (ret == null) {
            ret = getPayable(_documentId);
        }
        return ret;
    }

    public AbstractPayableDocument<?> getPayable(final String _documentId)
    {
        AbstractPayableDocument<?> ret = getReceipt(_documentId);
        if (ret == null) {
            ret = getInvoice(_documentId);
        }
        if (ret == null) {
            ret = getTicket(_documentId);
        }
        if (ret == null) {
            ret = getCreditNote(_documentId);
        }
        return ret;
    }

    public Collection<PayableHead> findInvoices(final String _term)
    {
        final LookupOperation lookupOperation = LookupOperation.newLookup()
                        .from("orders")
                        .localField("oid")
                        .foreignField("payableOid")
                        .as("orders");
        final Aggregation aggregation = Aggregation.newAggregation(
                        Aggregation.match(Criteria.where("number").regex(_term, "i")),
                        lookupOperation);
        return mongoTemplate.aggregate(aggregation, "invoices", PayableHead.class).getMappedResults();
    }

    public Collection<PayableHead> findReceipts(final String _term)
    {
        final LookupOperation lookupOperation = LookupOperation.newLookup()
                        .from("orders")
                        .localField("oid")
                        .foreignField("payableOid")
                        .as("orders");
        final Aggregation aggregation = Aggregation.newAggregation(
                        Aggregation.match(Criteria.where("number").regex(_term, "i")),
                        lookupOperation);
        return mongoTemplate.aggregate(aggregation, "receipts", PayableHead.class).getMappedResults();
    }

    public Collection<PayableHead> findTickets(final String _term)
    {
        final LookupOperation lookupOperation = LookupOperation.newLookup()
                        .from("orders")
                        .localField("oid")
                        .foreignField("payableOid")
                        .as("orders");
        final Aggregation aggregation = Aggregation.newAggregation(
                        Aggregation.match(Criteria.where("number").regex(_term, "i")),
                        lookupOperation);
        return mongoTemplate.aggregate(aggregation, "tickets", PayableHead.class).getMappedResults();
    }

    public Collection<PayableHead> findCreditNotes(final String _term)
    {
        final LookupOperation lookupOperation = LookupOperation.newLookup()
                        .from("orders")
                        .localField("oid")
                        .foreignField("payableOid")
                        .as("orders");
        final Aggregation aggregation = Aggregation.newAggregation(
                        Aggregation.match(Criteria.where("number").regex(_term, "i")),
                        lookupOperation);
        return mongoTemplate.aggregate(aggregation, "creditnotes", PayableHead.class).getMappedResults();
    }

    public Collection<CreditNote> getCreditNotes4SourceDocument(final String sourceDocOid)
    {
        return creditNoteRepository.findBySourceDocOid(sourceDocOid);
    }

    public void retrigger4Receipt(final String identifier)
    {
        LOG.warn("Retriggering for Receipt: {}", identifier);
        final var entity = findReceipt(identifier);
        if (entity.isPresent()) {
            final var dto = Converter.toDto(entity.get());
            for (final var listener : receiptListeners) {
                LOG.warn("Retriggering listener for dto: {}", dto);
                listener.onCreate(null, dto, configService.getProperties());
            }
        }
    }

    public void retrigger4Invoice(final String identifier)
    {
        LOG.warn("Retriggering for Invoice: {}", identifier);
        final var entity = findInvoice(identifier);
        if (entity.isPresent()) {
            final var dto = Converter.toDto(entity.get());
            for (final var listener : invoiceListeners) {
                LOG.warn("Retriggering listener for dto: {}", dto);
                listener.onCreate(null, dto, configService.getProperties());
            }
        }
    }

    public void retrigger4CreditNote(final String identifier)
    {
        LOG.warn("Retriggering for CreditNote: {}", identifier);
        final var entity = findCreditNote(identifier);
        if (entity.isPresent()) {
            final var dto = Converter.toDto(entity.get());
            for (final var listener : creditNoteListeners) {
                LOG.warn("Retriggering listener for dto: {}", dto);
                listener.onCreate(null, dto, configService.getProperties());
            }
        }
    }

    private void validateContact(final String _workspaceOid,
                                 final AbstractDocument<?> _document)
    {
        if (_document.getContactOid() == null) {
            final Pos pos = posService.getPos4Workspace(_workspaceOid);
            if (pos != null) {
                _document.setContactOid(pos.getDefaultContactOid());
            }
        }
    }

    private IPos getPos(final Pos _pos)
    {
        final String name = _pos.getName();
        final Currency currency = _pos.getCurrency();
        final ContactDto contactDto = Converter.toDto(contactService.getContact(_pos.getDefaultContactOid()));

        return new IPos()
        {

            @Override
            public String getName()
            {
                return name;
            }

            @Override
            public Currency getCurrency()
            {
                return currency;
            }

            @Override
            public ContactDto getDefaultContact()
            {
                return contactDto;
            }
        };
    }
}
