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

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.config.ConfigProperties;
import org.efaps.pos.dto.ContactDto;
import org.efaps.pos.entity.Contact;
import org.efaps.pos.entity.SyncInfo;
import org.efaps.pos.entity.Visibility;
import org.efaps.pos.error.PreconditionException;
import org.efaps.pos.repository.ContactRepository;
import org.efaps.pos.util.Converter;
import org.efaps.pos.util.SyncServiceDeactivatedException;
import org.efaps.pos.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ContactService
{

    private static final Logger LOG = LoggerFactory.getLogger(ContactService.class);

    private final ConfigProperties configProperties;
    private final ContactRepository contactRepository;
    private final EFapsClient eFapsClient;

    public ContactService(final ConfigProperties configProperties,
                          final ContactRepository contactRepository,
                          final EFapsClient eFapsClient)
    {
        this.configProperties = configProperties;
        this.contactRepository = contactRepository;
        this.eFapsClient = eFapsClient;
    }

    public Contact getContact(final String _oid)
    {
        return contactRepository.findOneByOid(_oid).orElse(null);
    }

    public Contact findContact(final String _key)
    {
        return findContact(_key, false);
    }

    public Contact findContact(final String _key,
                               final boolean evalCloud)
    {
        var contact = contactRepository.findOneByOid(_key).orElse(contactRepository.findById(_key).orElse(null));
        if (contact == null && evalCloud && Utils.isOid(_key)) {
            final var dto = eFapsClient.getContact(_key);
            if (dto != null) {
                contact = Converter.toEntity(dto);
                contact.setVisibility(Visibility.HIDDEN);
                contact = contactRepository.save(contact);
            }
        }
        return contact;
    }

    public Page<Contact> getContacts(final Pageable pageable)
    {
        return contactRepository.findAllVisible(pageable);
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
        entity.setVisibility(Visibility.VISIBLE);
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
        existingEntity.setVisibility(Visibility.VISIBLE);
        existingEntity.setUpdated(true);
        return contactRepository.save(existingEntity);
    }

    public boolean syncContacts(final SyncInfo syncInfo)
        throws SyncServiceDeactivatedException
    {
        boolean ret = false;
        syncUpdatedContacts();
        if (syncInfo != null) {
            final var after = OffsetDateTime.of(syncInfo.getLastSync(), ZoneOffset.of("-5")).minusMinutes(10);
            syncContactsDown(after);
            ret = true;
        }
        return ret;
    }

    public List<Contact> syncContactsUp()
    {
        final var syncedContacts = new ArrayList<Contact>();
        final Collection<Contact> tosync = contactRepository.findByOidIsNull();
        for (final Contact contact : tosync) {
            LOG.debug("Syncing Contact: {}", contact);
            final ContactDto recDto = eFapsClient.postContact(Converter.toDto(contact));
            LOG.debug("received Contact: {}", recDto);
            if (recDto.getOid() != null) {
                contact.setOid(recDto.getOid());
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

    private void syncContactsDown(OffsetDateTime after)
    {
        final var queriedContacts = new ArrayList<Contact>();
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
            queriedContacts.addAll(recievedContacts);
            i++;
            next = !(recievedContacts.size() < limit);
        }
        for (final Contact contact : queriedContacts) {
            contact.setVisibility(Visibility.VISIBLE);
            final List<Contact> contacts = contactRepository.findByOid(contact.getOid());
            if (CollectionUtils.isEmpty(contacts)) {
                contactRepository.save(contact);
            } else if (contacts.size() > 1) {
                contacts.forEach(entity -> contactRepository.delete(entity));
                contactRepository.save(contact);
            } else {
                final var updatedContact = contacts.get(0);
                updatedContact
                                .setEmail(contact.getEmail())
                                .setIdNumber(contact.getIdNumber())
                                .setIdType(contact.getIdType())
                                .setName(contact.getName())
                                .setVisibility(Visibility.VISIBLE);
                contactRepository.save(updatedContact);
            }
        }
        if (after == null && !queriedContacts.isEmpty()) {
            for (final Contact contact : contactRepository.findAllVisible()) {
                if (contact.getOid() != null && !queriedContacts.stream()
                                .filter(recieved -> recieved.getOid().equals(contact.getOid()))
                                .findFirst()
                                .isPresent()) {
                    contact.setVisibility(Visibility.HIDDEN);
                    contactRepository.save(contact);
                }
            }
        }
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
