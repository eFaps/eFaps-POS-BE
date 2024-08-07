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

import org.efaps.pos.config.IApi;
import org.efaps.pos.dto.PromoInfoDto;
import org.efaps.pos.dto.PromotionHeaderDto;
import org.efaps.pos.service.PromotionService;
import org.efaps.pos.util.Converter;
import org.efaps.pos.util.PromotionNotFoundException;
import org.efaps.promotionengine.promotion.Promotion;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(IApi.BASEPATH + "promotions")
public class PromotionController
{

    private final PromotionService promotionService;

    public PromotionController(final PromotionService promotionService)
    {
        this.promotionService = promotionService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "{promotionOid}")
    public Promotion getPromotion(@PathVariable(name = "promotionOid") final String promotionOid)
        throws PromotionNotFoundException
    {
        return Converter.toDto(promotionService.getPromotion(promotionOid));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "{promotionOid}", params = {"headerOnly"})
    public PromotionHeaderDto getPromotionHeader(@PathVariable(name = "promotionOid") final String promotionOid)
        throws PromotionNotFoundException
    {
        return Converter.toHeaderDto(promotionService.getPromotion(promotionOid));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/info")
    public PromoInfoDto getPromotionInfoForDocument(@RequestParam(name = "documentId") final String documentId)
        throws PromotionNotFoundException
    {
        return promotionService.getPromotionInfoForDocument(documentId);
    }
}
