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

import org.efaps.pos.client.TaxpayerRegistryClient;
import org.efaps.pos.dto.TaxpayerDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TaxpayerService
{

    private final TaxpayerRegistryClient taxpayerRegistryClient;

    public TaxpayerService(final TaxpayerRegistryClient _taxpayerRegistryClient)
    {
        taxpayerRegistryClient = _taxpayerRegistryClient;
    }

    public TaxpayerDto get(final String _id)
    {
        return taxpayerRegistryClient.getTaxpayer(_id);
    }

    public Page<TaxpayerDto> find(final Pageable _pageable, final String _term)
    {
        return taxpayerRegistryClient.findTaxpayer(_pageable, _term);
    }

}
