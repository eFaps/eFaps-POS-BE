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

import java.util.List;

import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.dto.LoyaltyPointsBalanceDto;
import org.springframework.stereotype.Service;

@Service
public class LoyaltyService
{

    private final EFapsClient eFapsClient;

    public LoyaltyService(final EFapsClient eFapsClient)
    {
        this.eFapsClient = eFapsClient;
    }

    public List<LoyaltyPointsBalanceDto> findBalance4Contact(final String contactIdent,
                                                             final Boolean includeContact)
    {
        return eFapsClient.retrieveLoyaltyBalance(contactIdent, includeContact);
    }

}
