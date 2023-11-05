/*
 * Copyright 2003 - 2023 The eFaps Team
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
 *
 */

package org.efaps.pos.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.EnumUtils;
import org.efaps.abacus.api.ITax;
import org.efaps.abacus.api.TaxType;
import org.efaps.abacus.pojo.Configuration;
import org.efaps.abacus.pojo.Tax;
import org.efaps.pos.dto.CalculatorPositionResponseDto;
import org.efaps.pos.dto.CalculatorRequestDto;
import org.efaps.pos.dto.CalculatorResponseDto;
import org.efaps.pos.dto.TaxEntryDto;
import org.efaps.pos.dto.WorkspaceFlag;
import org.efaps.pos.util.Converter;
import org.efaps.pos.util.Utils;
import org.efaps.promotionengine.Calculator;
import org.efaps.promotionengine.api.IDocument;
import org.efaps.promotionengine.pojo.Document;
import org.efaps.promotionengine.pojo.Position;
import org.springframework.stereotype.Service;

@Service
public class CalculatorService
{
    private final WorkspaceService workspaceService;
    private final ProductService productService;
    private final PromotionService promotionService;

    public CalculatorService(final WorkspaceService workspaceService,
                             final ProductService productService,
                             final PromotionService promotionService)
    {
        this.workspaceService = workspaceService;
        this.productService = productService;
        this.promotionService = promotionService;
    }

    public CalculatorResponseDto calculate(final String workspaceOid,
                                           final CalculatorRequestDto calculatorPayloadDto)
    {

        final var document = new Document();
        int i = 0;
        final var taxMap = new HashMap<String, org.efaps.pos.pojo.Tax>();

        for (final var pos : calculatorPayloadDto.getPositions()) {
            final var product = productService.getProduct(pos.getProductOid());
            final var taxes = product.getTaxes().stream().map(tax -> {
                taxMap.put(tax.getKey(), tax);
                return (ITax) new Tax()
                                .setKey(tax.getKey())
                                .setPercentage(tax.getPercent())
                                .setAmount(tax.getAmount())
                                .setType(EnumUtils.getEnum(TaxType.class, tax.getType().name()));
            }).toList();
            document.addPosition(new Position()
                            .setNetUnitPrice(product.getNetPrice())
                            .setTaxes(taxes)
                            .setIndex(i++)
                            .setProductOid(pos.getProductOid())
                            .setQuantity(pos.getQuantity()));
        }
        final var result = calculate(document);
        return CalculatorResponseDto.builder()
                        .withNetTotal(result.getNetTotal())
                        .withTaxTotal(result.getTaxTotal())
                        .withCrossTotal(result.getCrossTotal())
                        .withPayableAmount(getPayableAmount(workspaceOid, result.getCrossTotal()))
                        .withTaxes(toDto(taxMap, result.getTaxes()))
                        .withPositions(result.getPositions().stream()
                                        .map(pos -> CalculatorPositionResponseDto.builder()
                                                        .withQuantity(pos.getQuantity())
                                                        .withProductOid(pos.getProductOid())
                                                        .withNetUnitPrice(pos.getNetUnitPrice())
                                                        .withNetPrice(pos.getNetPrice())
                                                        .withCrossPrice(pos.getCrossPrice())
                                                        .withTaxAmount(pos.getTaxAmount())
                                                        .withTaxes(toDto(taxMap, pos.getTaxes()))
                                                        .build())
                                        .toList())
                        .build();
    }

    public IDocument calculate(final IDocument document)
    {
        final var calculator = new Calculator(getConfig());
        calculator.calc(document, promotionService.getPromotions());
        return document;
    }

    protected BigDecimal getPayableAmount(final String workspaceOid, final BigDecimal total)
    {
        BigDecimal ret = total;
        final var workspace = workspaceService.getWorkspace(workspaceOid);
        if (Utils.hasFlag(workspace.getFlags(), WorkspaceFlag.ROUNDPAYABLE)) {
            ret = total.setScale(1, RoundingMode.FLOOR);
        }
        return ret;
    }

    protected Configuration getConfig()
    {
        return new Configuration();
    }

    protected List<TaxEntryDto> toDto(final Map<String, org.efaps.pos.pojo.Tax> taxMap,
                                      final List<ITax> list)
    {
        return list.stream().map(itax -> toDto(taxMap, itax)).toList();
    }

    protected TaxEntryDto toDto(final Map<String, org.efaps.pos.pojo.Tax> taxMap,
                                final ITax tax)
    {
        return TaxEntryDto.builder()
                        .withAmount(tax.getAmount())
                        .withBase(tax.getBase())
                        .withTax(Converter.toDto(taxMap.get(tax.getKey())))
                        .build();
    }
}
