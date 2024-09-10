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

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.efaps.pos.ConfigProperties;
import org.efaps.pos.ConfigProperties.Company;
import org.efaps.pos.client.Checkout;
import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.context.Context;
import org.efaps.pos.dto.BalanceDto;
import org.efaps.pos.dto.BalanceStatus;
import org.efaps.pos.dto.DocStatus;
import org.efaps.pos.dto.InvoiceDto;
import org.efaps.pos.dto.OrderDto;
import org.efaps.pos.dto.ReceiptDto;
import org.efaps.pos.dto.TicketDto;
import org.efaps.pos.entity.AbstractDocument;
import org.efaps.pos.entity.AbstractPayableDocument;
import org.efaps.pos.entity.Balance;
import org.efaps.pos.entity.Category;
import org.efaps.pos.entity.Config;
import org.efaps.pos.entity.Contact;
import org.efaps.pos.entity.CreditNote;
import org.efaps.pos.entity.Employee;
import org.efaps.pos.entity.InventoryEntry;
import org.efaps.pos.entity.Invoice;
import org.efaps.pos.entity.Order;
import org.efaps.pos.entity.Pos;
import org.efaps.pos.entity.Printer;
import org.efaps.pos.entity.Product;
import org.efaps.pos.entity.Receipt;
import org.efaps.pos.entity.Sequence;
import org.efaps.pos.entity.SyncInfo;
import org.efaps.pos.entity.Ticket;
import org.efaps.pos.entity.User;
import org.efaps.pos.entity.Warehouse;
import org.efaps.pos.entity.Workspace;
import org.efaps.pos.entity.Workspace.Floor;
import org.efaps.pos.entity.Workspace.PrintCmd;
import org.efaps.pos.pojo.StashId;
import org.efaps.pos.repository.BalanceRepository;
import org.efaps.pos.repository.CategoryRepository;
import org.efaps.pos.repository.CreditNoteRepository;
import org.efaps.pos.repository.InventoryRepository;
import org.efaps.pos.repository.InvoiceRepository;
import org.efaps.pos.repository.OrderRepository;
import org.efaps.pos.repository.PrinterRepository;
import org.efaps.pos.repository.ProductRepository;
import org.efaps.pos.repository.ReceiptRepository;
import org.efaps.pos.repository.SequenceRepository;
import org.efaps.pos.repository.TicketRepository;
import org.efaps.pos.repository.UserRepository;
import org.efaps.pos.repository.WarehouseRepository;
import org.efaps.pos.repository.WorkspaceRepository;
import org.efaps.pos.util.Converter;
import org.efaps.pos.util.SyncServiceDeactivatedException;
import org.efaps.pos.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@Service
public class SyncService
{

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(SyncService.class);

    private final MongoTemplate mongoTemplate;
    private final GridFsTemplate gridFsTemplate;
    private final EFapsClient eFapsClient;
    private final ReceiptRepository receiptRepository;
    private final InvoiceRepository invoiceRepository;
    private final TicketRepository ticketRepository;
    private final CreditNoteRepository creditNoteRepository;
    private final ProductRepository productRepository;
    private final SequenceRepository sequenceRepository;
    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryRepository inventoryRepository;
    private final PrinterRepository printerRepository;
    private final WorkspaceRepository workspaceRepository;
    private final BalanceRepository balanceRepository;
    private final OrderRepository orderRepository;
    private final CategoryRepository categoryRepository;
    private final ConfigProperties configProperties;
    private final ContactService contactService;
    private final DocumentService documentService;
    private final ExchangeRateService exchangeRateService;
    private final LogService logService;
    private final PromotionService promotionService;
    private final ProductService productService;
    private boolean deactivated;

    @Autowired
    public SyncService(final MongoTemplate _mongoTemplate,
                       final GridFsTemplate _gridFsTemplate,
                       final ReceiptRepository _receiptRepository,
                       final InvoiceRepository _invoiceRepository,
                       final TicketRepository _ticketRepository,
                       final CreditNoteRepository _creditNoteRepository,
                       final ProductRepository _productRepository,
                       final SequenceRepository _sequenceRepository,
                       final UserRepository _userRepository,
                       final WarehouseRepository _warehouseRepository,
                       final InventoryRepository _inventoryRepository,
                       final PrinterRepository _printerRepository,
                       final WorkspaceRepository _workspaceRepository,
                       final BalanceRepository _balanceRepository,
                       final OrderRepository _orderRepository,
                       final CategoryRepository _categoryRepository,
                       final EFapsClient _eFapsClient,
                       final ConfigProperties _configProperties,
                       final ContactService contactService,
                       final DocumentService documentService,
                       final ExchangeRateService _exchangeRateService,
                       final LogService logService,
                       final PromotionService promotionService,
                       final ProductService productService)
    {
        mongoTemplate = _mongoTemplate;
        gridFsTemplate = _gridFsTemplate;
        receiptRepository = _receiptRepository;
        invoiceRepository = _invoiceRepository;
        ticketRepository = _ticketRepository;
        creditNoteRepository = _creditNoteRepository;
        productRepository = _productRepository;
        sequenceRepository = _sequenceRepository;
        userRepository = _userRepository;
        warehouseRepository = _warehouseRepository;
        inventoryRepository = _inventoryRepository;
        printerRepository = _printerRepository;
        workspaceRepository = _workspaceRepository;
        balanceRepository = _balanceRepository;
        orderRepository = _orderRepository;
        categoryRepository = _categoryRepository;
        eFapsClient = _eFapsClient;
        configProperties = _configProperties;
        this.contactService = contactService;
        this.documentService = documentService;
        exchangeRateService = _exchangeRateService;
        this.logService = logService;
        this.promotionService = promotionService;
        this.productService = productService;
    }

    public void runSyncJob(final String _methodName)
        throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
        InvocationTargetException
    {
        final Method method = getClass().getMethod(_methodName);
        if (configProperties.getCompanies().size() > 0) {
            for (final Company company : configProperties.getCompanies()) {
                Context.get().setCompany(company);
                MDC.put("company", company.getTenant());
                method.invoke(this);
            }
        } else {
            method.invoke(this);
        }
    }

    public void syncPromotionInfos()
    {
        if (promotionService.syncPromotionInfos()) {
            registerSync(StashId.PROMOTIONINFOSYNC);
        }
    }

    public void syncPromotions()
    {
        if (promotionService.syncPromotions()) {
            registerSync(StashId.PROMOTIONSYNC);
        }
    }

    public void syncLogs()
    {
        LOG.info("Syncing Logs");
        logService.syncLogs();
    }

    public void syncProperties()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        LOG.info("Syncing Properties");
        final Map<String, String> properties = eFapsClient.getProperties();
        if (!properties.isEmpty()) {
            final Config config = new Config().setId(Config.KEY).setProperties(properties);
            mongoTemplate.save(config);
        }
    }

    public void syncAllProducts()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        productService.syncAllProducts();
        registerSync(StashId.PRODUCTSYNC);
    }

    public void syncProducts()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        if (productService.syncProducts(getSync(StashId.PRODUCTSYNC))) {
            registerSync(StashId.PRODUCTSYNC);
        }
    }

    public void syncCategories()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        LOG.info("Syncing Categories");
        final List<Category> categories = eFapsClient.getCategories().stream()
                        .map(Converter::toEntity)
                        .collect(Collectors.toList());
        if (!categories.isEmpty()) {
            final List<Category> existingCategories = mongoTemplate.findAll(Category.class);
            existingCategories.forEach(existing -> {
                if (!categories.stream()
                                .filter(category -> category.getOid().equals(existing.getOid())).findFirst()
                                .isPresent()) {
                    mongoTemplate.remove(existing);
                }
            });
            categories.forEach(category -> mongoTemplate.save(category));
        }
        registerSync(StashId.CATEGORYSYNC);
    }

    public void syncWorkspaces()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }

        LOG.info("Syncing Workspaces");
        final List<Workspace> workspaces = eFapsClient.getWorkspaces().stream()
                        .map(Converter::toEntity)
                        .collect(Collectors.toList());
        if (!workspaces.isEmpty()) {
            final List<Workspace> existingWorkspaces = workspaceRepository.findAll();
            existingWorkspaces.forEach(existing -> {
                if (!workspaces.stream()
                                .filter(workspace -> workspace.getOid().equals(existing.getOid())).findFirst()
                                .isPresent()) {
                    workspaceRepository.delete(existing);
                }
            });
            workspaces.forEach(workspace -> workspaceRepository.save(workspace));
        }
        registerSync(StashId.WORKSPACESYNC);
    }

    public void syncWarehouses()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        LOG.info("Syncing Warehouses");
        final List<Warehouse> warehouses = eFapsClient.getWarehouses().stream()
                        .map(Converter::toEntity)
                        .collect(Collectors.toList());
        if (!warehouses.isEmpty()) {
            warehouseRepository.deleteAll();
            warehouses.forEach(workspace -> warehouseRepository.save(workspace));
        }
        registerSync(StashId.WAREHOUSESYNC);
    }

    public void syncExchangeRates()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        LOG.info("Syncing ExchangeRates");
        exchangeRateService.loadExchangeRate();
        registerSync(StashId.EXCHANGERATESSYNC);
    }

    public void syncInventory()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        LOG.info("Syncing Inventory");
        final List<InventoryEntry> entries = eFapsClient.getInventory().stream()
                        .map(Converter::toEntity)
                        .collect(Collectors.toList());
        if (!entries.isEmpty()) {
            inventoryRepository.deleteAll();
            entries.forEach(workspace -> inventoryRepository.save(workspace));
        }
        registerSync(StashId.WAREHOUSESYNC);
    }

    public void syncPrinters()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        LOG.info("Syncing Printers");
        final List<Printer> printers = eFapsClient.getPrinters().stream()
                        .map(Converter::toEntity)
                        .collect(Collectors.toList());
        if (!printers.isEmpty()) {
            printerRepository.deleteAll();
            printers.forEach(printer -> printerRepository.save(printer));
        }
        registerSync(StashId.PRINTERSYNC);
    }

    public void syncPOSs()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        LOG.info("Syncing POSs");
        final List<Pos> poss = eFapsClient.getPOSs().stream()
                        .map(Converter::toEntity)
                        .collect(Collectors.toList());
        if (!poss.isEmpty()) {
            final List<Pos> existingPoss = mongoTemplate.findAll(Pos.class);
            existingPoss.forEach(existing -> {
                if (!poss.stream()
                                .filter(pos -> pos.getOid().equals(existing.getOid())).findFirst()
                                .isPresent()) {
                    mongoTemplate.remove(existing);
                }
            });
            poss.forEach(pos -> mongoTemplate.save(pos));
        }
        registerSync(StashId.POSSYNC);
    }

    public void syncUsers()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }

        LOG.info("Syncing Users");
        final List<User> users = eFapsClient.getUsers().stream()
                        .map(Converter::toEntity)
                        .collect(Collectors.toList());
        if (!users.isEmpty()) {
            userRepository.deleteAll();
            users.forEach(user -> userRepository.save(user));
        }
        registerSync(StashId.USERSYNC);
    }

    public void syncPayables()
        throws SyncServiceDeactivatedException
    {
        final var syncedContacts = contactService.syncContactsUp();
        documentService.updateContactOid(syncedContacts);
        syncBalance();
        syncReceipts();
        syncInvoices();
        syncTickets();
        syncOrders();
        syncCreditNotes();
    }

    public void syncBalance()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        LOG.info("Syncing Balance");
        final Collection<BalanceDto> tosync = balanceRepository.findByOidIsNull().stream()
                        .map(Converter::toBalanceDto)
                        .collect(Collectors.toList());
        for (final BalanceDto dto : tosync) {
            LOG.debug("Syncing Balance: {}", dto);
            final BalanceDto recDto = eFapsClient.postBalance(dto);
            LOG.debug("received Balance: {}", recDto);
            if (recDto.getOid() != null) {
                final Optional<Balance> balanceOpt = balanceRepository.findById(recDto.getId());
                if (balanceOpt.isPresent()) {
                    final Balance balance = balanceOpt.get();
                    balance.setOid(recDto.getOid());
                    balanceRepository.save(balance);
                    final Collection<Receipt> reciepts = receiptRepository.findByBalanceOid(balance.getId());
                    reciepts.forEach(_doc -> {
                        _doc.setBalanceOid(balance.getOid());
                        receiptRepository.save(_doc);
                    });
                    final Collection<Invoice> invoices = invoiceRepository.findByBalanceOid(balance.getId());
                    invoices.forEach(_doc -> {
                        _doc.setBalanceOid(balance.getOid());
                        invoiceRepository.save(_doc);
                    });
                    final Collection<Ticket> tickets = ticketRepository.findByBalanceOid(balance.getId());
                    tickets.forEach(_doc -> {
                        _doc.setBalanceOid(balance.getOid());
                        ticketRepository.save(_doc);
                    });
                }
            }
        }
        final Collection<BalanceDto> tosync2 = balanceRepository.findBySyncedIsFalseAndStatus(BalanceStatus.CLOSED)
                        .stream()
                        .map(Converter::toBalanceDto)
                        .collect(Collectors.toList());
        for (final BalanceDto dto : tosync2) {
            if (eFapsClient.putBalance(dto)) {
                final Optional<Balance> balanceOpt = balanceRepository.findById(dto.getId());
                if (balanceOpt.isPresent()) {
                    final Balance balance = balanceOpt.get();
                    balance.setSynced(true);
                    balanceRepository.save(balance);
                }
            }
        }
        registerSync(StashId.BALANCESYNC);
    }

    public void syncOrders()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        LOG.info("Syncing Canceled Orders");
        final Collection<Order> tosync = orderRepository.findByOidIsNullAndStatus(DocStatus.CANCELED);
        for (final Order order : tosync) {
            if (order.getContactOid() == null || validateContact(order)) {
                LOG.debug("Syncing Order: {}", order);
                final OrderDto recDto = eFapsClient.postOrder(Converter.toOrderDto(order));
                LOG.debug("received Order: {}", recDto);
                if (recDto.getOid() != null) {
                    final Optional<Order> orderOpt = orderRepository.findById(recDto.getId());
                    if (orderOpt.isPresent()) {
                        final Order retOrder = orderOpt.get();
                        retOrder.setOid(recDto.getOid());
                        orderRepository.save(retOrder);
                    }
                }
            }
        }
        LOG.info("Syncing Closed Orders");
        final Collection<Order> tosync2 = orderRepository.findByOidIsNullAndStatus(DocStatus.CLOSED);
        for (final Order order : tosync2) {
            if (order.getContactOid() == null || validateContact(order)) {
                boolean sync = true;
                if (order.getPayableOid() != null && !Utils.isOid(order.getPayableOid())) {
                    final AbstractPayableDocument<?> payable = documentService.getPayable(order.getPayableOid());
                    if (payable != null && Utils.isOid(payable.getOid())) {
                        order.setPayableOid(payable.getOid());
                        orderRepository.save(order);
                    } else {
                        sync = false;
                    }
                }
                if (sync) {
                    LOG.debug("Syncing Order: {}", order);
                    final OrderDto recDto = eFapsClient.postOrder(Converter.toOrderDto(order));
                    LOG.debug("received Order: {}", recDto);
                    if (recDto.getOid() != null) {
                        final Optional<Order> orderOpt = orderRepository.findById(recDto.getId());
                        if (orderOpt.isPresent()) {
                            final Order retOrder = orderOpt.get();
                            retOrder.setOid(recDto.getOid());
                            orderRepository.save(retOrder);
                        }
                    }
                } else {
                    LOG.info("skipped Order: {}", order);
                }
            }
        }
    }

    public void syncReceipts()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        LOG.info("Syncing Receipts");
        final Collection<Receipt> tosync = receiptRepository.findByOidIsNull();
        for (final Receipt receipt : tosync) {
            if (validateContact(receipt) && verifyBalance(receipt)) {
                LOG.debug("Syncing Receipt: {}", receipt);
                final ReceiptDto recDto = eFapsClient.postReceipt(Converter.toReceiptDto(receipt));
                LOG.debug("received Receipt: {}", recDto);
                if (recDto.getOid() != null) {
                    final Optional<Receipt> receiptOpt = receiptRepository.findById(recDto.getId());
                    if (receiptOpt.isPresent()) {
                        final Receipt retReceipt = receiptOpt.get();
                        retReceipt.setOid(recDto.getOid());
                        retReceipt.setStatus(DocStatus.CLOSED);
                        receiptRepository.save(retReceipt);
                    }
                }
            } else {
                LOG.debug("skipped Receipt: {}", receipt);
            }
        }
        registerSync(StashId.RECEIPTSYNC);
    }

    private boolean verifyBalance(final AbstractPayableDocument<?> _document)
    {
        boolean ret = true;
        if (!Utils.isOid(_document.getBalanceOid())) {
            ret = false;
            final Optional<Balance> balanceOpt = balanceRepository.findById(_document.getBalanceOid());
            if (balanceOpt.isPresent()) {
                final Balance balance = balanceOpt.get();
                if (Utils.isOid(balance.getOid())) {
                    _document.setBalanceOid(balance.getOid());
                    ret = true;
                    if (_document instanceof Receipt) {
                        receiptRepository.save((Receipt) _document);
                    } else if (_document instanceof Invoice) {
                        invoiceRepository.save((Invoice) _document);
                    } else if (_document instanceof Ticket) {
                        ticketRepository.save((Ticket) _document);
                    }
                } else {
                    LOG.error("The found Balance does no thave an OID {}", balance);
                }
            }
        }
        return ret;
    }

    public void syncInvoices()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        LOG.info("Syncing Invoices");
        final Collection<Invoice> tosync = invoiceRepository.findByOidIsNull();
        for (final Invoice invoice : tosync) {
            LOG.debug("Syncing Invoice: {}", invoice);
            if (validateContact(invoice) && verifyBalance(invoice)) {
                final InvoiceDto recDto = eFapsClient.postInvoice(Converter.toInvoiceDto(invoice));
                LOG.debug("received Invoice: {}", recDto);
                if (recDto.getOid() != null) {
                    final Optional<Invoice> opt = invoiceRepository.findById(recDto.getId());
                    if (opt.isPresent()) {
                        final Invoice receipt = opt.get();
                        receipt.setOid(recDto.getOid());
                        receipt.setStatus(DocStatus.CLOSED);
                        invoiceRepository.save(receipt);
                    }
                }
            } else {
                LOG.debug("skipped Invoice: {}", invoice);
            }
        }
        registerSync(StashId.INVOICESYNC);
    }

    public void syncTickets()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        LOG.info("Syncing Tickets");
        final Collection<Ticket> tosync = ticketRepository.findByOidIsNull();
        for (final Ticket dto : tosync) {
            LOG.debug("Syncing Ticket: {}", dto);
            if (validateContact(dto) && verifyBalance(dto)) {
                final TicketDto recDto = eFapsClient.postTicket(Converter.toTicketDto(dto));
                LOG.debug("received Ticket: {}", recDto);
                if (recDto.getOid() != null) {
                    final Optional<Ticket> opt = ticketRepository.findById(recDto.getId());
                    if (opt.isPresent()) {
                        final Ticket receipt = opt.get();
                        receipt.setOid(recDto.getOid());
                        receipt.setStatus(DocStatus.CLOSED);
                        ticketRepository.save(receipt);
                    }
                }
            } else {
                LOG.debug("skipped Ticket: {}", dto);
            }
        }
        registerSync(StashId.TICKETSYNC);
    }

    public void syncCreditNotes()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        LOG.info("Syncing CreditNotes");
        final Collection<CreditNote> tosync = creditNoteRepository.findByOidIsNull();
        for (final CreditNote creditNote : tosync) {
            LOG.debug("Syncing CreditNote: {}", creditNote);
            if (validateContact(creditNote) && verifyBalance(creditNote) && verifySourceDoc(creditNote)) {
                final var recDto = eFapsClient.postCreditNote(Converter.toCreditNoteDto(creditNote));
                LOG.debug("received CreditNote: {}", recDto);
                if (recDto.getOid() != null) {
                    final Optional<CreditNote> opt = creditNoteRepository.findById(recDto.getId());
                    if (opt.isPresent()) {
                        final CreditNote receipt = opt.get();
                        receipt.setOid(recDto.getOid());
                        receipt.setStatus(DocStatus.CLOSED);
                        creditNoteRepository.save(receipt);
                    }
                }
            } else {
                LOG.debug("skipped CreditNote: {}", creditNote);
            }
        }
        registerSync(StashId.INVOICESYNC);
    }

    private boolean verifySourceDoc(final CreditNote creditNote)
    {
        boolean ret = false;
        if (Utils.isOid(creditNote.getSourceDocOid())) {
            ret = true;
        } else {
            final var payable = documentService.getPayable(creditNote.getSourceDocOid());
            if (payable != null && Utils.isOid(payable.getOid())) {
                creditNote.setSourceDocOid(payable.getOid());
                creditNoteRepository.save(creditNote);
                ret = true;
            }
        }
        return ret;
    }

    private boolean validateContact(final AbstractDocument<?> entity)
    {
        boolean ret = false;
        if (Utils.isOid(entity.getContactOid())) {
            ret = contactService.findOneByOid(entity.getContactOid()).isPresent();
        } else if (entity.getContactOid() != null) {
            final Optional<Contact> optContact = contactService.findById(entity.getContactOid());
            if (optContact.isPresent()) {
                final String contactOid = optContact.get().getOid();
                if (Utils.isOid(contactOid)) {
                    entity.setContactOid(contactOid);
                    mongoTemplate.save(entity);
                    ret = true;
                }
            }
        }
        return ret;
    }

    public void syncImages()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        LOG.info("Syncing Images");
        final List<Product> products = productRepository.findAll();
        for (final Product product : products) {
            if (product.getImageOid() != null) {
                LOG.debug("Syncing Product-Image {}", product.getImageOid());
                storeImage(product.getImageOid());
            }
            for (final var indicationSet : product.getIndicationSets()) {
                if (indicationSet.getImageOid() != null) {
                    LOG.debug("Syncing IndicationSet-Image {}", indicationSet.getImageOid());
                    storeImage(indicationSet.getImageOid());
                }
                for (final var indication : indicationSet.getIndications()) {
                    if (indication.getImageOid() != null) {
                        LOG.debug("Syncing Indication-Image {}", indication.getImageOid());
                        storeImage(indication.getImageOid());
                    }
                }
            }
        }

        final List<Workspace> workspaces = workspaceRepository.findAll();
        for (final Workspace workspace : workspaces) {
            for (final Floor floor : workspace.getFloors()) {
                LOG.debug("Syncing Floor-Image {}", floor.getImageOid());
                storeImage(floor.getImageOid());
            }
        }

        final var categories = categoryRepository.findAll();
        for (final var category : categories) {
            if (StringUtils.isNotEmpty(category.getImageOid())) {
                LOG.debug("Syncing Category-Image {}", category.getImageOid());
                storeImage(category.getImageOid());
            }
        }
        registerSync(StashId.IMAGESYNC);
    }

    protected void storeImage(final String imageOid)
    {
        final Checkout checkout = eFapsClient.checkout(imageOid);
        if (checkout != null) {
            gridFsTemplate.delete(new Query(Criteria.where("metadata.oid").is(imageOid)));
            final DBObject metaData = new BasicDBObject();
            metaData.put("oid", imageOid);
            metaData.put("contentType", checkout.getContentType().toString());
            gridFsTemplate.store(new ByteArrayInputStream(checkout.getContent()), checkout.getFilename(),
                            metaData);
        }
    }

    public void syncReports()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        LOG.info("Syncing Reports");
        final List<Workspace> workspaces = workspaceRepository.findAll();
        final Set<String> reportOids = workspaces.stream()
                        .map(Workspace::getPrintCmds)
                        .flatMap(Set::stream)
                        .map(PrintCmd::getReportOid)
                        .collect(Collectors.toSet());

        for (final String reportOid : reportOids) {
            LOG.debug("Syncing Report {}", reportOid);
            final Checkout checkout = eFapsClient.checkout(reportOid);
            if (checkout != null) {
                gridFsTemplate.delete(new Query(Criteria.where("metadata.oid").is(reportOid)));
                final DBObject metaData = new BasicDBObject();
                metaData.put("oid", reportOid);
                metaData.put("contentType", checkout.getContentType().toString());
                gridFsTemplate.store(new ByteArrayInputStream(checkout.getContent()), checkout.getFilename(),
                                metaData);
            }
        }
        registerSync(StashId.REPORTSYNC);
    }

    public void syncSequences()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        LOG.info("Syncing Sequences");
        final List<Sequence> sequences = eFapsClient.getSequences().stream()
                        .map(Converter::toEntity)
                        .collect(Collectors.toList());
        for (final Sequence sequence : sequences) {
            LOG.debug("Syncing Sequence: {}", sequence);
            final Optional<Sequence> seqOpt = sequenceRepository.findByOid(sequence.getOid());
            if (seqOpt.isPresent()) {
                final Sequence es = seqOpt.get();
                if (es.getSeq() < sequence.getSeq()) {
                    es.setSeq(sequence.getSeq());
                    sequenceRepository.save(es);
                }
                if (!es.getFormat().equals(sequence.getFormat())) {
                    es.setFormat(sequence.getFormat());
                    sequenceRepository.save(es);
                }
            } else {
                sequenceRepository.save(sequence);
            }
        }
        registerSync(StashId.SEQUENCESYNC);
    }

    public void syncAllContacts()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        LOG.info("Syncing All Contacts");
        final var syncedContacts = contactService.syncContactsUp();
        documentService.updateContactOid(syncedContacts);
        contactService.syncContacts(null);
    }

    public void syncContacts()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        LOG.info("Syncing Contacts");
        final var syncedContacts = contactService.syncContactsUp();
        documentService.updateContactOid(syncedContacts);
        if (contactService.syncContacts(getSync(StashId.CONTACTSYNC))) {
            registerSync(StashId.CONTACTSYNC);
        }
    }

    public void syncEmployees()
        throws SyncServiceDeactivatedException
    {
        if (isDeactivated()) {
            throw new SyncServiceDeactivatedException();
        }
        LOG.info("Syncing Employees");
        final List<Employee> employees = eFapsClient.getEmployees().stream()
                        .map(Converter::toEntity)
                        .collect(Collectors.toList());
        if (!employees.isEmpty()) {
            final List<Employee> existingEmployees = mongoTemplate.findAll(Employee.class);
            existingEmployees.forEach(existing -> {
                if (!employees.stream().filter(employee -> employee.getOid().equals(existing.getOid())).findFirst()
                                .isPresent()) {
                    mongoTemplate.remove(existing);
                }
            });
            employees.forEach(product -> mongoTemplate.save(product));
        }
        registerSync(StashId.EMPLOYEESYNC);
    }

    public void registerSync(final StashId _stashId)
    {
        registerSync(_stashId.getKey());
    }

    private void registerSync(final String _id)
    {
        SyncInfo syncInfo = mongoTemplate.findById(_id, SyncInfo.class);
        if (syncInfo == null) {
            syncInfo = new SyncInfo();
            syncInfo.setId(_id);
        }
        syncInfo.setLastSync(LocalDateTime.now());
        mongoTemplate.save(syncInfo);
    }

    private SyncInfo getSync(final StashId _stashId)
    {
        return mongoTemplate.findById(_stashId.getKey(), SyncInfo.class);
    }

    public boolean isDeactivated()
    {
        return deactivated;
    }

    public void setDeactivated(final boolean deactivated)
    {
        this.deactivated = deactivated;
    }
}
