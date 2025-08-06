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

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.efaps.pos.entity.Invoice;
import org.efaps.pos.entity.Origin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface InvoiceRepository
    extends MongoRepository<Invoice, String>
{
    Collection<Invoice> findByOidIsNull();

    Collection<Invoice> findByContactOid(String contactOid);

    Collection<Invoice> findByBalanceOid(String balanceOid);

    @Query("""
        SELECT c\
        FROM Invoice c \
        WHERE (:name is null or c.name = :name) and (:email is null\
         or c.email = :email)""")
    Collection<Invoice> findByNumberLikeIgnoreCaseAndOrginIn(String term, Set<Origin> orgings);

    Collection<Invoice> findByDate(LocalDate date);

    Optional<Invoice> findByOid(String oid);

    List<Invoice> findByNumber(final String number);
}
