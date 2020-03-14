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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.efaps.pos.ConfigProperties.Company;
import org.efaps.pos.context.Context;
import org.efaps.pos.dto.CollectStartOrderDto;
import org.efaps.pos.dto.CollectorDto;
import org.efaps.pos.entity.CollectOrder;
import org.efaps.pos.entity.CollectOrder.State;
import org.efaps.pos.entity.Collector;
import org.efaps.pos.pojo.CollectorState;
import org.efaps.pos.repository.CollectOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CollectorService
{
    private static final Logger LOG = LoggerFactory.getLogger(CollectorService.class);

    private final Executor executer = Executors.newFixedThreadPool(5);

    private final List<ICollectorListener> collectorListener;
    private final CollectOrderRepository collectOrderRepository;
    private final WebSocketService webSocketService;

    public static Map<String,CollectorState> CACHE = Collections.synchronizedMap(new PassiveExpiringMap<>(10, TimeUnit.MINUTES));

    public CollectorService(final Optional<List<ICollectorListener>> _collectorListener,
                            final CollectOrderRepository _collectOrderRepository,
                            final WebSocketService _webSocketService)
    {
        collectorListener = _collectorListener.isPresent() ? _collectorListener.get() : Collections.emptyList();
        LOG.info("Discovered {} ICollectorListener", collectorListener.size());
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
                               final CollectStartOrderDto _dto) {
        String ret = null;
        final Optional<CollectorDto> collectorOpt = getCollectors().stream()
                        .filter(collectorDto -> _key.equals(collectorDto.getKey()))
                        .findFirst();
        if (collectorOpt.isPresent()) {
            final CollectorDto collector = collectorOpt.get();
            CollectOrder collectOrder = new CollectOrder()
                            .setState(State.PENDING)
                            .setAmount(_dto.getAmount())
                            .setCollector(new Collector()
                                            .setKey(collector.getKey())
                                            .setLabel(collector.getLabel()));
            collectOrder = collectOrderRepository.save(collectOrder);
            ret = collectOrder.getId();
            final var collectorState = new CollectorState(ret);
            collectorState.setState(State.PENDING);
            CACHE.put(ret, collectorState);
            final Company company = Context.get().getCompany();
            for (final ICollectorListener listener : collectorListener) {
                executer.execute(() -> {
                    Context.get().setCompany(company);
                    listener.collect(collectorState, _dto.getDetails());
                });
            }
            executer.execute(() -> {
                Context.get().setCompany(company);
                var max = 0;
                var overhang = 0;
                var collecting = true;
                while (max < 1000 && overhang < 5) {
                    try {
                        Thread.sleep(1000);
                    } catch (final InterruptedException e) {
                        LOG.error("Catched", e);
                    }
                    webSocketService.notifyCollectOrderState(collectorState);
                    if (collecting == true && !State.PENDING.equals(collectorState.getState())) {
                        collecting = false;
                        final var storedState = collectOrderRepository.findById(collectorState.getCollectOrderId()).get().getState();
                        if (storedState != collectorState.getState()) {
                            LOG.debug("Different states for collectOrder {}", collectorState.getCollectOrderId());
                        }
                    }
                    if (!collecting) {
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
        var collectOrderOpt = getCollectOrder(_collectOrderId);
        if (collectOrderOpt.isPresent()) {
            final var collectOrder = collectOrderOpt.get();
            collectOrder.setState(State.CANCELED);
            collectOrderOpt = Optional.of(collectOrderRepository.save(collectOrder));
        }
        getCollectorState(_collectOrderId).ifPresent(collectorState -> collectorState.setState(State.CANCELED));
        return collectOrderOpt;
    }

    public Optional<CollectorState> getCollectorState(final String _collectOrderId)
    {
        return Optional.ofNullable(CACHE.get(_collectOrderId));
    }
}
