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
package org.efaps.pos.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.efaps.pos.entity.Contact;
import org.efaps.pos.entity.Visibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ContactRepository
    extends MongoRepository<Contact, String>
{
    @Query(value = "{'visibility': 'VISIBLE'}")
    List<Contact> findAllVisible();

    @Query(value = "{'visibility': 'VISIBLE'}")
    Page<Contact> findAllVisible(Pageable pageable);

    Optional<Contact> findOneByOid(String _oid);

    List<Contact> findByOid(String _oid);

    default List<Contact> findByIdNumberStartingWith(String _term) {
        return findByIdNumberStartingWithAndVisibility(_term, Visibility.VISIBLE);
    }

    List<Contact> findByIdNumberStartingWithAndVisibility(String _term, Visibility visibility);

    @Query(value = "{'name': {$regex : '?0', $options: 'i'}, 'visibility': 'VISIBLE'}")
    List<Contact> findByNameRegex(String _term);

    Collection<Contact> findByOidIsNull();

    List<Contact> findByIdNumber(String _idNumber);

    Collection<Contact> findByUpdatedIsTrue();
}
