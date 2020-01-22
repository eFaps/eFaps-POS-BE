/*
 * Copyright 2003 - 2020 The eFaps Team
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.efaps.pos.ConfigProperties.Company;
import org.efaps.pos.context.Context;
import org.efaps.pos.dto.CollectorDto;
import org.efaps.pos.entity.CollectOrder;
import org.efaps.pos.entity.CollectOrder.State;
import org.efaps.pos.entity.Collector;
import org.efaps.pos.repository.CollectOrderRepository;
import org.springframework.stereotype.Service;

@Service
public class CollectorService
{
    private final Executor executer = Executors.newFixedThreadPool(5);

    private final List<ICollectorListener> collectorListener;
    private final CollectOrderRepository collectOrderRepository;
    private final WebSocketService webSocketService;

    public CollectorService(final Optional<List<ICollectorListener>> _collectorListener,
                            final CollectOrderRepository _collectOrderRepository,
                            final WebSocketService _webSocketService)
    {
        collectorListener = _collectorListener.isPresent() ? _collectorListener.get() : Collections.emptyList();
        collectOrderRepository = _collectOrderRepository;
        webSocketService = _webSocketService;
    }

    public List<CollectorDto> getCollectors()
    {
        final List<CollectorDto> collectors = new ArrayList<>();
        for (final ICollectorListener listener : collectorListener) {
            collectors.addAll(listener.getCollectors());
        }
        return collectors;
    }

    public String startCollect(final String _key,
                               final BigDecimal _amount) {
        String ret = null;
        final Optional<CollectorDto> collectorOpt = getCollectors().stream()
                        .filter(collectorDto -> _key.equals(collectorDto.getKey()))
                        .findFirst();
        if (collectorOpt.isPresent()) {
            final CollectorDto collector = collectorOpt.get();
            CollectOrder collectOrder = new CollectOrder()
                            .setState(State.PENDING)
                            .setAmount(_amount)
                            .setCollector(new Collector()
                                            .setKey(collector.getKey())
                                            .setLabel(collector.getLabel()));
            collectOrder = collectOrderRepository.save(collectOrder);
            ret = collectOrder.getId();
            final String collectOrderId = ret;
            final Company company = Context.get().getCompany();
            for (final ICollectorListener listener : collectorListener) {
                executer.execute(() -> {
                    Context.get().setCompany(company);
                    listener.collect(collectOrderId);
                });
            }
            executer.execute(() -> {
                Context.get().setCompany(company);
                int max = 0;
                int overhang = 0;
                while (max < 1000 && overhang < 5) {
                    try {
                        Thread.sleep(2000);
                    } catch (final InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    final State state = collectOrderRepository.findById(collectOrderId).get().getState();
                    webSocketService.notifyCollectOrderState(collectOrderId, state);
                    if (!State.PENDING.equals(state)) {
                        overhang++;
                    }
                    max++;
                }
            });
        }
        return ret;
    }

    public Optional<CollectOrder> getCollectOrder(final String _collectOrderId)
    {
        return collectOrderRepository.findById(_collectOrderId);
    }

    public Optional<CollectOrder> cancelCollectOrder(final String _collectOrderId)
    {
        final var collectOrderOpt = getCollectOrder(_collectOrderId);
        if (collectOrderOpt.isPresent()) {
            collectOrderOpt.get().setState(State.CANCELED);
        }
        return collectOrderOpt;
    }
}
