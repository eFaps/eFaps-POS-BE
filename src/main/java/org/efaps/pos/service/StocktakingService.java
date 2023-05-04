/*
 * Copyright 2003 - 2023 The eFaps Team
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

import java.time.LocalDateTime;
import java.util.Optional;

import org.efaps.pos.dto.StockTakingEntryDto;
import org.efaps.pos.dto.StocktakingStatus;
import org.efaps.pos.entity.Stocktaking;
import org.efaps.pos.entity.StocktakingEntry;
import org.efaps.pos.entity.User;
import org.efaps.pos.repository.StocktakingEntriesRepository;
import org.efaps.pos.repository.StocktakingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class StocktakingService
{

    private final StocktakingRepository stocktakingRepository;
    private final StocktakingEntriesRepository stocktakingEntriesRepository;
    private final SequenceService sequenceService;

    public StocktakingService(final StocktakingRepository stocktakingRepository,
                              StocktakingEntriesRepository stocktakingEntriesRepository,
                              final SequenceService sequenceService)
    {
        this.stocktakingRepository = stocktakingRepository;
        this.stocktakingEntriesRepository = stocktakingEntriesRepository;
        this.sequenceService = sequenceService;
    }

    public Optional<Stocktaking> getCurrent(final String warehouseOid)
    {
        return stocktakingRepository.findOneByWarehouseOidAndStatus(warehouseOid, StocktakingStatus.OPEN);
    }

    public Stocktaking createStocktaking(final User principal,
                                         final String warehousOid)
    {
        final String number = sequenceService.getNextNumber("Stocktaking", false);
        final Stocktaking stocktaking = new Stocktaking()
                        .setStartAt(LocalDateTime.now())
                        .setUserOid(principal.getOid())
                        .setStatus(StocktakingStatus.OPEN)
                        .setNumber(number)
                        .setWarehouseOid(warehousOid);
        return stocktakingRepository.save(stocktaking);
    }

    public Page<Stocktaking> getStocktakings(Pageable pageable)
    {
        return stocktakingRepository.findAll(pageable);
    }

    public StocktakingEntry addEntry(String stocktakingId,
                                     StockTakingEntryDto entry)
    {
        final var stocktaking = stocktakingRepository.findById(stocktakingId).orElseThrow();
        stocktaking.getId();
        return stocktakingEntriesRepository.save(new StocktakingEntry()
                        .setProductOid(entry.getProductOid())
                        .setQuantity(entry.getQuantity())
                        .setStocktakingId(stocktakingId));
    }
}
