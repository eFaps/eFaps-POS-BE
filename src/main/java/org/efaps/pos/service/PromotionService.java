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

import java.util.List;
import java.util.stream.Collectors;

import org.efaps.pos.repository.PromotionRepository;
import org.efaps.pos.util.Converter;
import org.efaps.promotionengine.promotion.Promotion;
import org.springframework.stereotype.Service;

@Service
public class PromotionService
{

    private final PromotionRepository promotionRepository;

    public PromotionService(final PromotionRepository promotionRepository)
    {
        this.promotionRepository = promotionRepository;
    }

    protected List<Promotion> getPromotions()
    {
        return promotionRepository.findAll().stream().map(Converter::toDto).collect(Collectors.toList());
    }

}
