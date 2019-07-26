/*
 * Copyright 2003 - 2019 The eFaps Team
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

import org.efaps.pos.dto.BalanceStatus;
import org.efaps.pos.dto.BalanceSummaryDto;
import org.efaps.pos.entity.Balance;
import org.efaps.pos.entity.User;
import org.efaps.pos.repository.BalanceRepository;
import org.efaps.pos.util.Converter;
import org.springframework.stereotype.Service;

@Service
public class BalanceService
{
    private final BalanceRepository repository;
    private final SequenceService sequenceService;

    public BalanceService(final BalanceRepository _repository, final SequenceService _sequenceService) {
        repository = _repository;
        sequenceService = _sequenceService;
    }

    public Optional<Balance> getCurrent(final User _principal, final boolean _createNew)
    {
        final Optional<Balance> ret;
        final Optional<Balance> balanceOpt = repository.findOneByUserOidAndStatus(_principal.getOid(),
                        BalanceStatus.OPEN);
        if (!balanceOpt.isPresent() && _createNew) {
            final String number = sequenceService.getNextNumber("Balance", false);
            final Balance balance = new Balance()
                            .setStartAt(LocalDateTime.now())
                            .setUserOid(_principal.getOid())
                            .setStatus(BalanceStatus.OPEN)
                            .setNumber(number);
            ret = Optional.of(repository.save(balance));
        } else {
            ret = balanceOpt;
        }
        return ret;
    }

    public Balance update(final Balance _retrievedBalance) {
        Balance ret = _retrievedBalance;
        final Optional<Balance> balanceOpt = repository.findById(_retrievedBalance.getId());
        if (balanceOpt.isPresent()) {
            final Balance balance = balanceOpt.get();
            balance.setStatus(BalanceStatus.CLOSED)
                .setEndAt(LocalDateTime.now())
                .setSynced(false);
            ret = repository.save(balance);
        }
        return ret;
    }

    public BalanceSummaryDto getSummary(final String _balanceId) {
        final Balance balance = repository.findById(_balanceId).orElseThrow();

        return BalanceSummaryDto.builder()
                        .withBalance(Converter.toDto(balance))
                        .build();
    }

}
