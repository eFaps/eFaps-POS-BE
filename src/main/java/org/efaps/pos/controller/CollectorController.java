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
package org.efaps.pos.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.efaps.pos.config.IApi;
import org.efaps.pos.dto.CollectOrderDto;
import org.efaps.pos.dto.CollectStartOrderDto;
import org.efaps.pos.dto.CollectStartResponseDto;
import org.efaps.pos.dto.CollectorDto;
import org.efaps.pos.listener.ICollectorListener;
import org.efaps.pos.service.CollectorService;
import org.efaps.pos.util.Converter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(IApi.BASEPATH + "collectors")
public class CollectorController
{

    private final List<ICollectorListener> collectorListener;
    private final CollectorService collectorService;

    public CollectorController(final Optional<List<ICollectorListener>> _collectorListener,
                               final CollectorService _collectorService)
    {
        collectorListener = _collectorListener.isPresent() ? _collectorListener.get() : Collections.emptyList();
        collectorService = _collectorService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CollectorDto> getCollecters()
    {
        return collectorService.getCollectors();
    }

    @PostMapping(path = "{key}/start", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public CollectStartResponseDto startCollect(@PathVariable("key") final String _key,
                                                @RequestBody final CollectStartOrderDto _dto)
    {
        return collectorService.startCollect(_key, _dto);
    }

    @GetMapping(path = "orders/{collectOrderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CollectOrderDto getCollectOrder(@PathVariable(name = "collectOrderId") final String _collectOrderId)
    {
        CollectOrderDto dto = null;
        final var collectOrderOpt = collectorService.getCollectOrder(_collectOrderId);
        if (collectOrderOpt.isPresent()) {
            final var collectOrder = collectOrderOpt.get();
            final var map = new HashMap<String, Object>();
            for (final ICollectorListener listener : collectorListener) {
                listener.addAdditionalInfo2CollectOrderDto(collectOrder, map);
            }
            dto = Converter.toDto(collectOrder, map);
        }
        return dto;
    }

    @PostMapping(path = "orders/{collectOrderId}/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
    public CollectOrderDto cancelCollectOrder(@PathVariable(name = "collectOrderId") final String _collectOrderId)
    {
        return Converter.toDto(collectorService.cancelCollectOrder(_collectOrderId).orElse(null));
    }
}
