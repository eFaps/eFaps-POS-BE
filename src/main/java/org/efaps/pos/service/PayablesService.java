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
package org.efaps.pos.service;

import java.util.Optional;

import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.config.ConfigProperties;
import org.efaps.pos.entity.AbstractPayableDocument;
import org.efaps.pos.entity.Balance;
import org.efaps.pos.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PayablesService
    extends DocumentsService
{

    private static final Logger LOG = LoggerFactory.getLogger(PayablesService.class);

    private final BalanceService balanceService;

    public PayablesService(final ConfigProperties configProperties,
                           final EFapsClient eFapsClient,
                           final DocumentHelperService documentHelperService,
                           final ContactService contactService,
                           final BalanceService balanceService)
    {
        super(configProperties, eFapsClient, documentHelperService, contactService);
        this.balanceService = balanceService;
    }

    protected boolean verifyBalance(final AbstractPayableDocument<?> document)
    {
        boolean ret = true;
        if (!Utils.isOid(document.getBalanceOid())) {
            ret = false;
            final Optional<Balance> balanceOpt = balanceService.findById(document.getBalanceOid());
            if (balanceOpt.isPresent()) {
                final Balance balance = balanceOpt.get();
                if (Utils.isOid(balance.getOid())) {
                    document.setBalanceOid(balance.getOid());
                    ret = true;
                } else {
                    LOG.error("The found Balance does not have an OID {}", balance);
                }
            }
        }
        return ret;
    }
}
