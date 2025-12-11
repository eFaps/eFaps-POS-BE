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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.efaps.pos.listener.IMonitoringReportContributor;
import org.efaps.pos.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OrderContributor
    implements IMonitoringReportContributor
{

    private static final Logger LOG = LoggerFactory.getLogger(OrderContributor.class);

    private final OrderRepository orderRepository;

    public OrderContributor(final OrderRepository orderRepository)
    {
        this.orderRepository = orderRepository;
    }

    @Override
    public Map<String, Object> add2Report(final LocalDateTime startDate,
                                          final LocalDateTime endDateTime)
    {
        final var map = new HashMap<String, Object>();
        final var info = new HashMap<String, Object>();
        map.put("orders", info);
        LOG.debug("Adding to MonitoringReport for Order");
        final var orders = orderRepository.findByCreatedDateBetween(startDate, endDateTime);
        info.put("created", orders.size());
        return map;
    }

}
