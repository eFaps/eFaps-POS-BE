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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.dto.PromoInfoDto;
import org.efaps.pos.dto.PromoInfoSyncDto;
import org.efaps.pos.entity.PromotionEntity;
import org.efaps.pos.entity.PromotionInfo;
import org.efaps.pos.repository.PromotionInfoRepository;
import org.efaps.pos.repository.PromotionRepository;
import org.efaps.pos.util.Converter;
import org.efaps.pos.util.PromotionNotFoundException;
import org.efaps.pos.util.Utils;
import org.efaps.promotionengine.dto.PromotionInfoDto;
import org.efaps.promotionengine.promotion.Promotion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import tools.jackson.databind.ObjectMapper;

@Service
public class PromotionService
{

    private static final Logger LOG = LoggerFactory.getLogger(PromotionService.class);

    private final ObjectMapper objectMapper;
    private final PromotionRepository promotionRepository;
    private final PromotionInfoRepository promotionInfoRepository;
    private final EFapsClient eFapsClient;
    private final ConfigService configService;
    private final DocumentHelperService documentHelperService;

    @Value("${org.quartz.jobs.syncPromotionInfos.batchSize:100}")
    private Integer syncPromotionsBatchSize;

    public PromotionService(final ObjectMapper objectMapper,
                            final PromotionRepository promotionRepository,
                            final PromotionInfoRepository promotionInfoRepository,
                            final EFapsClient eFapsClient,
                            final ConfigService configService,
                            final DocumentHelperService documentHelperService)
    {
        this.objectMapper = objectMapper;
        this.promotionRepository = promotionRepository;
        this.promotionInfoRepository = promotionInfoRepository;
        this.eFapsClient = eFapsClient;
        this.configService = configService;
        this.documentHelperService = documentHelperService;
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
        promoInfo.getDetails().forEach(detail -> {
            promotionOids.add(detail.getPromotionOid());
        });
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

    public boolean syncPromotions()
    {
        final var active = BooleanUtils.toBoolean(configService.getSystemConfig(ConfigService.PROMOTIONS_ACTIVATE));
        if (active) {
            LOG.info("Syncing Promotions");
            final var promotions = eFapsClient.getPromotions();
            final List<PromotionEntity> existingPromotions = promotionRepository.findAll();
            existingPromotions.forEach(existing -> {
                if (!promotions.stream()
                                .filter(promotion -> promotion.getOid().equals(existing.getOid())).findFirst()
                                .isPresent()) {
                    promotionRepository.delete(existing);
                }
            });
            promotions.forEach(promotion -> promotionRepository.save(Converter.toEntity(promotion)));
        }
        return active;
    }

    public boolean syncPromotionInfos()
    {
        final var active = BooleanUtils.toBoolean(configService.getSystemConfig(ConfigService.PROMOTIONS_ACTIVATE));
        if (active) {
            LOG.info("Syncing promotionInfos with batchSize: {}", syncPromotionsBatchSize);
            var slice = promotionInfoRepository.findByOidIsNull(PageRequest.of(0, syncPromotionsBatchSize));
            syncPromotionInfos(slice.getContent());
            while (slice.hasNext()) {
                LOG.info("  page: {}", slice.nextPageable());
                slice = promotionInfoRepository.findByOidIsNull(slice.nextPageable());
                syncPromotionInfos(slice.getContent());
            }

        }
        return active;
    }

    private void syncPromotionInfos(Collection<PromotionInfo> infos)
    {
        for (final var infoToBeSynced : infos) {
            LOG.debug("Syncing promotionInfo {}", infoToBeSynced);
            final var doc = documentHelperService.getDocument(infoToBeSynced.getDocumentId());
            if (doc.isPresent() && doc.get().getOid() != null) {
                final List<String> promotionStr = new ArrayList<>();
                final List<String> promotionOids = new ArrayList<>();
                for (final var promotionEntity : infoToBeSynced.getPromotions()) {
                    promotionOids.add(promotionEntity.getOid());
                    final var promotion = Converter.toDto(promotionEntity);
                    promotionStr.add(objectMapper.writeValueAsString(promotion));
                }

                final var dto = PromoInfoSyncDto.builder()
                                .withDocumentOid(doc.get().getOid())
                                .withPromoInfo(infoToBeSynced.getPromoInfo())
                                .withPromotions(promotionStr)
                                .build();
                final var oid = eFapsClient.postPromotionInfo(dto);
                if (Utils.isOid(oid)) {
                    infoToBeSynced.setOid(oid);
                    promotionInfoRepository.save(infoToBeSynced);
                }
            }
        }
    }
}
