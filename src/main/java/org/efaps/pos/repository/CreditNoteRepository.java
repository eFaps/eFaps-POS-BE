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

import org.efaps.pos.entity.CreditNote;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CreditNoteRepository
    extends MongoRepository<CreditNote, String>
{
    List<CreditNote> findBySourceDocOid(String _oid);

    Collection<CreditNote> findByOidIsNull();

    Collection<CreditNote> findByBalanceOid(String _balanceOid);

    Collection<CreditNote> findByDate(LocalDate date);

    Optional<CreditNote> findByOid(String oid);
}
