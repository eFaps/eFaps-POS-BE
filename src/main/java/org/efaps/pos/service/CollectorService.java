package org.efaps.pos.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.efaps.pos.dto.CollectorDto;
import org.springframework.stereotype.Service;

@Service
public class CollectorService
{

    private final List<ICollectorListener> collectorListener;

    public CollectorService(final Optional<List<ICollectorListener>> _collectorListener)
    {
        collectorListener = _collectorListener.isPresent() ? _collectorListener.get() : Collections.emptyList();
    }

    public List<CollectorDto> getCollectors()
    {
        final List<CollectorDto> collectors = new ArrayList<>();
        for (final ICollectorListener listener : collectorListener) {
            collectors.addAll(listener.getCollectors());
        }
        return collectors;
    }

}
