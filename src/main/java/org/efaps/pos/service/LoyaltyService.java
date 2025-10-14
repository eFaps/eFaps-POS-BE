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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.BooleanUtils;
import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.dto.LoyaltyInfoDto;
import org.efaps.pos.dto.LoyaltyPointsBalanceDto;
import org.efaps.pos.listener.ILoyaltyListener;
import org.efaps.promotionengine.api.IDocument;
import org.springframework.stereotype.Service;

@Service
public class LoyaltyService
{

    private final EFapsClient eFapsClient;
    private final ConfigService configService;
    private final List<ILoyaltyListener> loyaltyListeners;

    public LoyaltyService(final EFapsClient eFapsClient,
                          final ConfigService configService,
                          final Optional<List<ILoyaltyListener>> loyaltyListeners)
    {
        this.eFapsClient = eFapsClient;
        this.configService = configService;
        this.loyaltyListeners = loyaltyListeners.isPresent() ? loyaltyListeners.get() : Collections.emptyList();
    }

    public List<LoyaltyPointsBalanceDto> findBalance4Contact(final String contactIdent,
                                                             final Boolean includeContact)
    {
        return eFapsClient.retrieveLoyaltyBalance(contactIdent, includeContact);
    }

    public List<LoyaltyInfoDto> evalLoyaltyInfos(final String contactOid,
                                                 final IDocument document)
    {
        final List<LoyaltyInfoDto> ret = new ArrayList<>();
        final var active = BooleanUtils.toBoolean(configService.getSystemConfig(ConfigService.PROMOTIONS_ACTIVATE));
        if (active) {
            for (final ILoyaltyListener listener : loyaltyListeners) {
                ret.addAll(listener.evalLoyaltyInfos(contactOid, document));
            }
        }
        return ret;
    }

}
