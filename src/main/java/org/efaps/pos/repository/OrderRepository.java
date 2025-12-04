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

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.efaps.pos.dto.DocStatus;
import org.efaps.pos.entity.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository
    extends MongoRepository<Order, String>
{
    Collection<Order> findBySpotIsNotNullAndStatus(DocStatus _status);

    Collection<Order> findByStatus(DocStatus _status);

    Collection<Order> findByNumberLikeIgnoreCase(String _term);

    Collection<Order> findByOidIsNullAndStatus(DocStatus _status);

    Collection<Order> findByOidIsNull();

    List<Order> findByPayableOidIn(Collection<String> idents);

    Optional<Order> findByOid(String oid);

    List<Order> findByCreatedDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    Collection<Order> findByContactOid(String contactOid);

    Collection<Order> findByLoyaltyContactOid(String loyaltyContactOid);
}
