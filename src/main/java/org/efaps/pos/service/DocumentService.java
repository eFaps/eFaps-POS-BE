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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.dto.ContactDto;
import org.efaps.pos.dto.CreateDocumentDto;
import org.efaps.pos.dto.Currency;
import org.efaps.pos.dto.DocStatus;
import org.efaps.pos.dto.DocType;
import org.efaps.pos.dto.EmployeeRelationType;
import org.efaps.pos.dto.IPosPaymentDto;
import org.efaps.pos.dto.IdentificationType;
import org.efaps.pos.dto.PosCreditNoteDto;
import org.efaps.pos.dto.PosInvoiceDto;
import org.efaps.pos.dto.PosOrderDto;
import org.efaps.pos.dto.PosReceiptDto;
import org.efaps.pos.dto.PosTicketDto;
import org.efaps.pos.dto.ProductRelationType;
import org.efaps.pos.dto.ProductType;
import org.efaps.pos.dto.ValidateForCreditNoteDto;
import org.efaps.pos.dto.ValidateForCreditNoteResponseDto;
import org.efaps.pos.entity.AbstractDocument;
import org.efaps.pos.entity.AbstractDocument.Item;
import org.efaps.pos.entity.AbstractPayableDocument;
import org.efaps.pos.entity.Balance;
import org.efaps.pos.entity.Contact;
import org.efaps.pos.entity.CreditNote;
import org.efaps.pos.entity.Invoice;
import org.efaps.pos.entity.Order;
import org.efaps.pos.entity.Origin;
import org.efaps.pos.entity.Pos;
import org.efaps.pos.entity.Receipt;
import org.efaps.pos.entity.Ticket;
import org.efaps.pos.entity.User;
import org.efaps.pos.error.PreconditionException;
import org.efaps.pos.flags.WorkspaceFlag;
import org.efaps.pos.interfaces.ICreditNoteListener;
import org.efaps.pos.interfaces.IInvoiceListener;
import org.efaps.pos.interfaces.IPos;
import org.efaps.pos.interfaces.IReceiptListener;
import org.efaps.pos.interfaces.ITicketListener;
import org.efaps.pos.listener.IDocumentListener;
import org.efaps.pos.pojo.EmployeeRelation;
import org.efaps.pos.pojo.PaymentRedeemCreditNote;
import org.efaps.pos.projection.PayableHead;
import org.efaps.pos.repository.BalanceRepository;
import org.efaps.pos.repository.CreditNoteRepository;
import org.efaps.pos.repository.InvoiceRepository;
import org.efaps.pos.repository.OrderRepository;
import org.efaps.pos.repository.PromotionInfoRepository;
import org.efaps.pos.repository.ReceiptRepository;
import org.efaps.pos.repository.TicketRepository;
import org.efaps.pos.util.Converter;
import org.efaps.pos.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
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
    private final EFapsClient eFapsClient;
    private final PosService posService;
    private final SequenceService sequenceService;
    private final ContactService contactService;
    private final InventoryService inventoryService;
    private final CalculatorService calculatorService;
    private final ProductService productService;
    private final PromotionService promotionService;
    private final WorkspaceService workspaceService;
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
    private final List<IDocumentListener> documentListeners;

    private final MongoTemplate mongoTemplate;
    private final TaskExecutor taskExecutor;

    @Autowired
    public DocumentService(final MongoTemplate mongoTemplate,
                           final TaskExecutor taskExecutor,
                           final ConfigService configService,
                           final EFapsClient eFapsClient,
                           final PosService posService,
                           final SequenceService sequenceService,
                           final ContactService contactService,
                           final InventoryService inventoryService,
                           final CalculatorService calculatorService,
                           final ProductService productService,
                           final PromotionService promotionService,
                           final WorkspaceService workspaceService,
                           final OrderRepository orderRepository,
                           final ReceiptRepository receiptRepository,
                           final InvoiceRepository invoiceRepository,
                           final TicketRepository ticketRepository,
                           final CreditNoteRepository creditNoteRepository,
                           final BalanceRepository balanceRepository,
                           final PromotionInfoRepository promotionInfoRepository,
                           final Optional<List<IReceiptListener>> receiptListeners,
                           final Optional<List<IInvoiceListener>> invoiceListeners,
                           final Optional<List<ITicketListener>> ticketListeners,
                           final Optional<List<ICreditNoteListener>> creditNoteListener,
                           final Optional<List<IDocumentListener>> documentListeners)
    {
        this.mongoTemplate = mongoTemplate;
        this.taskExecutor = taskExecutor;
        this.configService = configService;
        this.productService = productService;
        this.eFapsClient = eFapsClient;
        this.posService = posService;
        this.sequenceService = sequenceService;
        this.orderRepository = orderRepository;
        this.inventoryService = inventoryService;
        this.calculatorService = calculatorService;
        this.receiptRepository = receiptRepository;
        this.contactService = contactService;
        this.promotionService = promotionService;
        this.workspaceService = workspaceService;
        this.invoiceRepository = invoiceRepository;
        this.ticketRepository = ticketRepository;
        this.creditNoteRepository = creditNoteRepository;
        this.balanceRepository = balanceRepository;
        this.receiptListeners = receiptListeners.isPresent() ? receiptListeners.get() : Collections.emptyList();
        this.invoiceListeners = invoiceListeners.isPresent() ? invoiceListeners.get() : Collections.emptyList();
        this.ticketListeners = ticketListeners.isPresent() ? ticketListeners.get() : Collections.emptyList();
        this.creditNoteListeners = creditNoteListener.isPresent() ? creditNoteListener.get() : Collections.emptyList();
        this.documentListeners = documentListeners.isPresent() ? documentListeners.get() : Collections.emptyList();
    }

    public Order getOrderById(final String id)
    {
        return orderRepository.findById(id).orElse(null);
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

    public Collection<Order> findOrders(final String _term)
    {
        return orderRepository.findByNumberLikeIgnoreCase(_term);
    }

    public Order createOrder(final String workspaceOid,
                             final Order order)
    {
        Order storedOrder;
        if (configService.getInstProperties().getOrder().isSkipCalcOnCreate()) {
            order.setNumber(sequenceService.getNextOrder());
            storedOrder = orderRepository.insert(order);
        } else {
            final var promoInfo = calculatorService.calculate(workspaceOid, order);
            order.setNumber(sequenceService.getNextOrder());
            storedOrder = orderRepository.insert(order);
            promotionService.registerInfo(storedOrder.getId(), promoInfo);
        }
        for (final IDocumentListener listener : documentListeners) {
            listener.afterCreate(storedOrder);
        }
        return storedOrder;
    }

    public Order createOrder(final User user,
                             final String workspaceOid,
                             final CreateDocumentDto createOrderDto)
    {
        final var order = new Order()
                        .setStatus(DocStatus.OPEN)
                        .setDate(LocalDate.now())
                        .setCurrency(createOrderDto.getCurrency())
                        .setNote(createOrderDto.getNote())
                        .setWorkspaceOid(workspaceOid);
        order.setNumber(sequenceService.getNextOrder());
        order.setItems(getItems(createOrderDto));
        assignSeller(user, workspaceOid, order);
        final var promoInfo = calculatorService.calculate(workspaceOid, order);
        final var storedOrder = orderRepository.insert(order);
        promotionService.registerInfo(storedOrder.getId(), promoInfo);
        for (final IDocumentListener listener : documentListeners) {
            listener.afterCreate(storedOrder);
        }
        return storedOrder;
    }

    public Order updateOrder(final String workspaceOid,
                             final String id,
                             final PosOrderDto dto)
    {
        final var order = orderRepository.findById(id).orElseThrow();
        Converter.mapToEntity(dto, order);
        final var promoInfo = calculatorService.calculate(workspaceOid, order);
        promotionService.registerInfo(order.getId(), promoInfo);
        return orderRepository.save(order);
    }

    public Order updateOrder(final User user,
                             final String workspaceOid,
                             final String orderId,
                             final CreateDocumentDto createOrderDto)
    {
        final var order = orderRepository.findById(orderId).orElseThrow();
        order.setItems(getItems(createOrderDto));
        order.setDate(LocalDate.now());
        order.setNote(createOrderDto.getNote());
        assignSeller(user, workspaceOid, order);
        final var promoInfo = calculatorService.calculate(workspaceOid, order);
        promotionService.registerInfo(order.getId(), promoInfo);
        return orderRepository.save(order);
    }

    private ArrayList<Item> getItems(final CreateDocumentDto createOrderDto)
    {
        final var items = new ArrayList<Item>();
        final var counter = new AtomicInteger(1);
        createOrderDto.getItems().forEach(item -> {
            var productOid = item.getProductOid();
            String standInOid = null;
            final var product = productService.getProduct(item.getProductOid());
            if ((product.getType().equals(ProductType.BATCH) || product.getType().equals(ProductType.INDIVIDUAL))
                            && product.getRelations() != null) {
                final var relOpt = product.getRelations().stream()
                                .filter(relation -> ProductRelationType.BATCH.equals(relation.getType()))
                                .findFirst();
                if (relOpt.isPresent()) {
                    productOid = relOpt.get().getProductOid();
                    standInOid = item.getProductOid();
                }
            }
            items.add(new Item().setIndex(counter.getAndIncrement())
                            .setQuantity(item.getQuantity())
                            .setNetUnitPrice(item.getNetUnitPrice())
                            .setProductOid(productOid)
                            .setStandInOid(standInOid));
        });
        return items;
    }

    public Order updateOrderWithContact(final String orderId,
                                        final String contactId)
    {
        final var order = orderRepository.findById(orderId).orElseThrow();
        final var contact = contactService.findContact(contactId);
        if (contact != null) {
            order.setContactOid(contact.getOid() == null ? contact.getId() : contact.getOid());
        }
        return orderRepository.save(order);
    }

    public Order updateOrderWithLoyaltyContact(final String orderId,
                                               final String contactId)
    {
        final var order = orderRepository.findById(orderId).orElseThrow();
        final var contact = contactService.findContact(contactId);
        if (contact != null) {
            order.setLoyaltyContactOid(contact.getOid() == null ? contact.getId() : contact.getOid());
        }
        return orderRepository.save(order);
    }

    public Order updateOrderContacts(final String orderId,
                                     final String contactIdent,
                                     final String loyaltyContactIdent)
    {
        final var order = orderRepository.findById(orderId).orElseThrow();
        final var contact = contactService.findContact(contactIdent);
        if (contact != null) {
            order.setContactOid(contact.getOid() == null ? contact.getId() : contact.getOid());
        }
        if (loyaltyContactIdent == null) {
            order.setLoyaltyContactOid(null);
        } else {
            final var loyaltyContact = contactService.findContact(loyaltyContactIdent);
            order.setLoyaltyContactOid(
                            loyaltyContact.getOid() == null ? loyaltyContact.getId() : loyaltyContact.getOid());
        }
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

    public Receipt createReceipt(final String workspaceOid,
                                 final String orderId,
                                 final Receipt receipt)
        throws PreconditionException
    {
        validateOrder(orderId);
        validateContact(workspaceOid, receipt);
        validateBalance(receipt);
        receipt.setNumber(sequenceService.getNext(workspaceOid, DocType.RECEIPT, null));
        Receipt ret = receiptRepository.insert(receipt);
        verifyPayment(ret);
        try {
            if (!receiptListeners.isEmpty()) {
                PosReceiptDto dto = Converter.toDto(ret);
                for (final IReceiptListener listener : receiptListeners) {
                    dto = (PosReceiptDto) listener.onCreate(getPos(posService.getPos4Workspace(workspaceOid)), dto,
                                    configService.getProperties());
                }
                ret = receiptRepository.save(Converter.mapToEntity(dto, ret));
            }
        } catch (final Exception e) {
            LOG.error("Wow that should not happen", e);
        }
        promotionService.copyPromotionInfo(orderId, ret.getId());
        closeOrder(orderId, ret.getId());
        inventoryService.removeFromInventory(workspaceOid, ret);

        for (final IDocumentListener listener : documentListeners) {
            listener.afterCreate(ret);
        }
        return ret;
    }

    public Invoice createInvoice(final String workspaceOid,
                                 final String orderId,
                                 final Invoice invoice)
        throws PreconditionException
    {
        validateOrder(orderId);
        validateContact(workspaceOid, invoice);
        validateBalance(invoice);
        invoice.setNumber(sequenceService.getNext(workspaceOid, DocType.INVOICE, null));
        Invoice ret = invoiceRepository.insert(invoice);
        verifyPayment(ret);
        try {
            if (!invoiceListeners.isEmpty()) {
                PosInvoiceDto dto = Converter.toDto(ret);
                for (final IInvoiceListener listener : invoiceListeners) {
                    dto = (PosInvoiceDto) listener.onCreate(getPos(posService.getPos4Workspace(workspaceOid)), dto,
                                    configService.getProperties());
                }
                ret = invoiceRepository.save(Converter.mapToEntity(dto, ret));
            }
        } catch (final Exception e) {
            LOG.error("Wow that should not happen", e);
        }
        promotionService.copyPromotionInfo(orderId, ret.getId());
        closeOrder(orderId, ret.getId());
        inventoryService.removeFromInventory(workspaceOid, ret);
        for (final IDocumentListener listener : documentListeners) {
            listener.afterCreate(ret);
        }
        return ret;
    }

    public Ticket createTicket(final String workspaceOid,
                               final String orderId,
                               final Ticket ticket)
        throws PreconditionException
    {
        validateOrder(orderId);
        validateContact(workspaceOid, ticket);
        validateBalance(ticket);
        ticket.setNumber(sequenceService.getNext(workspaceOid, DocType.TICKET, null));
        Ticket ret = ticketRepository.insert(ticket);
        verifyPayment(ret);
        try {
            if (!ticketListeners.isEmpty()) {
                PosTicketDto dto = Converter.toDto(ret);
                for (final ITicketListener listener : ticketListeners) {
                    dto = (PosTicketDto) listener.onCreate(getPos(posService.getPos4Workspace(workspaceOid)), dto,
                                    configService.getProperties());
                }
                ret = ticketRepository.save(Converter.mapToEntity(dto, ret));
            }
        } catch (final Exception e) {
            LOG.error("Wow that should not happen", e);
        }
        promotionService.copyPromotionInfo(orderId, ret.getId());
        closeOrder(orderId, ret.getId());
        inventoryService.removeFromInventory(workspaceOid, ret);
        for (final IDocumentListener listener : documentListeners) {
            listener.afterCreate(ret);
        }
        return ret;
    }

    public CreditNote createCreditNote(final String _workspaceOid,
                                       final CreditNote _creditNote)
    {
        validateContact(_workspaceOid, _creditNote);
        validateBalance(_creditNote);
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
        for (final IDocumentListener listener : documentListeners) {
            listener.afterCreate(ret);
        }
        return ret;
    }

    private void validateBalance(final AbstractPayableDocument<?> entity)
    {
        if (entity.getBalanceOid() != null && !Utils.isOid(entity.getBalanceOid())) {
            final var opt = balanceRepository.findById(entity.getBalanceOid());
            if (opt.isPresent() && Utils.isOid(opt.get().getOid())) {
                entity.setBalanceOid(opt.get().getOid());
            }
        }
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

    public Receipt getReceiptById(final String _documentId)
    {
        return receiptRepository.findById(_documentId).orElse(null);
    }

    public Optional<Receipt> findReceipt(final String identifier,
                                         final Boolean remote)
    {
        Optional<Receipt> opt;
        if (ObjectId.isValid(identifier)) {
            opt = receiptRepository.findById(identifier);
        } else {
            opt = receiptRepository.findByOid(identifier);
        }

        if (opt.isEmpty() && remote && Utils.isOid(identifier)) {
            final var receiptDto = eFapsClient.getReceipt(identifier);
            if (receiptDto != null) {
                final var entity = Converter.toEntity(receiptDto);
                entity.setOrigin(Origin.REMOTE);
                final var persisted = receiptRepository.save(entity);
                opt = Optional.of(persisted);
            }
        }
        return opt;
    }

    public List<Receipt> retrieveReceipts(final String number)
    {
        List<Receipt> receipts = receiptRepository.findByNumber(number);
        if (receipts.isEmpty()) {
            final var retrieved = eFapsClient.retrieveReceipts(number);
            if (retrieved != null) {
                receipts = retrieved.stream().map(Converter::toEntity).toList();
            }
        }
        return receipts;
    }

    public Optional<CreditNote> findCreditNote(final String identifier)
    {

        Optional<CreditNote> opt;
        if (ObjectId.isValid(identifier)) {
            opt = creditNoteRepository.findById(identifier);
        } else {
            opt = creditNoteRepository.findByOid(identifier);
        }

        return opt;
    }

    public Collection<Receipt> getReceipts4Balance(final String _key)
    {
        return receiptRepository.findByBalanceOid(evalBalanceOid(_key));
    }

    public Collection<PayableHead> getReceiptHeads4Balance(final String _balanceKey)
    {
        return getPayableHeads4Balance(_balanceKey, "receipts");
    }

    public Invoice getInvoiceById(final String _documentId)
    {
        return invoiceRepository.findById(_documentId).orElse(null);
    }

    public Optional<Invoice> findInvoice(final String identifier,
                                         final Boolean remote)
    {
        Optional<Invoice> opt;
        if (ObjectId.isValid(identifier)) {
            opt = invoiceRepository.findById(identifier);
        } else {
            opt = invoiceRepository.findByOid(identifier);
        }

        if (opt.isEmpty() && remote && Utils.isOid(identifier)) {

        }
        return opt;
    }

    public Collection<PayableHead> getInvoiceHeads4Balance(final String _balanceKey)
    {
        return getPayableHeads4Balance(_balanceKey, "invoices");
    }

    public Collection<Invoice> getInvoices4Balance(final String _key)
    {
        return invoiceRepository.findByBalanceOid(evalBalanceOid(_key));
    }

    public List<Invoice> retrieveInvoices(final String number)
    {
        List<Invoice> invoices = invoiceRepository.findByNumber(number);
        if (invoices.isEmpty()) {
            final var retrieved = eFapsClient.retrieveInvoices(number);
            if (retrieved != null) {
                invoices = retrieved.stream().map(Converter::toEntity).toList();
            }
        }
        return invoices;
    }

    public Ticket getTicketById(final String _documentId)
    {
        return ticketRepository.findById(_documentId).orElse(null);
    }

    public Collection<Ticket> getTickets4Balance(final String _key)
    {
        return ticketRepository.findByBalanceOid(evalBalanceOid(_key));
    }

    public List<Ticket> retrieveTickets(final String number)
    {
        List<Ticket> tickets = ticketRepository.findByNumber(number);
        if (tickets.isEmpty()) {
            final var retrieved = eFapsClient.retrieveTickets(number);
            if (retrieved != null) {
                tickets = retrieved.stream().map(Converter::toEntity).toList();
            }
        }
        return tickets;
    }

    public CreditNote getCreditNoteById(final String documentId)
    {
        return creditNoteRepository.findById(documentId).orElse(null);
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

    public AbstractPayableDocument<?> getPayableById(final String documentId)
    {
        AbstractPayableDocument<?> ret = getReceiptById(documentId);
        if (ret == null) {
            ret = getInvoiceById(documentId);
        }
        if (ret == null) {
            ret = getTicketById(documentId);
        }
        if (ret == null) {
            ret = getCreditNoteById(documentId);
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

    public void findInvoicesRemote(String number)
    {
        // TODO Auto-generated method stub

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

    public List<CreditNote> retrieveCreditNotes(final String number)
    {
        List<CreditNote> creditNotes = creditNoteRepository.findByNumber(number);
        if (creditNotes.isEmpty()) {
            final var retrieved = eFapsClient.retrieveCreditNotes(number);
            if (retrieved != null) {
                creditNotes = retrieved.stream().map(Converter::toEntity).toList();
            }
        }
        return creditNotes;
    }

    public void retrigger4Receipt(final String identifier)
    {
        LOG.warn("Retriggering for Receipt: {}", identifier);
        final var entity = findReceipt(identifier, false);
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
        final var entity = findInvoice(identifier, false);
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

    public AbstractPayableDocument<?> payAndEmit(final String balanceId,
                                                 final String orderId,
                                                 final List<IPosPaymentDto> paymentDtos)
        throws PreconditionException
    {
        final var order = getOrderById(orderId);
        final var contact = contactService.findContact(order.getContactOid());
        final AbstractPayableDocument<? extends AbstractPayableDocument<?>> payable;
        final var payments = paymentDtos.stream().map(Converter::toEntity).toList();
        if (IdentificationType.RUC.equals(contact.getIdType())) {
            final var invoice = new Invoice();
            Converter.clone(order, invoice);
            invoice.setPayments(payments);
            invoice.setBalanceOid(balanceId);
            invoice.setStatus(DocStatus.CLOSED);
            payable = createInvoice(order.getWorkspaceOid(), orderId, invoice);
        } else {
            final var receipt = new Receipt();
            Converter.clone(order, receipt);
            receipt.setPayments(payments);
            receipt.setBalanceOid(balanceId);
            receipt.setStatus(DocStatus.CLOSED);
            payable = createReceipt(order.getWorkspaceOid(), orderId, receipt);
        }
        return payable;
    }

    protected void assignSeller(final User user,
                                final String workspaceOid,
                                final Order order)
    {
        final var workspace = workspaceService.getWorkspace(workspaceOid);
        if (workspace != null && user.getEmployeeOid() != null
                        && Utils.hasFlag(workspace.getFlags(), WorkspaceFlag.ASSIGNSELLER)) {
            if (order.getEmployeeRelations() == null) {
                order.setEmployeeRelations(new HashSet<>(Arrays.asList(new EmployeeRelation()
                                .setEmployeeOid(user.getEmployeeOid()).setType(EmployeeRelationType.SELLER))));
            } else if (order.getEmployeeRelations().stream()
                            .noneMatch(relation -> relation.getType().equals(EmployeeRelationType.SELLER))) {
                order.getEmployeeRelations().add(new EmployeeRelation()
                                .setEmployeeOid(user.getEmployeeOid()).setType(EmployeeRelationType.SELLER));
            }
        }
    }

    public void updateContactOids(final List<Contact> syncedContacts)
    {
        for (final var contact : syncedContacts) {
            // contact
            receiptRepository.findByContactOid(contact.getId()).stream().forEach(doc -> {
                doc.setContactOid(contact.getOid());
                receiptRepository.save(doc);
            });
            invoiceRepository.findByContactOid(contact.getId()).stream().forEach(doc -> {
                doc.setContactOid(contact.getOid());
                invoiceRepository.save(doc);
            });
            ticketRepository.findByContactOid(contact.getId()).stream().forEach(doc -> {
                doc.setContactOid(contact.getOid());
                ticketRepository.save(doc);
            });
            orderRepository.findByContactOid(contact.getId()).stream().forEach(doc -> {
                doc.setContactOid(contact.getOid());
                orderRepository.save(doc);
            });

            // loyalty contact
            receiptRepository.findByLoyaltyContactOid(contact.getId()).stream().forEach(doc -> {
                doc.setContactOid(contact.getOid());
                receiptRepository.save(doc);
            });
            invoiceRepository.findByLoyaltyContactOid(contact.getId()).stream().forEach(doc -> {
                doc.setContactOid(contact.getOid());
                invoiceRepository.save(doc);
            });
            ticketRepository.findByLoyaltyContactOid(contact.getId()).stream().forEach(doc -> {
                doc.setContactOid(contact.getOid());
                ticketRepository.save(doc);
            });
            orderRepository.findByLoyaltyContactOid(contact.getId()).stream().forEach(doc -> {
                doc.setContactOid(contact.getOid());
                orderRepository.save(doc);
            });
        }
    }

    public ValidateForCreditNoteResponseDto validateForCreditNote(final ValidateForCreditNoteDto dto)
    {
        dto.getPayableOid();
        return ValidateForCreditNoteResponseDto.builder().withValid(true).build();
    }

    public void verifyPayment(final AbstractPayableDocument<?> payable)
    {
        if (payable.getPayments() != null) {
            for (final var payment : payable.getPayments()) {
                if (payment instanceof final PaymentRedeemCreditNote redeemPayment) {
                    // check if the RedeemDocOid is a oid. If oid it was already
                    // synced or is from remote
                    // if mongoId the id must be updated to the oid
                    if (Utils.isOid(redeemPayment.getRedeemDocOid())) {
                        validateRedeemDocument(redeemPayment.getRedeemDocOid(), payable.getId());
                    } else {
                        final var creditNote = creditNoteRepository.findById(redeemPayment.getRedeemDocOid()).get();
                        creditNote.setRedeemedById(payable.getId());
                        creditNoteRepository.save(creditNote);
                    }
                }
            }
        }
    }

    private void validateRedeemDocument(final String oid,
                                        final String payableId)
    {
        final var creditNoteOpt = creditNoteRepository.findByOid(oid);
        if (creditNoteOpt.isPresent()) {
            final var creditNote = creditNoteOpt.get();
            creditNote.setRedeemedById(payableId);
            creditNoteRepository.save(creditNote);
        } else {
            taskExecutor.execute(() -> {
                final var creditNoteDto = eFapsClient.getCreditNote(oid);
                if (creditNoteDto != null) {
                    final var entity = Converter.toEntity(creditNoteDto);
                    entity.setOrigin(Origin.REMOTE);
                    entity.setRedeemedById(payableId);
                    creditNoteRepository.save(entity);
                }
            });
        }
    }

    public void updateBalanceOid(Balance balance)
    {
        final Collection<Receipt> reciepts = receiptRepository.findByBalanceOid(balance.getId());
        reciepts.forEach(doc -> {
            doc.setBalanceOid(balance.getOid());
            receiptRepository.save(doc);
        });
        final Collection<Invoice> invoices = invoiceRepository.findByBalanceOid(balance.getId());
        invoices.forEach(doc -> {
            doc.setBalanceOid(balance.getOid());
            invoiceRepository.save(doc);
        });
        final Collection<Ticket> tickets = ticketRepository.findByBalanceOid(balance.getId());
        tickets.forEach(doc -> {
            doc.setBalanceOid(balance.getOid());
            ticketRepository.save(doc);
        });
    }
}
