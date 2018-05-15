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
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.efaps.pos.client.Checkout;
import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.dto.InvoiceDto;
import org.efaps.pos.dto.ReceiptDto;
import org.efaps.pos.dto.TicketDto;
import org.efaps.pos.entity.Category;
import org.efaps.pos.entity.Contact;
import org.efaps.pos.entity.Invoice;
import org.efaps.pos.entity.Pos;
import org.efaps.pos.entity.Product;
import org.efaps.pos.entity.Receipt;
import org.efaps.pos.entity.Sequence;
import org.efaps.pos.entity.Ticket;
import org.efaps.pos.entity.User;
import org.efaps.pos.entity.Workspace;
import org.efaps.pos.respository.ContactRepository;
import org.efaps.pos.respository.InvoiceRepository;
import org.efaps.pos.respository.ProductRepository;
import org.efaps.pos.respository.ReceiptRepository;
import org.efaps.pos.respository.SequenceRepository;
import org.efaps.pos.respository.TicketRepository;
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

    @Autowired
    public SyncService(final MongoTemplate _mongoTemplate,
                       final GridFsTemplate _gridFsTemplate,
                       final ReceiptRepository _receiptRepository,
                       final InvoiceRepository _invoiceRepository,
                       final TicketRepository _ticketRepository,
                       final ProductRepository _productRepository,
                       final SequenceRepository _sequenceRepository,
                       final ContactRepository _contactRepository,
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
        this.eFapsClient = _eFapsClient;
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
    }

    public void syncWorkspaces()
    {
        LOG.info("Syncing Workspaces");
        final List<Workspace> workspaces = this.eFapsClient.getWorkspaces().stream()
                        .map(dto -> Converter.toEntity(dto))
                        .collect(Collectors.toList());
        if (!workspaces.isEmpty()) {
            final List<Workspace> existingWorkspaces = this.mongoTemplate.findAll(Workspace.class);
            existingWorkspaces.forEach(existing -> {
                if (!workspaces.stream()
                                .filter(workspace -> workspace.getOid().equals(existing.getOid())).findFirst()
                                .isPresent()) {
                    this.mongoTemplate.remove(existing);
                }
            });
            workspaces.forEach(workspace -> this.mongoTemplate.save(workspace));
        }
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
    }

    public void syncUsers()
    {
        LOG.info("Syncing Users");
        final List<User> users = this.eFapsClient.getUsers().stream()
                        .map(dto -> Converter.toEntity(dto))
                        .collect(Collectors.toList());
        if (!users.isEmpty()) {
            final List<User> existingUsers = this.mongoTemplate.findAll(User.class);
            existingUsers.forEach(existing -> {
                if (!users.stream()
                                .filter(user -> user.getOid().equals(existing.getOid())).findFirst()
                                .isPresent()) {
                    this.mongoTemplate.remove(existing);
                }
            });
            users.forEach(user -> this.mongoTemplate.save(user));
        }
    }

    public void syncReceipts()
    {
        LOG.info("Syncing Receipts");
        final Collection<ReceiptDto> tosync = this.receiptRepository.findByOidIsNull().stream()
                        .map(receipt -> Converter.toReceiptDto(receipt))
                        .collect(Collectors.toList());
        for (final ReceiptDto dto : tosync) {
            LOG.debug("Syncing Receipt: {}", dto);
            final ReceiptDto recDto = this.eFapsClient.postReceipt(dto);
            LOG.debug("received Receipt: {}", recDto);
            if (recDto.getOid() != null) {
                final Optional<Receipt> receiptOpt = this.receiptRepository.findById(recDto.getId());
                if (receiptOpt.isPresent()) {
                    final Receipt receipt = receiptOpt.get();
                    receipt.setOid(recDto.getOid());
                    this.receiptRepository.save(receipt);
                }
            }
        }
    }

    public void syncInvoices()
    {
        LOG.info("Syncing Invoices");
        final Collection<InvoiceDto> tosync = this.invoiceRepository.findByOidIsNull().stream()
                        .map(receipt -> Converter.toInvoiceDto(receipt))
                        .collect(Collectors.toList());
        for (final InvoiceDto dto : tosync) {
            LOG.debug("Syncing Invoice: {}", dto);
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
        }
    }

    public void syncTickets()
    {
        LOG.info("Syncing Tickets");
        final Collection<TicketDto> tosync = this.ticketRepository.findByOidIsNull().stream()
                        .map(receipt -> Converter.toTicketDto(receipt))
                        .collect(Collectors.toList());
        for (final TicketDto dto : tosync) {
            LOG.debug("Syncing Ticket: {}", dto);
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
        }
    }

    public void syncImages()
    {
        LOG.info("Syncing Images");
        final List<Product> products = this.productRepository.findAll();
        for (final Product product : products) {
            if (product.getImageOid() != null) {
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
    }

    public void syncContacts()
    {
        LOG.info("Syncing Contacts");
        final List<Contact> contacts = this.eFapsClient.getContacts().stream()
                        .map(dto -> Converter.toEntity(dto))
                        .collect(Collectors.toList());
        for (final Contact contact : contacts) {
           final Optional<Contact> contactOpt = this.contactRepository.findByOid(contact.getOid());
           if (contactOpt.isPresent()) {
               final Contact entity = contactOpt.get();
               contact.setId(entity.getId());
               this.contactRepository.save(contact);
           } else {
               this.contactRepository.save(contact);
           }
        }
    }
}
