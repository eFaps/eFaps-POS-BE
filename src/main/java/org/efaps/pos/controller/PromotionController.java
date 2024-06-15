package org.efaps.pos.controller;

import org.efaps.pos.config.IApi;
import org.efaps.pos.service.PromotionService;
import org.efaps.pos.util.Converter;
import org.efaps.pos.util.PromotionNotFoundException;
import org.efaps.promotionengine.promotion.Promotion;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public Promotion getPromotion(@PathVariable(name = "promotionOid") final String _productOid)
        throws PromotionNotFoundException
    {
        return Converter.toDto(promotionService.getPromotion(_productOid));
    }
}
