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

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

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

import org.apache.commons.collections4.CollectionUtils;
import org.efaps.pos.ConfigProperties;
import org.efaps.pos.ConfigProperties.Company;
import org.efaps.pos.client.Checkout;
import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.context.Context;
import org.efaps.pos.dto.BalanceDto;
import org.efaps.pos.dto.BalanceStatus;
import org.efaps.pos.dto.ContactDto;
import org.efaps.pos.dto.InvoiceDto;
import org.efaps.pos.dto.ReceiptDto;
import org.efaps.pos.dto.TicketDto;
import org.efaps.pos.entity.AbstractPayableDocument;
import org.efaps.pos.entity.Balance;
import org.efaps.pos.entity.Category;
import org.efaps.pos.entity.Config;
import org.efaps.pos.entity.Contact;
import org.efaps.pos.entity.InventoryEntry;
import org.efaps.pos.entity.Invoice;
import org.efaps.pos.entity.Pos;
import org.efaps.pos.entity.Printer;
import org.efaps.pos.entity.Product;
import org.efaps.pos.entity.Receipt;
import org.efaps.pos.entity.Sequence;
import org.efaps.pos.entity.StashId;
import org.efaps.pos.entity.SyncInfo;
import org.efaps.pos.entity.Ticket;
import org.efaps.pos.entity.User;
import org.efaps.pos.entity.Warehouse;
import org.efaps.pos.entity.Workspace;
import org.efaps.pos.entity.Workspace.PrintCmd;
import org.efaps.pos.repository.BalanceRepository;
import org.efaps.pos.repository.ContactRepository;
import org.efaps.pos.repository.InventoryRepository;
import org.efaps.pos.repository.InvoiceRepository;
import org.efaps.pos.repository.PrinterRepository;
import org.efaps.pos.repository.ProductRepository;
import org.efaps.pos.repository.ReceiptRepository;
import org.efaps.pos.repository.SequenceRepository;
import org.efaps.pos.repository.TicketRepository;
import org.efaps.pos.repository.UserRepository;
import org.efaps.pos.repository.WarehouseRepository;
import org.efaps.pos.repository.WorkspaceRepository;
import org.efaps.pos.util.Converter;
import org.efaps.pos.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

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
    private final ProductRepository productRepository;
    private final SequenceRepository sequenceRepository;
    private final ContactRepository contactRepository;
    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryRepository inventoryRepository;
    private final PrinterRepository printerRepository;
    private final WorkspaceRepository workspaceRepository;
    private final BalanceRepository balanceRepository;
    private final ConfigProperties configProperties;

    @Autowired
    public SyncService(final MongoTemplate _mongoTemplate,
                       final GridFsTemplate _gridFsTemplate,
                       final ReceiptRepository _receiptRepository,
                       final InvoiceRepository _invoiceRepository,
                       final TicketRepository _ticketRepository,
                       final ProductRepository _productRepository,
                       final SequenceRepository _sequenceRepository,
                       final ContactRepository _contactRepository,
                       final UserRepository _userRepository,
                       final WarehouseRepository _warehouseRepository,
                       final InventoryRepository _inventoryRepository,
                       final PrinterRepository _printerRepository,
                       final WorkspaceRepository _workspaceRepository,
                       final BalanceRepository _balanceRepository,
                       final EFapsClient _eFapsClient,
                       final ConfigProperties _configProperties)
    {
        mongoTemplate = _mongoTemplate;
        gridFsTemplate = _gridFsTemplate;
        receiptRepository = _receiptRepository;
        invoiceRepository = _invoiceRepository;
        ticketRepository = _ticketRepository;
        productRepository = _productRepository;
        sequenceRepository = _sequenceRepository;
        contactRepository = _contactRepository;
        userRepository = _userRepository;
        warehouseRepository = _warehouseRepository;
        inventoryRepository = _inventoryRepository;
        printerRepository = _printerRepository;
        workspaceRepository = _workspaceRepository;
        balanceRepository = _balanceRepository;
        eFapsClient = _eFapsClient;
        configProperties = _configProperties;
    }

    public void runSyncJob(final String _methodName)
        throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
        InvocationTargetException
    {
        final Method method = getClass().getMethod(_methodName);
        if (configProperties.getCompanies().size() > 0) {
            for (final Company company : configProperties.getCompanies()) {
                Context.get().setCompany(company);
                method.invoke(this);
            }
        } else {
            method.invoke(this);
        }
    }

    public void syncProperties()
    {
        LOG.info("Syncing Properties");
        final Map<String, String> properties = eFapsClient.getProperties();
        final Config config = new Config().setId(Config.KEY).setProperties(properties);
        mongoTemplate.save(config);
    }

    public void syncProducts()
    {
        LOG.info("Syncing Products");
        final List<Product> products = eFapsClient.getProducts().stream()
                        .map(dto -> Converter.toEntity(dto))
                        .collect(Collectors.toList());
        if (!products.isEmpty()) {
            final List<Product> existingProducts = mongoTemplate.findAll(Product.class);
            existingProducts.forEach(existing -> {
                if (!products.stream().filter(product -> product.getOid().equals(existing.getOid())).findFirst()
                                .isPresent()) {
                    mongoTemplate.remove(existing);
                }
            });
            products.forEach(product -> mongoTemplate.save(product));
        }
        registerSync(StashId.PRODUCTSYNC);
    }

    public void syncCategories()
    {
        LOG.info("Syncing Categories");
        final List<Category> categories = eFapsClient.getCategories().stream()
                        .map(dto -> Converter.toEntity(dto))
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
    {
        LOG.info("Syncing Workspaces");
        final List<Workspace> workspaces = eFapsClient.getWorkspaces().stream()
                        .map(dto -> Converter.toEntity(dto))
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
    {
        LOG.info("Syncing Warehouses");
        final List<Warehouse> warehouses = eFapsClient.getWarehouses().stream()
                        .map(dto -> Converter.toEntity(dto))
                        .collect(Collectors.toList());
        if (!warehouses.isEmpty()) {
            warehouseRepository.deleteAll();
            warehouses.forEach(workspace -> warehouseRepository.save(workspace));
        }
        registerSync(StashId.WAREHOUSESYNC);
    }

    public void syncInventory()
    {
        LOG.info("Syncing Inventory");
        final List<InventoryEntry> entries = eFapsClient.getInventory().stream()
                        .map(dto -> Converter.toEntity(dto))
                        .collect(Collectors.toList());
        if (!entries.isEmpty()) {
            inventoryRepository.deleteAll();
            entries.forEach(workspace -> inventoryRepository.save(workspace));
        }
        registerSync(StashId.WAREHOUSESYNC);
    }

    public void syncPrinters()
    {
        LOG.info("Syncing Printers");
        final List<Printer> printers = eFapsClient.getPrinters().stream()
                        .map(dto -> Converter.toEntity(dto))
                        .collect(Collectors.toList());
        if (!printers.isEmpty()) {
            printerRepository.deleteAll();
            printers.forEach(printer -> printerRepository.save(printer));
        }
        registerSync(StashId.PRINTERSYNC);
    }

    public void syncPOSs()
    {
        LOG.info("Syncing POSs");
        final List<Pos> poss = eFapsClient.getPOSs().stream()
                        .map(dto -> Converter.toEntity(dto))
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
    {
        LOG.info("Syncing Users");
        final List<User> users = eFapsClient.getUsers().stream()
                        .map(dto -> Converter.toEntity(dto))
                        .collect(Collectors.toList());
        if (!users.isEmpty()) {
            userRepository.deleteAll();
            users.forEach(user -> userRepository.save(user));
        }
        registerSync(StashId.USERSYNC);
    }

    public void syncPayables()
    {
        syncContactsUp();
        syncBalance();
        syncReceipts();
        syncInvoices();
        syncTickets();
    }

    public void syncBalance()
    {
        LOG.info("Syncing Balance");
        final Collection<BalanceDto> tosync = balanceRepository.findByOidIsNull().stream()
                        .map(receipt -> Converter.toDto(receipt))
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
                        .map(balance -> Converter.toDto(balance))
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

    public void syncReceipts()
    {
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
                        receiptRepository.save(retReceipt);
                    }
                }
            }  else {
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
    {
        LOG.info("Syncing Invoices");
        final Collection<Invoice> tosync = invoiceRepository.findByOidIsNull();
        for (final Invoice dto : tosync) {
            LOG.debug("Syncing Invoice: {}", dto);
            if (validateContact(dto) && verifyBalance(dto)) {
                final InvoiceDto recDto = eFapsClient.postInvoice(Converter.toInvoiceDto(dto));
                LOG.debug("received Invoice: {}", recDto);
                if (recDto.getOid() != null) {
                    final Optional<Invoice> opt = invoiceRepository.findById(recDto.getId());
                    if (opt.isPresent()) {
                        final Invoice receipt = opt.get();
                        receipt.setOid(recDto.getOid());
                        invoiceRepository.save(receipt);
                    }
                }
            } else {
                LOG.debug("skipped Invoice: {}", dto);
            }
        }
        registerSync(StashId.INVOICESYNC);
    }

    public void syncTickets()
    {
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
                        ticketRepository.save(receipt);
                    }
                }
            } else {
                LOG.debug("skipped Ticket: {}", dto);
            }
        }
        registerSync(StashId.TICKETSYNC);
    }

    private boolean validateContact(final AbstractPayableDocument<?> _entity)
    {
        return contactRepository.findOneByOid(_entity.getContactOid()).isPresent();
    }

    public void syncImages()
    {
        LOG.info("Syncing Images");
        final List<Product> products = productRepository.findAll();
        for (final Product product : products) {
            if (product.getImageOid() != null) {
                LOG.debug("Syncing Image {}", product.getImageOid());
                final Checkout checkout = eFapsClient.checkout(product.getImageOid());
                if (checkout != null) {
                    gridFsTemplate.delete(new Query(Criteria.where("metadata.oid").is(product.getImageOid())));
                    final DBObject metaData = new BasicDBObject();
                    metaData.put("oid", product.getImageOid());
                    metaData.put("contentType", checkout.getContentType().toString());
                    gridFsTemplate.store(new ByteArrayInputStream(checkout.getContent()), checkout.getFilename(),
                                    metaData);
                }
            }
        }
        registerSync(StashId.IMAGESYNC);
    }

    public void syncReports()
    {
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

    public void syncSequences() {
        LOG.info("Syncing Sequences");
        final List<Sequence> sequences = eFapsClient.getSequences().stream()
                        .map(dto -> Converter.toEntity(dto))
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

    public void syncContacts()
    {
        LOG.info("Syncing Contacts");
        syncContactsUp();
        syncContactsDown();
        registerSync(StashId.CONTACTSYNC);
    }

    private void syncContactsUp()
    {
        final Collection<Contact> tosync = contactRepository.findByOidIsNull();
        for (final Contact contact : tosync) {
            LOG.debug("Syncing Contact: {}", contact);
            final ContactDto recDto = eFapsClient.postContact(Converter.toDto(contact));
            LOG.debug("received Contact: {}", recDto);
            if (recDto.getOid() != null) {
                contact.setOid(recDto.getOid());
                contactRepository.save(contact);
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
            }
        }
    }

    private void syncContactsDown() {
        final List<Contact> recievedContacts = eFapsClient.getContacts().stream()
                        .map(dto -> Converter.toEntity(dto))
                        .collect(Collectors.toList());
        for (final Contact contact : recievedContacts) {
           final List<Contact> contacts = contactRepository.findByOid(contact.getOid());
           if (CollectionUtils.isEmpty(contacts)) {
               contactRepository.save(contact);
           } else if (contacts.size() > 1) {
               contacts.forEach(entity -> contactRepository.delete(entity));
               contactRepository.save(contact);
           } else {
               contact.setId(contacts.get(0).getId());
               contactRepository.save(contact);
           }
        }

        for (final Contact contact : contactRepository.findAll()) {
            if (contact.getOid() != null && !recievedContacts.stream().filter(recieved -> recieved.getOid().equals(
                            contact.getOid())).findFirst().isPresent()) {
                contactRepository.delete(contact);
            }
        }
    }

    private void registerSync(final StashId _stashId)
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

}
