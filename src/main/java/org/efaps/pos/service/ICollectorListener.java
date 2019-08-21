package org.efaps.pos.service;

import java.util.List;

import org.efaps.pos.dto.CollectorDto;

public interface ICollectorListener
{
   List<CollectorDto> getCollectors();
}
