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

import org.efaps.pos.client.EnquiryClient;
import org.efaps.pos.dto.DNIDto;
import org.efaps.pos.dto.RUCDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class EnquiryService
{

    private final EnquiryClient enquiryClient;

    public EnquiryService(final EnquiryClient enquiryClient)
    {
        this.enquiryClient = enquiryClient;
    }

    public DNIDto getDNI(final String number)
    {
        return enquiryClient.getDNI(number);
    }

    public RUCDto getRUC(final String number)
    {
        return enquiryClient.getRUC(number);
    }

    public Page<RUCDto> findRUCs(final Pageable pageable,
                                 final String term)
    {
        return enquiryClient.findRUCs(pageable, term);
    }
}
