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
import java.util.Optional;

import org.efaps.pos.entity.InventoryEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InventoryRepository
    extends MongoRepository<InventoryEntry, String>
{
    Collection<InventoryEntry> findByWarehouseOid(String _warehouseOid);

    Collection<InventoryEntry> findByProductOid(String _productOid);

    Optional<InventoryEntry> findByWarehouseOidAndProductOid(String _warehouseOid, String _productOid);
}
