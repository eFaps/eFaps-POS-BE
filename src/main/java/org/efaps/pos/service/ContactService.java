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

import java.util.List;

import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.entity.Contact;
import org.efaps.pos.entity.Visibility;
import org.efaps.pos.error.PreconditionException;
import org.efaps.pos.repository.ContactRepository;
import org.efaps.pos.util.Converter;
import org.efaps.pos.util.Utils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ContactService
{

    private final ContactRepository contactRepository;
    private final EFapsClient eFapsClient;

    public ContactService(final ContactRepository _contactRepository,
                          final EFapsClient eFapsClient)
    {
        contactRepository = _contactRepository;
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
            for (final var en :sameIdNumbers) {
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
}
