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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.EnumUtils;
import org.efaps.abacus.api.ICalcPosition;
import org.efaps.abacus.api.ITax;
import org.efaps.abacus.api.TaxType;
import org.efaps.abacus.pojo.Configuration;
import org.efaps.abacus.pojo.Tax;
import org.efaps.pos.dto.CalculatorPositionResponseDto;
import org.efaps.pos.dto.CalculatorRequestDto;
import org.efaps.pos.dto.CalculatorResponseDto;
import org.efaps.pos.dto.TaxEntryDto;
import org.efaps.pos.dto.WorkspaceFlag;
import org.efaps.pos.entity.AbstractDocument;
import org.efaps.pos.entity.AbstractDocument.TaxEntry;
import org.efaps.pos.entity.Identifier;
import org.efaps.pos.util.Converter;
import org.efaps.pos.util.Utils;
import org.efaps.promotionengine.Calculator;
import org.efaps.promotionengine.PromotionsConfiguration;
import org.efaps.promotionengine.api.IDocument;
import org.efaps.promotionengine.condition.StoreCondition;
import org.efaps.promotionengine.pojo.Document;
import org.efaps.promotionengine.pojo.Position;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.lang.Collections;

@Service
public class CalculatorService
{

    private final MongoTemplate mongoTemplate;
    private final ConfigService configService;
    private final WorkspaceService workspaceService;
    private final ProductService productService;
    private final PromotionService promotionService;

    public CalculatorService(final MongoTemplate mongoTemplate,
                             final ConfigService configService,
                             final WorkspaceService workspaceService,
                             final ProductService productService,
                             final PromotionService promotionService)
    {
        this.mongoTemplate = mongoTemplate;
        this.configService = configService;
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
                            .setNetUnitPrice(productService.evalPrices(product).getLeft())
                            .setTaxes(taxes)
                            .setIndex(i++)
                            .setProductOid(pos.getProductOid())
                            .setQuantity(pos.getQuantity()));
        }
        final IDocument result;
        if (CollectionUtils.isEmpty(calculatorPayloadDto.getPositions())) {
            result = new IDocument()
            {

                @Override
                public Collection<ICalcPosition> getPositions()
                {
                    return Collections.emptyList();
                }

                @Override
                public BigDecimal getNetTotal()
                {
                    return BigDecimal.ZERO;
                }

                @Override
                public void setNetTotal(BigDecimal netTotal)
                {
                }

                @Override
                public BigDecimal getTaxTotal()
                {
                    return BigDecimal.ZERO;
                }

                @Override
                public void setTaxTotal(BigDecimal taxTotal)
                {
                }

                @Override
                public List<ITax> getTaxes()
                {
                    return Collections.emptyList();
                }

                @Override
                public void setTaxes(List<ITax> taxes)
                {
                }

                @Override
                public BigDecimal getCrossTotal()
                {
                    return BigDecimal.ZERO;
                }

                @Override
                public void setCrossTotal(BigDecimal crossTotal)
                {
                }

                @Override
                public IDocument clone()
                {
                    return null;
                }

                @Override
                public void addDocDiscount(BigDecimal discount)
                {
                }

                @Override
                public BigDecimal getDocDiscount()
                {
                    return null;
                }

            };
        } else {
            result = calculate(document);
        }
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
                                                        .withCrossUnitPrice(pos.getCrossUnitPrice())
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
        calculator.calc(document, promotionService.getPromotions(), evalData(), getPromotionsConfig());
        return document;
    }

    protected BigDecimal getPayableAmount(final String workspaceOid,
                                          final BigDecimal total)
    {
        BigDecimal ret = total;
        final var workspace = workspaceService.getWorkspace(workspaceOid);
        if (workspace != null && Utils.hasFlag(workspace.getFlags(), WorkspaceFlag.ROUNDPAYABLE)) {
            ret = total.setScale(1, RoundingMode.FLOOR);
        }
        return ret;
    }

    protected Configuration getConfig()
    {
        return configService.getCalculatorConfig();
    }

    protected PromotionsConfiguration getPromotionsConfig()
    {
        return configService.getPromotionsConfig();
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

    protected Map<String, Object> evalData()
    {
        final Map<String, Object> map = new HashMap<>();
        final Identifier identifier = mongoTemplate.findById(Identifier.KEY, Identifier.class);
        if (identifier != null) {
            map.put(StoreCondition.KEY, identifier.getIdentifier());
        }
        return map;
    }

    public void calculate(final String workspaceOid,
                          final AbstractDocument<?> posDoc)
    {
        final var calcDocument = new Document();
        int i = 0;
        final var taxMap = new HashMap<String, org.efaps.pos.pojo.Tax>();
        for (final var item : posDoc.getItems()) {
            final var product = productService.getProduct(item.getProductOid());
            final var taxes = product.getTaxes().stream().map(tax -> {
                taxMap.put(tax.getKey(), tax);
                return (ITax) new Tax()
                                .setKey(tax.getKey())
                                .setPercentage(tax.getPercent())
                                .setAmount(tax.getAmount())
                                .setType(EnumUtils.getEnum(TaxType.class, tax.getType().name()));
            }).toList();
            calcDocument.addPosition(new Position()
                            .setNetUnitPrice(product.getNetPrice())
                            .setTaxes(taxes)
                            .setIndex(i++)
                            .setProductOid(item.getProductOid())
                            .setQuantity(item.getQuantity()));
        }
        calculate(calcDocument);

        final var calcPosIter = calcDocument.getPositions().iterator();
        for (final var item : posDoc.getItems()) {
            final var product = productService.getProduct(item.getProductOid());
            final var calcPos = calcPosIter.next();
            item.setNetUnitPrice(calcPos.getNetUnitPrice());
            item.setNetPrice(calcPos.getNetPrice());
            item.setCrossPrice(calcPos.getCrossPrice());
            item.setCrossUnitPrice(calcPos.getCrossUnitPrice());

            final var taxes = product.getTaxes().stream().map(tax -> {
                final var calcTax = calcPos.getTaxes().stream()
                                .filter(posTax -> posTax.getKey().equals(tax.getKey())).findFirst().get();

                return new TaxEntry()
                                .setAmount(calcTax.getAmount())
                                .setBase(calcTax.getBase())
                                .setCurrency(posDoc.getCurrency())
                                .setTax(tax);
            }).collect(Collectors.toSet());
            item.setTaxes(taxes);
        }
        posDoc.setNetTotal(calcDocument.getNetTotal());
        posDoc.setCrossTotal(calcDocument.getCrossTotal());
        posDoc.setPayableAmount(getPayableAmount(workspaceOid, calcDocument.getCrossTotal()));

        final var taxes = new HashSet<TaxEntry>();
        for (final var entry : taxMap.entrySet()) {
            final var calcTax = calcDocument.getTaxes().stream()
                            .filter(posTax -> posTax.getKey().equals(entry.getKey())).findFirst().get();

            taxes.add(new TaxEntry()
                            .setAmount(calcTax.getAmount())
                            .setBase(calcTax.getBase())
                            .setCurrency(posDoc.getCurrency())
                            .setTax(entry.getValue()));
        }
        posDoc.setTaxes(taxes);
    }
}
