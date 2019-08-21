package org.efaps.pos.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.efaps.pos.dto.CollectOrderDto;
import org.efaps.pos.dto.CollectorDto;
import org.efaps.pos.entity.CollectOrder;
import org.efaps.pos.entity.Collector;
import org.efaps.pos.repository.CollectOrderRepository;
import org.efaps.pos.util.Converter;
import org.springframework.stereotype.Service;

@Service
public class CollectorService
{

    private final List<ICollectorListener> collectorListener;
    private final CollectOrderRepository collectOrderRepository;

    public CollectorService(final Optional<List<ICollectorListener>> _collectorListener,
                            final CollectOrderRepository _collectOrderRepository)
    {
        collectorListener = _collectorListener.isPresent() ? _collectorListener.get() : Collections.emptyList();
        collectOrderRepository = _collectOrderRepository;
    }

    public List<CollectorDto> getCollectors()
    {
        final List<CollectorDto> collectors = new ArrayList<>();
        for (final ICollectorListener listener : collectorListener) {
            collectors.addAll(listener.getCollectors());
        }
        return collectors;
    }

    public CollectOrderDto collect(final String _key,
                                   final CollectOrderDto _collectOrderDto)
    {
        CollectOrderDto ret = null;
        final Optional<CollectorDto> collectorOpt = getCollectors().stream()
            .filter(collectorDto -> _key.equals(collectorDto.getKey()))
            .findFirst();
        if (collectorOpt.isPresent()) {
            final CollectorDto collector = collectorOpt.get();

            CollectOrder collectOrder = new CollectOrder()
                            .setAmount(_collectOrderDto.getAmount())
                            .setCollector(new Collector()
                                            .setKey(collector.getKey())
                                            .setLabel(collector.getLabel()));
            collectOrder = collectOrderRepository.save(collectOrder);
            ret = Converter.toDto(collectOrder);

            for (final ICollectorListener listener : collectorListener) {
                listener.collect(_key, _collectOrderDto.getAmount());
            }
        } else {
            // what to do now??
        }
        return ret;
    }

}
