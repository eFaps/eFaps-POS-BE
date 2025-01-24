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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.efaps.pos.config.ConfigProperties.Company;
import org.efaps.pos.context.Context;
import org.efaps.pos.dto.CollectStartOrderDto;
import org.efaps.pos.dto.CollectStartResponseDto;
import org.efaps.pos.dto.CollectorDto;
import org.efaps.pos.dto.PaymentAbstractDto;
import org.efaps.pos.entity.CollectOrder;
import org.efaps.pos.entity.CollectOrder.State;
import org.efaps.pos.entity.Order;
import org.efaps.pos.listener.ICollectorListener;
import org.efaps.pos.pojo.Collector;
import org.efaps.pos.pojo.CollectorState;
import org.efaps.pos.pojo.IPayment;
import org.efaps.pos.repository.CollectOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CollectorService
{

    private static final Logger LOG = LoggerFactory.getLogger(CollectorService.class);

    private final Executor executer = Executors.newFixedThreadPool(5);

    private final LogService logService;
    private final List<ICollectorListener> collectorListener;
    private final CollectOrderRepository collectOrderRepository;
    private final WebSocketService webSocketService;

    public static Map<String, CollectorState> CACHE = Collections
                    .synchronizedMap(new PassiveExpiringMap<>(10, TimeUnit.MINUTES));

    public CollectorService(final LogService logService,
                            final Optional<List<ICollectorListener>> _collectorListener,
                            final CollectOrderRepository _collectOrderRepository,
                            final WebSocketService _webSocketService)
    {
        this.logService = logService;
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

    public CollectStartResponseDto startCollect(final String key,
                                                final CollectStartOrderDto dto)
    {
        String collectOrderId = null;
        final var responseDetails = new HashMap<String, Object>();
        final Optional<CollectorDto> collectorOpt = getCollectors().stream()
                        .filter(collectorDto -> key.equals(collectorDto.getKey()))
                        .findFirst();
        if (collectorOpt.isPresent()) {
            final CollectorDto collector = collectorOpt.get();
            CollectOrder collectOrder = new CollectOrder()
                            .setState(State.PENDING)
                            .setAmount(dto.getAmount())
                            .setCurrency(dto.getCurrency())
                            .setOrderId(dto.getOrderId())
                            .setCollector(new Collector()
                                            .setKey(collector.getKey())
                                            .setLabel(collector.getLabel()));
            collectOrder = collectOrderRepository.save(collectOrder);
            collectOrderId = collectOrder.getId();

            if (initCollectors(dto, responseDetails, collectOrderId, collector.getKey())) {
                final var collectorState = new CollectorState(collectOrderId);
                collectorState.setState(State.PENDING);
                CACHE.put(collectOrderId, collectorState);
                final Company company = Context.get().getCompany();
                final var authentication = SecurityContextHolder.getContext().getAuthentication();
                for (final ICollectorListener listener : collectorListener) {
                    executer.execute(() -> {
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        Context.get().setCompany(company);
                        try {
                            listener.collect(collectorState, dto.getDetails());
                        } catch (final Exception e) {
                            if (e instanceof CollectorException) {
                                logService.error((CollectorException) e);
                                LOG.error("CollectorException during collection of order with id: "
                                                + collectorState.getCollectOrderId(), e);
                            } else {
                                logService.error(new CollectorException("Catched unexpected Exception", e));
                                LOG.error("Catched CollectorException during collection of order with id: "
                                                + collectorState.getCollectOrderId(), e);
                            }
                            collectorState.setState(State.INVALID);
                            final var collectOrderOpt = getCollectOrder(collectorState.getCollectOrderId());
                            if (collectOrderOpt.isPresent()) {
                                final var intCollectOrder = collectOrderOpt.get();
                                intCollectOrder.setState(State.INVALID);
                                collectOrderRepository.save(intCollectOrder);
                            }
                        }
                    });
                }
                runSocketService(collectorState);
            }
        }
        return CollectStartResponseDto.builder()
                        .withCollectOrderId(collectOrderId)
                        .withDetails(responseDetails)
                        .build();
    }

    private void runSocketService(final CollectorState collectorState)
    {
        final Company company = Context.get().getCompany();
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        executer.execute(() -> {
            try {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                Context.get().setCompany(company);
                var max = 0;
                var overhang = 0;
                var collecting = true;
                while (max < 1000 && overhang < 5) {
                    Thread.sleep(1000);
                    webSocketService.notifyCollectOrderState(collectorState);
                    if (collecting == true && !State.PENDING.equals(collectorState.getState())) {
                        collecting = false;
                        final var storedState = collectOrderRepository.findById(collectorState.getCollectOrderId())
                                        .get().getState();
                        if (storedState != collectorState.getState()) {
                            LOG.debug("Different states for collectOrder {}", collectorState.getCollectOrderId());
                        }
                    }
                    if (!collecting) {
                        overhang++;
                    }
                    max++;
                }
            } catch (final InterruptedException e) {
                LOG.error("Catched Exception running Socket Service for collectOrder with id: "
                                + collectorState.getCollectOrderId(), e);
            }
        });
    }

    private boolean initCollectors(final CollectStartOrderDto dto,
                                   final Map<String, Object> details,
                                   final String collectOrderId,
                                   final String collectorKey)
    {
        boolean ret = false;
        try {
            for (final ICollectorListener listener : collectorListener) {
                final var value = listener.init(dto, collectOrderId);
                if (value != null) {
                    details.put(collectorKey, value);
                }
            }
            ret = true;
        } catch (final CollectorException e) {
            logService.error(e);
            LOG.error("Catched CollectorException on init of collectOrder with id: " + collectOrderId, e);
        } catch (final Exception e) {
            logService.error(new CollectorException("Catched Exception", e));
            LOG.error("Catched CollectorException on init of collectOrder with id: " + collectOrderId, e);
        }
        return ret;
    }

    public Optional<CollectOrder> getCollectOrder(final String _collectOrderId)
    {
        return collectOrderRepository.findById(_collectOrderId);
    }

    public List<CollectOrder> getCollectOrderByOrder(final Order _order)
    {
        return collectOrderRepository.findByOrderId(_order.getId());
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

    public void add2PaymentDto(final PaymentAbstractDto.Builder<?> builder,
                               final IPayment payment)
    {
        for (final ICollectorListener listener : collectorListener) {
            listener.add2PaymentDto(builder, payment);
        }
    }
}
