package org.efaps.pos.repository;

import java.util.List;

import org.efaps.pos.entity.StocktakingEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StocktakingEntriesRepository
    extends MongoRepository<StocktakingEntry, String>
{

    List<StocktakingEntry> findAllByStocktakingId(String stocktakingId);

}
