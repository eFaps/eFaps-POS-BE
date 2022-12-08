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

import java.util.List;

import org.efaps.pos.entity.Contact;
import org.efaps.pos.repository.ContactRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ContactService
{

    private final ContactRepository contactRepository;

    public ContactService(final ContactRepository _contactRepository)
    {
        contactRepository = _contactRepository;
    }

    public Contact getContact(final String _oid)
    {

        return contactRepository.findOneByOid(_oid).orElse(null);
    }

    public Contact findContact(final String _key)
    {
        return contactRepository.findOneByOid(_key).orElse(contactRepository.findById(_key).orElse(null));
    }

    public Page<Contact> getContacts(final Pageable pageable)
    {
        return contactRepository.findAll(pageable);
    }

    public List<Contact> findContacts(final String _term, final boolean _nameSearch)
    {
        return _nameSearch ? contactRepository.findByNameRegex(_term)
                        : contactRepository.findByIdNumberStartingWith(_term);
    }

    public Contact createContact(final Contact _entity)
    {
        if (!contactRepository.findByIdNumber(_entity.getIdNumber()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,
                            "Contact with same IdNumber already exists");
        }
        return contactRepository.save(_entity);
    }
}
