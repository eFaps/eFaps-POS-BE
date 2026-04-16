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
import java.io.IOException;
import java.io.StringWriter;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.config.ConfigProperties;
import org.efaps.pos.config.UserAudtiting;
import org.efaps.pos.dto.ContactDto;
import org.efaps.pos.entity.Contact;
import org.efaps.pos.entity.Origin;
import org.efaps.pos.entity.SyncInfo;
import org.efaps.pos.error.PreconditionException;
import org.efaps.pos.repository.ContactRepository;
import org.efaps.pos.util.Converter;
import org.efaps.pos.util.SyncServiceDeactivatedException;
import org.efaps.pos.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.BulkOperations.BulkMode;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

@Service
public class ContactService
{

    private static final Logger LOG = LoggerFactory.getLogger(ContactService.class);

    private final MongoTemplate mongoTemplate;
    private final UserAudtiting userAudtiting;
    private final JsonMapper jsonMapper;
    private final ConfigProperties configProperties;
    private final ContactRepository contactRepository;
    private final EFapsClient eFapsClient;

    public ContactService(final MongoTemplate mongoTemplate,
                          final UserAudtiting userAudtiting,
                          final JsonMapper jsonMapper,
                          final ConfigProperties configProperties,
                          final ContactRepository contactRepository,
                          final EFapsClient eFapsClient)
    {
        this.mongoTemplate = mongoTemplate;
        this.userAudtiting = userAudtiting;
        this.jsonMapper = jsonMapper;
        this.configProperties = configProperties;
        this.contactRepository = contactRepository;
        this.eFapsClient = eFapsClient;
    }

    public Contact getContact(final String _oid)
    {
        return contactRepository.findOneByOid(_oid).orElse(null);
    }

    public Contact findContact(final String ident)
    {
        return findContact(ident, false);
    }

    public Contact findContact(final String ident,
                               final boolean evalCloud)
    {
        var contact = contactRepository.findOneByOid(ident).orElse(contactRepository.findById(ident).orElse(null));
        if (contact == null && evalCloud && Utils.isOid(ident)) {
            final var dto = eFapsClient.getContact(ident);
            if (dto != null) {
                contact = Converter.toEntity(dto);
                contact.setOrigin(Origin.REMOTE);
                contact = contactRepository.save(contact);
            }
        }
        return contact;
    }

    public Page<Contact> getContacts(final Pageable pageable)
    {
        return contactRepository.findAll(pageable);
    }

    public List<Contact> findContacts(final String _term,
                                      final boolean _nameSearch)
    {
        return _nameSearch ? contactRepository.findByNameRegex(_term)
                        : contactRepository.findByIdNumberStartingWith(_term);
    }

    public Contact createContact(final Contact entity)
        throws PreconditionException
    {
        if (!contactRepository.findByIdNumber(entity.getIdNumber()).isEmpty()) {
            throw new PreconditionException("Contact with same IdNumber already exists");
        }
        entity.setOrigin(Origin.LOCAL);
        return contactRepository.save(entity);
    }

    public Contact updateContact(final String id,
                                 final Contact updateEntity)
        throws PreconditionException
    {
        final var existingEntity = contactRepository.findById(id).orElseThrow();
        if (!existingEntity.getIdNumber().equals(updateEntity.getIdNumber())) {
            final var sameIdNumbers = contactRepository.findByIdNumber(updateEntity.getIdNumber());
            for (final var en : sameIdNumbers) {
                if (!en.getId().equals(existingEntity.getId())) {
                    throw new PreconditionException("Contact with same IdNumber already exists");
                }
            }
            existingEntity.setIdNumber(updateEntity.getIdNumber());
        }
        existingEntity.setEmail(updateEntity.getEmail());
        existingEntity.setIdType(updateEntity.getIdType());
        existingEntity.setName(updateEntity.getName());
        existingEntity.setOrigin(Origin.LOCAL);
        existingEntity.setUpdated(true);
        return contactRepository.save(existingEntity);
    }

    public void syncContacts(final SyncInfo syncInfo)
        throws SyncServiceDeactivatedException
    {
        syncUpdatedContacts();
        if (syncInfo != null) {
            final var after = OffsetDateTime.of(syncInfo.getLastSync(), ZoneOffset.of("-5")).minusMinutes(10);
            syncContactsDown(after);
        } else {
            syncContactsDown(null);
        }
    }

    public void syncAllContacts()
        throws SyncServiceDeactivatedException
    {
        final var dumpDto = eFapsClient.getContactsDump();
        if (dumpDto != null) {
            LOG.info("Syncing All Contacts using dump");
            final var checkout = eFapsClient.checkout(dumpDto.getOid());
            try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(checkout.getContent()))) {
                var zipEntry = zis.getNextEntry();
                var i = 0;
                while (zipEntry != null) {
                    LOG.info("Reading contacts from: {}", zipEntry.getName());
                    final var writer = new StringWriter();
                    IOUtils.copy(zis, writer, "UTF-8");
                    final var contacts = jsonMapper.readValue(writer.toString(), new TypeReference<List<ContactDto>>()
                    {
                    });
                    LOG.info("Loaded contacts {} - {} ", i, i + contacts.size());
                    persistContacts(contacts.stream().map(Converter::toEntity).toList());
                    i += contacts.size();
                    zipEntry = zis.getNextEntry();
                }
                zis.closeEntry();
                zis.close();
            } catch (final IOException e) {
                LOG.error("Catched", e);
            }
            syncContacts(new SyncInfo().setLastSync(Utils.toLocal(dumpDto.getUpdateAt())));
        } else {
            syncContactsDown(null);
        }
    }

    public List<Contact> syncContactsUp()
    {
        final Collection<Contact> tosync = contactRepository.findByOidIsNull();
        return syncContactsUp(tosync);
    }

    public List<Contact> syncContactsUp(Collection<Contact> tosync)
    {
        final var syncedContacts = new ArrayList<Contact>();
        for (final Contact contact : tosync) {
            LOG.debug("Syncing Contact: {}", contact);
            final ContactDto recDto = eFapsClient.postContact(Converter.toDto(contact));
            LOG.debug("received Contact: {}", recDto);
            if (recDto.getOid() != null) {
                contact.setOid(recDto.getOid());
                contact.setOrigin(Origin.LOCAL);
                final var savedContact = contactRepository.save(contact);
                syncedContacts.add(savedContact);
            }
        }
        return syncedContacts;
    }

    private void syncUpdatedContacts()
    {
        final Collection<Contact> tosync = contactRepository.findByUpdatedIsTrue();
        for (final Contact contact : tosync) {
            if (contact.getOid() != null) {
                eFapsClient.putContact(Converter.toDto(contact));
                contact.setUpdated(null);
                contactRepository.save(contact);
            }
        }
    }

    private void syncContactsDown(final OffsetDateTime after)
    {
        final var limit = configProperties.getEFaps().getContactLimit();
        var next = true;
        var i = 0;
        while (next) {
            final var offset = i * limit;
            LOG.info("    Contact Batch {} - {}", offset, offset + limit);
            final List<Contact> recievedContacts = eFapsClient.getContacts(limit, i * limit, after)
                            .stream()
                            .map(Converter::toEntity)
                            .collect(Collectors.toList());
            persistContacts(recievedContacts);
            i++;
            next = !(recievedContacts.size() < limit);
        }
    }

    private void persistContacts(final List<Contact> contacts)
    {
        if (!contacts.isEmpty()) {
            var bulkOps = mongoTemplate.bulkOps(BulkMode.UNORDERED, Contact.class);
            final var of = contacts.size();
            var from = 0;
            var count = 0;
            for (final Contact contact : contacts) {
                count++;
                if (count % 1000 == 0) {
                    LOG.info("Persisting contacts {} - {} of {}", from, count, of);
                    from = count;
                    bulkOps.execute();
                    bulkOps = mongoTemplate.bulkOps(BulkMode.UNORDERED, Contact.class);
                } else {
                    LOG.trace("Persisting contact {} of {}", count, of);
                }
                addToBulkOps(bulkOps, contact);
            }
            LOG.info("Persisting contacts {} - {} of {}", from, count, of);
            bulkOps.execute();
        }
    }

    private void addToBulkOps(final BulkOperations bulkOps,
                              final Contact contact)
    {
        final var query = Query.query(Criteria.where("oid").is(contact.getOid()));
        final var update = new Update()
                        .setOnInsert("oid", contact.getOid())
                        .setOnInsert("origin", Origin.REMOTE)
                        .set("email", contact.getEmail())
                        .set("idNumber", contact.getIdNumber())
                        .set("idType", contact.getIdType())
                        .set("name", contact.getName())
                        .set("firstLastName", contact.getFirstLastName())
                        .set("forename", contact.getForename())
                        .set("secondLastName", contact.getSecondLastName())
                        .setOnInsert("user", userAudtiting.getCurrentAuditor().get())
                        .setOnInsert("createdDate", Instant.now())
                        .set("lastModifiedUser", userAudtiting.getCurrentAuditor().get())
                        .set("lastModifiedDate", Instant.now())
                        .inc("version")
                        .setOnInsert("_class", Contact.class.getName());
        bulkOps.upsert(query, update);
    }

    public Optional<Contact> findOneByOid(final String oid)
    {
        return contactRepository.findOneByOid(oid);
    }

    public Optional<Contact> findById(final String id)
    {
        return contactRepository.findById(id);
    }
}
