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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.EnumUtils;
import org.efaps.abacus.api.ICalcDocument;
import org.efaps.abacus.api.ICalcPosition;
import org.efaps.abacus.api.ITax;
import org.efaps.abacus.api.TaxType;
import org.efaps.abacus.pojo.Configuration;
import org.efaps.abacus.pojo.Tax;
import org.efaps.pos.dto.BOMActionType;
import org.efaps.pos.dto.CalculatorPositionRequestDto;
import org.efaps.pos.dto.CalculatorPositionResponseDto;
import org.efaps.pos.dto.CalculatorRequestDto;
import org.efaps.pos.dto.CalculatorResponseDto;
import org.efaps.pos.dto.LoyaltyInfoDto;
import org.efaps.pos.dto.ProductType;
import org.efaps.pos.dto.PromoDetailDto;
import org.efaps.pos.dto.PromoInfoDto;
import org.efaps.pos.dto.TaxEntryDto;
import org.efaps.pos.entity.AbstractDocument;
import org.efaps.pos.entity.AbstractDocument.Item;
import org.efaps.pos.entity.AbstractDocument.TaxEntry;
import org.efaps.pos.entity.Identifier;
import org.efaps.pos.entity.Order;
import org.efaps.pos.entity.Product;
import org.efaps.pos.flags.BOMGroupConfigFlag;
import org.efaps.pos.flags.WorkspaceFlag;
import org.efaps.pos.util.Converter;
import org.efaps.pos.util.Utils;
import org.efaps.promotionengine.Calculator;
import org.efaps.promotionengine.PromotionsConfiguration;
import org.efaps.promotionengine.api.IDocument;
import org.efaps.promotionengine.api.IPromotionInfo;
import org.efaps.promotionengine.condition.StoreCondition;
import org.efaps.promotionengine.dto.PromotionInfoDto;
import org.efaps.promotionengine.pojo.Document;
import org.efaps.promotionengine.pojo.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.lang.Collections;

@Service
public class CalculatorService
{

    private static final Logger LOG = LoggerFactory.getLogger(CalculatorService.class);
    private final MongoTemplate mongoTemplate;
    private final ConfigService configService;
    private final WorkspaceService workspaceService;
    private final ProductService productService;
    private final DocumentHelperService documentHelperService;
    private final PromotionService promotionService;
    private final LoyaltyService loyaltyService;

    public CalculatorService(final MongoTemplate mongoTemplate,
                             final ConfigService configService,
                             final WorkspaceService workspaceService,
                             final ProductService productService,
                             final DocumentHelperService documentHelperService,
                             final PromotionService promotionService,
                             final LoyaltyService loyaltyService)
    {
        this.mongoTemplate = mongoTemplate;
        this.configService = configService;
        this.workspaceService = workspaceService;
        this.productService = productService;
        this.documentHelperService = documentHelperService;
        this.promotionService = promotionService;
        this.loyaltyService = loyaltyService;
    }

    public CalculatorResponseDto calculate(final String workspaceOid,
                                           final CalculatorRequestDto calculatorPayloadDto)
    {
        final var document = new Document();
        int i = 0;
        final var taxMap = new HashMap<String, org.efaps.pos.pojo.Tax>();

        final boolean usesIndex = calculatorPayloadDto.getPositions().stream()
                        .anyMatch(pos -> (pos.getIndex() != null));
        if (usesIndex) {
            i = i + 1000;
        }

        // in case of a new creditnote --> calculation is based on the source
        // document
        if (calculatorPayloadDto.getSourceDocIdent() != null) {
            final var sourceDocOpt = documentHelperService.getDocument(calculatorPayloadDto.getSourceDocIdent());
            if (sourceDocOpt.isPresent()) {
                final var sourceDoc = sourceDocOpt.get();
                for (final var pos : calculatorPayloadDto.getPositions()) {
                    final var itemOpt = sourceDoc.getItems().stream()
                                    .filter(fitem -> fitem.getIndex().equals(pos.getIndex())).findFirst();
                    if (itemOpt.isPresent()) {
                        final var item = itemOpt.get();
                        document.addPosition(new Position()
                                        .setNetUnitPrice(item.getNetUnitPrice())
                                        .setTaxes(item.getTaxes().stream()
                                                        .map(TaxEntry::getTax)
                                                        .map(tax -> {
                                                            taxMap.put(tax.getKey(), tax);
                                                            return toAbacusTax(tax);
                                                        })
                                                        .toList())
                                        .setIndex(item.getIndex())
                                        .setProductOid(pos.getProductOid())
                                        .setQuantity(pos.getQuantity()));
                    }
                }
            }
        } else {
            for (final var pos : calculatorPayloadDto.getPositions()) {
                final var product = productService.getProduct(pos.getProductOid());
                final var taxes = product.getTaxes().stream().map(tax -> {
                    taxMap.put(tax.getKey(), tax);
                    return toAbacusTax(tax);
                }).toList();
                final BigDecimal netUnitPrice = evalPrice(pos, product);
                var index = 0;
                if (usesIndex) {
                    if (pos.getIndex() == null) {
                        LOG.warn("Mixed index for calculator", pos.getBomOid());
                        index = i++;
                    } else {
                        index = pos.getIndex();
                    }
                } else {
                    index = i++;
                }
                final var baseProduct = productService.evalBaseProduct(product);

                document.addPosition(new Position()
                                .setNetUnitPrice(netUnitPrice)
                                .setTaxes(taxes)
                                .setIndex(index)
                                .setProductOid(baseProduct.getOid())
                                .setStandInOid(baseProduct.getOid().equals(pos.getProductOid()) ? null
                                                : pos.getProductOid())
                                .setQuantity(pos.getQuantity()));
            }
        }
        final IDocument result;
        if (CollectionUtils.isEmpty(calculatorPayloadDto.getPositions())) {
            result = new EmptyDoc();
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
                                                        .withIndex(evalIndex(usesIndex, pos))
                                                        .withParentIdx(evalParentIndex(usesIndex, pos,
                                                                        calculatorPayloadDto))
                                                        .withQuantity(pos.getQuantity())
                                                        .withProductOid(pos.getStandInOid() != null
                                                                        ? pos.getStandInOid()
                                                                        : pos.getProductOid())
                                                        .withNetUnitPrice(pos.getNetUnitPrice())
                                                        .withNetPrice(pos.getNetPrice())
                                                        .withCrossUnitPrice(pos.getCrossUnitPrice())
                                                        .withCrossPrice(pos.getCrossPrice())
                                                        .withTaxAmount(pos.getTaxAmount())
                                                        .withTaxes(toDto(taxMap, pos.getTaxes()))
                                                        .withBomOid(evalBomOid(usesIndex, pos, calculatorPayloadDto))
                                                        .build())
                                        .toList())
                        .withPromotionInfo(getPromoInfo(result))
                        .withLoyaltyInfos(evalLoyalty(calculatorPayloadDto, result))
                        .build();
    }

    private List<LoyaltyInfoDto> evalLoyalty(final CalculatorRequestDto calculatorPayloadDto,
                                             final IDocument result)
    {
        List<LoyaltyInfoDto> ret;
        if (calculatorPayloadDto.isEvaluateLoyalty()) {
            ret = loyaltyService.evalLoyaltyInfos(calculatorPayloadDto.getContactOid(), result);
        } else {
            ret = Collections.emptyList();
        }
        return ret;
    }

    private BigDecimal evalPrice(CalculatorPositionRequestDto pos,
                                 Product product)
    {
        BigDecimal netUnitPrice = BigDecimal.ZERO;
        if (pos.getBomOid() != null) {
            final var partList = productService.getProductByBomOid(pos.getBomOid());
            if (!ProductType.PARTLIST.equals(partList.getType())) {
                LOG.warn("BomOid {} send to Calculator leads to a product that is not a partlist", pos.getBomOid());
            }
            final var bomOpt = partList.getConfigurationBOMs().stream()
                            .filter(bom -> bom.getOid().equals(pos.getBomOid()))
                            .findFirst();
            if (bomOpt.isPresent()) {
                final var bom = bomOpt.get();
                final var priceAdjustment = bom.getActions().stream()
                                .filter(action -> BOMActionType.PRICEADJUSTMENT.equals(action.getType()))
                                .findFirst();
                if (priceAdjustment.isPresent()) {
                    netUnitPrice = priceAdjustment.get().getNetAmount();
                } else {
                    final var confOpt = partList.getBomGroupConfigs().stream()
                                    .filter(conf -> conf.getOid().equals(bom.getBomGroupOid()))
                                    .findFirst();
                    if (confOpt.isPresent()
                                    && Utils.hasFlag(confOpt.get().getFlags(), BOMGroupConfigFlag.CHARGEABLE)) {
                        netUnitPrice = productService.evalPrices(product).getLeft();
                    }
                }

            } else {
                LOG.warn("BomOid {} could not be find", pos.getBomOid());
            }
        } else {
            netUnitPrice = productService.evalPrices(product).getLeft();
        }
        return netUnitPrice;
    }

    private Integer evalIndex(boolean usesIndex,
                              ICalcPosition pos)
    {
        Integer ret = null;
        if (usesIndex) {
            ret = pos.getIndex();
        }
        return ret;
    }

    private Integer evalParentIndex(boolean usesIndex,
                                    ICalcPosition pos,
                                    final CalculatorRequestDto calculatorPayloadDto)
    {
        Integer ret = null;
        if (usesIndex) {
            final var reqPos = calculatorPayloadDto.getPositions().stream()
                            .filter(p -> p.getIndex().equals(pos.getIndex()))
                            .findFirst();
            if (reqPos.isPresent()) {
                ret = reqPos.get().getParentIdx();
            }
        }
        return ret;
    }

    private String evalBomOid(boolean usesIndex,
                              ICalcPosition pos,
                              final CalculatorRequestDto calculatorPayloadDto)
    {
        String ret = null;
        if (usesIndex) {
            final var reqPos = calculatorPayloadDto.getPositions().stream()
                            .filter(p -> p.getIndex().equals(pos.getIndex())).findFirst();
            if (reqPos.isPresent()) {
                ret = reqPos.get().getBomOid();
            }
        }
        return ret;
    }

    public PromoInfoDto getPromoInfo(final IDocument result)
    {
        PromoInfoDto ret = null;
        if (result != null && result instanceof Document) {
            final var info = ((Document) result).getPromotionInfo();
            if (info != null) {
                ret = PromoInfoDto.builder()
                                .withNetTotalDiscount(info.getNetTotalDiscount())
                                .withCrossTotalDiscount(info.getCrossTotalDiscount())
                                .withPromotionOids(info.getPromotionOids())
                                .withDetails(info.getDetails().stream()
                                                .map(pos -> PromoDetailDto.builder()
                                                                .withPositionIndex(pos.getPositionIndex())
                                                                .withNetBase(pos.getNetBase())
                                                                .withNetUnitBase(pos.getNetUnitBase())
                                                                .withNetDiscount(pos.getNetDiscount())
                                                                .withNetUnitDiscount(pos.getNetUnitDiscount())
                                                                .withNetDiscount(pos.getNetDiscount())
                                                                .withCrossUnitDiscount(pos.getCrossUnitDiscount())
                                                                .withCrossDiscount(pos.getCrossDiscount())
                                                                .withPromotionOid(pos.getPromotionOid())
                                                                .build())
                                                .toList())
                                .build();
            }
        }
        return ret;
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

    public PromotionInfoDto calculate(final String workspaceOid,
                                      final AbstractDocument<?> posDoc)
    {
        if (posDoc.getItems().isEmpty()) {
            posDoc.setNetTotal(BigDecimal.ZERO);
            posDoc.setCrossTotal(BigDecimal.ZERO);
            posDoc.setPayableAmount(BigDecimal.ZERO);
            posDoc.setTaxes(Collections.emptySet());
            return null;
        }
        final var sortedItems = posDoc.getItems();
        sortedItems.sort(Comparator.comparing(Item::getIndex));

        final var calcDocument = new Document();
        final var taxMap = new HashMap<String, org.efaps.pos.pojo.Tax>();
        for (final var item : sortedItems) {
            final var product = productService.getProduct(item.getProductOid());
            final var taxes = product.getTaxes().stream().map(tax -> {
                taxMap.put(tax.getKey(), tax);
                return (ITax) new Tax()
                                .setKey(tax.getKey())
                                .setPercentage(tax.getPercent())
                                .setAmount(tax.getAmount())
                                .setType(EnumUtils.getEnum(TaxType.class, tax.getType().name()))
                                .setFreeOfCharge(isFreeOfCharge(tax.getKey()));
            }).toList();
            BigDecimal netUnitPrice = BigDecimal.ZERO;
            // use the send netUnitPrice if is allowed
            if (item.getNetUnitPrice() != null && posDoc instanceof Order
                            && configService.getInstProperties().getOrder().isAllowSetUnitPrice()) {
                netUnitPrice = item.getNetUnitPrice();
            } else if (item.getBomOid() != null) {
                final var partList = productService.getProductByBomOid(item.getBomOid());
                if (!ProductType.PARTLIST.equals(partList.getType())) {
                    LOG.warn("BomOid {} send to Calculator leads to a product that is not a partlist",
                                    item.getBomOid());
                }
                final var bomOpt = partList.getConfigurationBOMs().stream()
                                .filter(bom -> bom.getOid().equals(item.getBomOid()))
                                .findFirst();
                if (bomOpt.isPresent()) {
                    final var bom = bomOpt.get();
                    final var priceAdjustment = bom.getActions().stream()
                                    .filter(action -> BOMActionType.PRICEADJUSTMENT.equals(action.getType()))
                                    .findFirst();
                    if (priceAdjustment.isPresent()) {
                        netUnitPrice = priceAdjustment.get().getNetAmount();
                    } else {
                        final var confOpt = partList.getBomGroupConfigs().stream()
                                        .filter(conf -> conf.getOid().equals(bom.getBomGroupOid()))
                                        .findFirst();
                        if (confOpt.isPresent()
                                        && Utils.hasFlag(confOpt.get().getFlags(), BOMGroupConfigFlag.CHARGEABLE)) {
                            netUnitPrice = productService.evalPrices(product).getLeft();
                        }
                    }
                } else {
                    LOG.warn("BomOid {} could not be find", item.getBomOid());
                }
            } else {
                netUnitPrice = productService.evalPrices(product).getLeft();
            }
            final var baseProduct = productService.evalBaseProduct(product);

            calcDocument.addPosition(new Position()
                            .setNetUnitPrice(netUnitPrice)
                            .setTaxes(taxes)
                            .setIndex(item.getIndex())
                            .setProductOid(baseProduct.getOid())
                            .setStandInOid(baseProduct.getOid().equals(item.getProductOid()) ? null
                                            : item.getProductOid())
                            .setQuantity(item.getQuantity()));
        }
        // apply discount selected in the UI
        applyDiscount(posDoc, calcDocument);

        calculate(calcDocument);

        for (final var item : sortedItems) {
            final var calculatedPositionOpt = calcDocument.getPositions().stream()
                            .filter(pos -> (pos.getIndex() == item.getIndex()))
                            .findFirst();

            if (calculatedPositionOpt.isEmpty()) {
                LOG.error("There is something wrong with the index");
            } else {
                final var product = productService
                                .getProduct(item.getStandInOid() != null ? item.getStandInOid() : item.getProductOid());
                final var calculatedPosition = calculatedPositionOpt.get();
                item.setNetUnitPrice(calculatedPosition.getNetUnitPrice());
                item.setNetPrice(calculatedPosition.getNetPrice());
                item.setCrossPrice(calculatedPosition.getCrossPrice());
                item.setCrossUnitPrice(calculatedPosition.getCrossUnitPrice());

                final var taxes = product.getTaxes().stream().map(tax -> {
                    final var calcTax = calculatedPosition.getTaxes().stream()
                                    .filter(posTax -> posTax.getKey().equals(tax.getKey())).findFirst().get();
                    return new TaxEntry()
                                    .setAmount(calcTax.getAmount())
                                    .setBase(calcTax.getBase())
                                    .setCurrency(posDoc.getCurrency())
                                    .setTax(tax);
                }).collect(Collectors.toSet());
                item.setTaxes(taxes);
            }
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
        return (PromotionInfoDto) calcDocument.getPromotionInfo();
    }

    private void applyDiscount(final AbstractDocument<?> posDoc,
                               final Document calcDocument)
    {
        if (posDoc.getDiscount() != null) {
            final var discountPositionOpt = calcDocument.getPositions().stream()
                            .filter(pos -> pos.getProductOid().equals(posDoc.getDiscount().getProductOid()))
                            .findFirst();
            if (discountPositionOpt.isPresent()) {
                final var discountPosition = discountPositionOpt.get();
                // calculate first the normal positions
                final var normalPositions = new ArrayList<ICalcPosition>();
                normalPositions.addAll(calcDocument.getPositions().stream()
                                .filter(pos -> !pos.equals(discountPosition))
                                .toList());
                calcDocument.setPositions(normalPositions);
                calculate(calcDocument);

                final var posNetUnitPrice = switch (posDoc.getDiscount().getType()) {
                    case PERCENT -> {
                        final var factor = posDoc.getDiscount().getValue().setScale(4, RoundingMode.HALF_UP)
                                        .divide(new BigDecimal(100), RoundingMode.HALF_UP);
                        yield calcDocument.getNetTotal().multiply(factor);
                    }
                    case AMOUNT -> {
                        final var targetCross = calcDocument.getCrossTotal().subtract(posDoc.getDiscount().getValue());
                        final var percentage = new BigDecimal(100).setScale(4).multiply(
                                        BigDecimal.ONE.subtract(
                                                        targetCross.setScale(4).divide(calcDocument.getCrossTotal(),
                                                                        RoundingMode.HALF_UP)));
                        LOG.debug("percentage: {}", percentage);
                        final var factor2 = percentage.setScale(4, RoundingMode.HALF_UP)
                                        .divide(new BigDecimal(100), RoundingMode.HALF_UP);
                        yield calcDocument.getNetTotal().multiply(factor2);
                    }
                };
                discountPosition.setNetUnitPrice(posNetUnitPrice.negate());
                calcDocument.addPosition(discountPosition);
            }
        }
    }

    private ITax toAbacusTax(org.efaps.pos.pojo.Tax tax)
    {
        return new Tax()
                        .setKey(tax.getKey())
                        .setPercentage(tax.getPercent())
                        .setAmount(tax.getAmount())
                        .setType(EnumUtils.getEnum(TaxType.class, tax.getType().name()))
                        .setFreeOfCharge(isFreeOfCharge(tax.getKey()));
    }

    private boolean isFreeOfCharge(final String taxUuid)
    {
        final var taxmap = configService.getTaxMapping();
        return BooleanUtils.toBoolean(taxmap.getOrDefault("tax." + taxUuid + ".freeOfCharge", "false"));
    }

    public static class EmptyDoc
        implements IDocument
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

        @Override
        public ICalcDocument updateWith(ICalcDocument position)
        {
            return null;
        }

        @Override
        public void setPromotionInfo(IPromotionInfo info)
        {
        }

        @Override
        public void addPromotionOid(String oid)
        {
        }

        @Override
        public List<String> getPromotionOids()
        {
            return null;
        }
    }

}
