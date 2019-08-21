package org.efaps.pos.service;

import java.math.BigDecimal;
import java.util.List;

import org.efaps.pos.dto.CollectorDto;

public interface ICollectorListener
{

    List<CollectorDto> getCollectors();

    void collect(String _key, BigDecimal _amount);
}
