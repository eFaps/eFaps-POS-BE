package org.efaps.pos.repository;

import org.efaps.pos.entity.StocktakingEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StocktakingEntriesRepository
    extends MongoRepository<StocktakingEntry, String>
{
    Page<StocktakingEntry> findAllByStocktakingId(String stocktakingId, Pageable pageable);
}
