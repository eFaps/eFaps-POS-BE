package org.efaps.pos.repository;

import java.util.Optional;

import org.efaps.pos.dto.StocktakingStatus;
import org.efaps.pos.entity.Stocktaking;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StocktakingRepository
    extends MongoRepository<Stocktaking, String>
{

    Optional<Stocktaking> findOneByWarehouseOidAndStatus(String warehouseOid,
                                                         StocktakingStatus open);

}
