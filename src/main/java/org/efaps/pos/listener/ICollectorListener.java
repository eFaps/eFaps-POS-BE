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
package org.efaps.pos.listener;

import java.util.List;
import java.util.Map;

import org.efaps.pos.dto.CollectStartOrderDto;
import org.efaps.pos.dto.CollectorDto;
import org.efaps.pos.dto.PaymentDto;
import org.efaps.pos.entity.CollectOrder;
import org.efaps.pos.pojo.CollectorState;
import org.efaps.pos.pojo.Payment;
import org.efaps.pos.service.CollectorException;

public interface ICollectorListener
{

    List<CollectorDto> getCollectors();

    void collect(final CollectorState collectorState,
                 final Map<String, Object> details)
        throws CollectorException;

    default Object init(final CollectStartOrderDto dto,
                        final String collectOrderId)
        throws CollectorException
    {
        return null;
    }

    default void addAdditionalInfo2CollectOrderDto(final CollectOrder collectOrder,
                                                   final Map<String, Object> additionalInfo)
    {
    }

    default void add2PaymentDto(final PaymentDto.Builder builder,
                                final Payment payment)
    {
    }

}
