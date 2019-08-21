package org.efaps.pos.repository;

import org.efaps.pos.entity.CollectOrder;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CollectOrderRepository extends MongoRepository<CollectOrder, String>
{

}
