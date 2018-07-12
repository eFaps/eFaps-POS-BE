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

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.efaps.pos.client.Checkout;
import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.dto.AbstractPayableDocumentDto;
import org.efaps.pos.dto.BalanceDto;
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
import org.efaps.pos.respository.BalanceRepository;
import org.efaps.pos.respository.ContactRepository;
import org.efaps.pos.respository.InventoryRepository;
import org.efaps.pos.respository.InvoiceRepository;
import org.efaps.pos.respository.PrinterRepository;
import org.efaps.pos.respository.ProductRepository;
import org.efaps.pos.respository.ReceiptRepository;
import org.efaps.pos.respository.SequenceRepository;
import org.efaps.pos.respository.TicketRepository;
import org.efaps.pos.respository.UserRepository;
import org.efaps.pos.respository.WarehouseRepository;
import org.efaps.pos.respository.WorkspaceRepository;
import org.efaps.pos.util.Converter;
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
                       final EFapsClient _eFapsClient)
    {
        this.mongoTemplate = _mongoTemplate;
        this.gridFsTemplate = _gridFsTemplate;
        this.receiptRepository = _receiptRepository;
        this.invoiceRepository = _invoiceRepository;
        this.ticketRepository = _ticketRepository;
        this.productRepository = _productRepository;
        this.sequenceRepository = _sequenceRepository;
        this.contactRepository = _contactRepository;
        this.userRepository = _userRepository;
        this.warehouseRepository = _warehouseRepository;
        this.inventoryRepository = _inventoryRepository;
        this.printerRepository = _printerRepository;
        this.workspaceRepository = _workspaceRepository;
        this.balanceRepository = _balanceRepository;
        this.eFapsClient = _eFapsClient;
    }

    public void syncProperties()
    {
        LOG.info("Syncing Properties");
        final Map<String, String> properties = this.eFapsClient.getProperties();
        final Config config = new Config().setId(Config.KEY).setProperties(properties);
        this.mongoTemplate.save(config);
    }

    public void syncProducts()
    {
        LOG.info("Syncing Products");
        final List<Product> products = this.eFapsClient.getProducts().stream()
                        .map(dto -> Converter.toEntity(dto))
                        .collect(Collectors.toList());
        if (!products.isEmpty()) {
            final List<Product> existingProducts = this.mongoTemplate.findAll(Product.class);
            existingProducts.forEach(existing -> {
                if (!products.stream().filter(product -> product.getOid().equals(existing.getOid())).findFirst()
                                .isPresent()) {
                    this.mongoTemplate.remove(existing);
                }
            });
            products.forEach(product -> this.mongoTemplate.save(product));
        }
        registerSync(StashId.PRODUCTSYNC);
    }

    public void syncCategories()
    {
        LOG.info("Syncing Categories");
        final List<Category> categories = this.eFapsClient.getCategories().stream()
                        .map(dto -> Converter.toEntity(dto))
                        .collect(Collectors.toList());
        if (!categories.isEmpty()) {
            final List<Category> existingCategories = this.mongoTemplate.findAll(Category.class);
            existingCategories.forEach(existing -> {
                if (!categories.stream()
                                .filter(category -> category.getOid().equals(existing.getOid())).findFirst()
                                .isPresent()) {
                    this.mongoTemplate.remove(existing);
                }
            });
            categories.forEach(category -> this.mongoTemplate.save(category));
        }
        registerSync(StashId.CATEGORYSYNC);
    }

    public void syncWorkspaces()
    {
        LOG.info("Syncing Workspaces");
        final List<Workspace> workspaces = this.eFapsClient.getWorkspaces().stream()
                        .map(dto -> Converter.toEntity(dto))
                        .collect(Collectors.toList());
        if (!workspaces.isEmpty()) {
            final List<Workspace> existingWorkspaces = this.workspaceRepository.findAll();
            existingWorkspaces.forEach(existing -> {
                if (!workspaces.stream()
                                .filter(workspace -> workspace.getOid().equals(existing.getOid())).findFirst()
                                .isPresent()) {
                    this.workspaceRepository.delete(existing);
                }
            });
            workspaces.forEach(workspace -> this.workspaceRepository.save(workspace));
        }
        registerSync(StashId.WORKSPACESYNC);
    }

    public void syncWarehouses()
    {
        LOG.info("Syncing Warehouses");
        final List<Warehouse> warehouses = this.eFapsClient.getWarehouses().stream()
                        .map(dto -> Converter.toEntity(dto))
                        .collect(Collectors.toList());
        if (!warehouses.isEmpty()) {
            this.warehouseRepository.deleteAll();
            warehouses.forEach(workspace -> this.warehouseRepository.save(workspace));
        }
        registerSync(StashId.WAREHOUSESYNC);
    }

    public void syncInventory()
    {
        LOG.info("Syncing Inventory");
        final List<InventoryEntry> entries = this.eFapsClient.getInventory().stream()
                        .map(dto -> Converter.toEntity(dto))
                        .collect(Collectors.toList());
        if (!entries.isEmpty()) {
            this.inventoryRepository.deleteAll();
            entries.forEach(workspace -> this.inventoryRepository.save(workspace));
        }
        registerSync(StashId.WAREHOUSESYNC);
    }

    public void syncPrinters()
    {
        LOG.info("Syncing Printers");
        final List<Printer> printers = this.eFapsClient.getPrinters().stream()
                        .map(dto -> Converter.toEntity(dto))
                        .collect(Collectors.toList());
        if (!printers.isEmpty()) {
            this.printerRepository.deleteAll();
            printers.forEach(printer -> this.printerRepository.save(printer));
        }
        registerSync(StashId.PRINTERSYNC);
    }

    public void syncPOSs()
    {
        LOG.info("Syncing POSs");
        final List<Pos> poss = this.eFapsClient.getPOSs().stream()
                        .map(dto -> Converter.toEntity(dto))
                        .collect(Collectors.toList());
        if (!poss.isEmpty()) {
            final List<Pos> existingPoss = this.mongoTemplate.findAll(Pos.class);
            existingPoss.forEach(existing -> {
                if (!poss.stream()
                                .filter(pos -> pos.getOid().equals(existing.getOid())).findFirst()
                                .isPresent()) {
                    this.mongoTemplate.remove(existing);
                }
            });
            poss.forEach(pos -> this.mongoTemplate.save(pos));
        }
        registerSync(StashId.POSSYNC);
    }

    public void syncUsers()
    {
        LOG.info("Syncing Users");
        final List<User> users = this.eFapsClient.getUsers().stream()
                        .map(dto -> Converter.toEntity(dto))
                        .collect(Collectors.toList());
        if (!users.isEmpty()) {
            this.userRepository.deleteAll();
            users.forEach(user -> this.userRepository.save(user));
        }
        registerSync(StashId.USERSYNC);
    }

    public void syncBalance() {
        LOG.info("Syncing Balance");
        final Collection<BalanceDto> tosync = this.balanceRepository.findByOidIsNull().stream()
                        .map(receipt -> Converter.toDto(receipt))
                        .collect(Collectors.toList());
        for (final BalanceDto dto : tosync) {
            LOG.debug("Syncing Balance: {}", dto);
            final BalanceDto recDto = this.eFapsClient.postBalance(dto);
            LOG.debug("received Balance: {}", recDto);
            if (recDto.getOid() != null) {
                final Optional<Balance> balanceOpt = this.balanceRepository.findById(recDto.getId());
                if (balanceOpt.isPresent()) {
                    final Balance balance = balanceOpt.get();
                    balance.setOid(recDto.getOid());
                    this.balanceRepository.save(balance);
                }
            }
        }
        registerSync(StashId.BALANCESYNC);
    }

    public void syncReceipts()
    {
        LOG.info("Syncing Receipts");
        final Collection<Receipt> tosync = this.receiptRepository.findByOidIsNull();
        for (final Receipt receipt : tosync) {
            if (validateContact(receipt)) {
                if (!isOid(receipt.getBalanceOid())) {
                    final Optional<Balance> balanceOpt = this.balanceRepository.findById(receipt.getBalanceOid());
                    if (balanceOpt.isPresent()) {
                        final Balance balance = balanceOpt.get();
                        if (isOid(balance.getOid())) {
                            receipt.setBalanceOid(balance.getOid());
                            this.receiptRepository.save(receipt);
                        } else {
                            syncBalance();
                        }
                    }
                }
                LOG.debug("Syncing Receipt: {}", receipt);
                final ReceiptDto recDto = this.eFapsClient.postReceipt(Converter.toReceiptDto(receipt));
                LOG.debug("received Receipt: {}", recDto);
                if (recDto.getOid() != null) {
                    final Optional<Receipt> receiptOpt = this.receiptRepository.findById(recDto.getId());
                    if (receiptOpt.isPresent()) {
                        final Receipt retReceipt = receiptOpt.get();
                        retReceipt.setOid(recDto.getOid());
                        this.receiptRepository.save(retReceipt);
                    }
                }
            }
        }
        registerSync(StashId.RECEIPTSYNC);
    }

    public void syncInvoices()
    {
        LOG.info("Syncing Invoices");
        final Collection<InvoiceDto> tosync = this.invoiceRepository.findByOidIsNull().stream()
                        .map(receipt -> Converter.toInvoiceDto(receipt))
                        .collect(Collectors.toList());
        boolean triggerBalanceSync = false;
        for (final InvoiceDto dto : tosync) {
            LOG.debug("Syncing Invoice: {}", dto);
            if (validateContact(dto)) {
                if (isOid(dto.getBalanceOid())) {
                    final InvoiceDto recDto = this.eFapsClient.postInvoice(dto);
                    LOG.debug("received Invoice: {}", recDto);
                    if (recDto.getOid() != null) {
                        final Optional<Invoice> opt = this.invoiceRepository.findById(recDto.getId());
                        if (opt.isPresent()) {
                            final Invoice receipt = opt.get();
                            receipt.setOid(recDto.getOid());
                            this.invoiceRepository.save(receipt);
                        }
                    }
                } else {
                    triggerBalanceSync = true;
                }
            }
        }
        if (triggerBalanceSync) {
            syncBalance();
        }
        registerSync(StashId.INVOICESYNC);
    }

    public void syncTickets()
    {
        LOG.info("Syncing Tickets");
        final Collection<TicketDto> tosync = this.ticketRepository.findByOidIsNull().stream()
                        .map(receipt -> Converter.toTicketDto(receipt))
                        .collect(Collectors.toList());
        boolean triggerBalanceSync = false;
        for (final TicketDto dto : tosync) {
            LOG.debug("Syncing Ticket: {}", dto);
            if (validateContact(dto)) {
                if (isOid(dto.getBalanceOid())) {
                    final TicketDto recDto = this.eFapsClient.postTicket(dto);
                    LOG.debug("received Ticket: {}", recDto);
                    if (recDto.getOid() != null) {
                        final Optional<Ticket> opt = this.ticketRepository.findById(recDto.getId());
                        if (opt.isPresent()) {
                            final Ticket receipt = opt.get();
                            receipt.setOid(recDto.getOid());
                            this.ticketRepository.save(receipt);
                        }
                    }
                } else {
                    triggerBalanceSync = true;
                }
            }
        }
        if (triggerBalanceSync) {
            syncBalance();
        }
        registerSync(StashId.TICKETSYNC);
    }

    private boolean validateContact(final AbstractPayableDocumentDto _dto)
    {
        return this.contactRepository.findOneByOid(_dto.getContactOid()).isPresent();
    }

    private boolean validateContact(final AbstractPayableDocument<?> _entity)
    {
        return this.contactRepository.findOneByOid(_entity.getContactOid()).isPresent();
    }

    public void syncImages()
    {
        LOG.info("Syncing Images");
        final List<Product> products = this.productRepository.findAll();
        for (final Product product : products) {
            if (product.getImageOid() != null) {
                LOG.debug("Syncing Image {}", product.getImageOid());
                final Checkout checkout = this.eFapsClient.checkout(product.getImageOid());
                if (checkout != null) {
                    this.gridFsTemplate.delete(new Query(Criteria.where("metadata.oid").is(product.getImageOid())));
                    final DBObject metaData = new BasicDBObject();
                    metaData.put("oid", product.getImageOid());
                    metaData.put("contentType", checkout.getContentType().toString());
                    this.gridFsTemplate.store(new ByteArrayInputStream(checkout.getContent()), checkout.getFilename(),
                                    metaData);
                }
            }
        }
        registerSync(StashId.IMAGESYNC);
    }

    public void syncReports()
    {
        LOG.info("Syncing Reports");
        final List<Workspace> workspaces = this.workspaceRepository.findAll();
        final Set<String> reportOids = workspaces.stream()
            .map(Workspace::getPrintCmds)
            .flatMap(Set::stream)
            .map(PrintCmd::getReportOid)
            .collect(Collectors.toSet());

        for (final String reportOid : reportOids) {
            LOG.debug("Syncing Report {}", reportOid);
            final Checkout checkout = this.eFapsClient.checkout(reportOid);
            if (checkout != null) {
                this.gridFsTemplate.delete(new Query(Criteria.where("metadata.oid").is(reportOid)));
                final DBObject metaData = new BasicDBObject();
                metaData.put("oid", reportOid);
                metaData.put("contentType", checkout.getContentType().toString());
                this.gridFsTemplate.store(new ByteArrayInputStream(checkout.getContent()), checkout.getFilename(),
                                    metaData);
            }
        }
        registerSync(StashId.REPORTSYNC);
    }

    public void syncSequences() {
        LOG.info("Syncing Sequences");
        final List<Sequence> sequences = this.eFapsClient.getSequences().stream()
                        .map(dto -> Converter.toEntity(dto))
                        .collect(Collectors.toList());
        for (final Sequence sequence : sequences) {
            LOG.debug("Syncing Sequence: {}", sequence);
            final Optional<Sequence> seqOpt = this.sequenceRepository.findByOid(sequence.getOid());
            if (seqOpt.isPresent()) {
                final Sequence es = seqOpt.get();
                if (es.getSeq() < sequence.getSeq()) {
                    es.setSeq(sequence.getSeq());
                    this.sequenceRepository.save(sequence);
                }
                if (!es.getFormat().equals(sequence.getFormat())) {
                    es.setFormat(sequence.getFormat());
                    this.sequenceRepository.save(sequence);
                }
            } else {
                this.sequenceRepository.save(sequence);
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
        final Collection<Contact> tosync = this.contactRepository.findByOidIsNull();
        for (final Contact contact : tosync) {
            LOG.debug("Syncing Contact: {}", contact);
            final ContactDto recDto = this.eFapsClient.postContact(Converter.toContactDto(contact));
            LOG.debug("received Contact: {}", recDto);
            if (recDto.getOid() != null) {
                contact.setOid(recDto.getOid());
                this.contactRepository.save(contact);
                this.receiptRepository.findByContactOid(contact.getId()).stream().forEach(doc -> {
                    doc.setContactOid(contact.getOid());
                    this.receiptRepository.save(doc);
                });
                this.invoiceRepository.findByContactOid(contact.getId()).stream().forEach(doc -> {
                    doc.setContactOid(contact.getOid());
                    this.invoiceRepository.save(doc);
                });
                this.ticketRepository.findByContactOid(contact.getId()).stream().forEach(doc -> {
                    doc.setContactOid(contact.getOid());
                    this.ticketRepository.save(doc);
                });
            }
        }
    }

    private void syncContactsDown() {
        final List<Contact> recievedContacts = this.eFapsClient.getContacts().stream()
                        .map(dto -> Converter.toEntity(dto))
                        .collect(Collectors.toList());
        for (final Contact contact : recievedContacts) {
           final List<Contact> contacts = this.contactRepository.findByOid(contact.getOid());
           if (CollectionUtils.isEmpty(contacts)) {
               this.contactRepository.save(contact);
           } else if (contacts.size() > 1) {
               contacts.forEach(entity -> this.contactRepository.delete(entity));
               this.contactRepository.save(contact);
           } else {
               contact.setId(contacts.get(0).getId());
               this.contactRepository.save(contact);
           }
        }

        for (final Contact contact : this.contactRepository.findAll()) {
            if (contact.getOid() != null && !recievedContacts.stream().filter(recieved -> recieved.getOid().equals(
                            contact.getOid())).findFirst().isPresent()) {
                this.contactRepository.delete(contact);
            }
        }
    }

    private void registerSync(final StashId _stashId)
    {
        registerSync(_stashId.getKey());
    }

    private void registerSync(final String _id)
    {
        SyncInfo syncInfo = this.mongoTemplate.findById(_id, SyncInfo.class);
        if (syncInfo == null) {
            syncInfo = new SyncInfo();
            syncInfo.setId(_id);
        }
        syncInfo.setLastSync(LocalDateTime.now());
        this.mongoTemplate.save(syncInfo);
    }

    private boolean isOid(final String _value) {
        return _value != null && _value.matches("^\\d+\\.\\d+$");
    }
}
