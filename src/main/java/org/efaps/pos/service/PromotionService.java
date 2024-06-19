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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.efaps.pos.dto.PromoInfoDto;
import org.efaps.pos.entity.PromotionEntity;
import org.efaps.pos.entity.PromotionInfo;
import org.efaps.pos.repository.PromotionInfoRepository;
import org.efaps.pos.repository.PromotionRepository;
import org.efaps.pos.util.Converter;
import org.efaps.pos.util.PromotionNotFoundException;
import org.efaps.promotionengine.api.IPromotionDetail;
import org.efaps.promotionengine.dto.PromotionInfoDto;
import org.efaps.promotionengine.promotion.Promotion;
import org.springframework.stereotype.Service;

@Service
public class PromotionService
{

    private final PromotionRepository promotionRepository;
    private final PromotionInfoRepository promotionInfoRepository;

    public PromotionService(final PromotionRepository promotionRepository,
                            final PromotionInfoRepository promotionInfoRepository)
    {
        this.promotionRepository = promotionRepository;
        this.promotionInfoRepository = promotionInfoRepository;
    }

    protected List<Promotion> getPromotions()
    {
        return promotionRepository.findAll().stream().map(Converter::toDto).collect(Collectors.toList());
    }

    public PromotionEntity getPromotion(final String promotionOid)
        throws PromotionNotFoundException
    {
        return promotionRepository.findById(promotionOid).orElseThrow(PromotionNotFoundException::new);
    }

    public void registerInfo(final String documentId,
                             final PromotionInfoDto promoInfo)
    {
        final var infoEntityOpt = promotionInfoRepository.findOneByDocumentId(documentId);
        if (infoEntityOpt.isPresent()) {
            if (promoInfo == null) {
                promotionInfoRepository.delete(infoEntityOpt.get());
            } else {
                promotionInfoRepository.save(infoEntityOpt.get()
                                .setPromoInfo(Converter.toDto(promoInfo))
                                .setPromotions(getPromotions(promoInfo)));
            }
        } else if (promoInfo != null) {
            promotionInfoRepository.save(new PromotionInfo()
                            .setDocumentId(documentId)
                            .setPromoInfo(Converter.toDto(promoInfo))
                            .setPromotions(getPromotions(promoInfo)));
        }
    }

    private List<PromotionEntity> getPromotions(final PromotionInfoDto promoInfo)
    {
        final Set<String> promotionOids = new HashSet<>();
        promotionOids.addAll(promoInfo.getPromotionOids());
        promotionOids.addAll(promoInfo.getDetails().stream().map(IPromotionDetail::getPromotionOid).toList());
        return promotionRepository.findAllById(promotionOids);
    }

    public PromoInfoDto getPromotionInfoForDocument(final String documentId)
    {
        PromoInfoDto ret = null;
        final var infoEntityOpt = promotionInfoRepository.findOneByDocumentId(documentId);
        if (infoEntityOpt.isPresent()) {
            ret = infoEntityOpt.get().getPromoInfo();
        }
        return ret;
    }

    public void copyPromotionInfo(final String sourceDocumentId,
                                  final String targetDocumentId)
    {
        final var infoEntityOpt = promotionInfoRepository.findOneByDocumentId(sourceDocumentId);
        if (infoEntityOpt.isPresent()) {
            final var info = infoEntityOpt.get();
            promotionInfoRepository.save(new PromotionInfo()
                            .setDocumentId(targetDocumentId)
                            .setPromoInfo(info.getPromoInfo())
                            .setPromotions(info.getPromotions()));
        }
    }

}
