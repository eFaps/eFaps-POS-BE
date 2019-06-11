/*
 * Copyright 2003 - 2018 The eFaps Team
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
import org.efaps.pos.entity.Balance;
import org.efaps.pos.entity.User;
import org.efaps.pos.repository.BalanceRepository;
import org.springframework.stereotype.Service;

@Service
public class BalanceService
{
    private final BalanceRepository repository;
    private final SequenceService sequenceService;

    public BalanceService(final BalanceRepository _repository, final SequenceService _sequenceService) {
        this.repository = _repository;
        this.sequenceService = _sequenceService;
    }

    public Optional<Balance> getCurrent(final User _principal, final boolean _createNew)
    {
        final Optional<Balance> ret;
        final Optional<Balance> balanceOpt = this.repository.findOneByUserOidAndStatus(_principal.getOid(),
                        BalanceStatus.OPEN);
        if (!balanceOpt.isPresent() && _createNew) {
            final String number = this.sequenceService.getNextNumber("Balance", false);
            final Balance balance = new Balance()
                            .setStartAt(LocalDateTime.now())
                            .setUserOid(_principal.getOid())
                            .setStatus(BalanceStatus.OPEN)
                            .setNumber(number);
            ret = Optional.of(this.repository.save(balance));
        } else {
            ret = balanceOpt;
        }
        return ret;
    }

    public Balance update(final Balance _retrievedBalance) {
        Balance ret = _retrievedBalance;
        final Optional<Balance> balanceOpt = this.repository.findById(_retrievedBalance.getId());
        if (balanceOpt.isPresent()) {
            final Balance balance = balanceOpt.get();
            balance.setStatus(BalanceStatus.CLOSED)
                .setEndAt(LocalDateTime.now())
                .setSynced(false);
            ret = this.repository.save(balance);
        }
        return ret;
    }
}
