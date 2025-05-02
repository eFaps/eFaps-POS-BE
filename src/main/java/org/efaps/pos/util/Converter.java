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
package org.efaps.pos.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.efaps.pos.config.ConfigProperties;
import org.efaps.pos.dto.AbstractDocItemDto;
import org.efaps.pos.dto.AbstractDocumentDto;
import org.efaps.pos.dto.AbstractPayableDocumentDto;
import org.efaps.pos.dto.BOMActionDto;
import org.efaps.pos.dto.BOMGroupConfigDto;
import org.efaps.pos.dto.BalanceDto;
import org.efaps.pos.dto.BarcodeDto;
import org.efaps.pos.dto.CardDto;
import org.efaps.pos.dto.CashEntryDto;
import org.efaps.pos.dto.CategoryDto;
import org.efaps.pos.dto.CollectOrderDto;
import org.efaps.pos.dto.ConfigurationBOMDto;
import org.efaps.pos.dto.ContactDto;
import org.efaps.pos.dto.CreditNoteDto;
import org.efaps.pos.dto.DiscountDto;
import org.efaps.pos.dto.DocItemDto;
import org.efaps.pos.dto.DocumentHeadDto;
import org.efaps.pos.dto.DocumentHeadDto.Builder;
import org.efaps.pos.dto.EmployeeDto;
import org.efaps.pos.dto.EmployeeRelationDto;
import org.efaps.pos.dto.FileDto;
import org.efaps.pos.dto.FloorDto;
import org.efaps.pos.dto.IPaymentDto;
import org.efaps.pos.dto.IPosPaymentDto;
import org.efaps.pos.dto.IndicationDto;
import org.efaps.pos.dto.IndicationSetDto;
import org.efaps.pos.dto.InventoryEntryDto;
import org.efaps.pos.dto.InvoiceDto;
import org.efaps.pos.dto.JobDto;
import org.efaps.pos.dto.LogEntryDto;
import org.efaps.pos.dto.OrderDto;
import org.efaps.pos.dto.PayableHeadDto;
import org.efaps.pos.dto.PaymentAbstractDto;
import org.efaps.pos.dto.PaymentCardDto;
import org.efaps.pos.dto.PaymentCashDto;
import org.efaps.pos.dto.PaymentChangeDto;
import org.efaps.pos.dto.PaymentElectronicAbstractDto;
import org.efaps.pos.dto.PaymentElectronicDto;
import org.efaps.pos.dto.PaymentFreeDto;
import org.efaps.pos.dto.PaymentLoyaltyPointsAbstractDto;
import org.efaps.pos.dto.PaymentLoyaltyPointsDto;
import org.efaps.pos.dto.PosBalanceDto;
import org.efaps.pos.dto.PosCreditNoteDto;
import org.efaps.pos.dto.PosDocItemDto;
import org.efaps.pos.dto.PosDto;
import org.efaps.pos.dto.PosFileDto;
import org.efaps.pos.dto.PosInventoryEntryDto;
import org.efaps.pos.dto.PosInvoiceDto;
import org.efaps.pos.dto.PosOrderDto;
import org.efaps.pos.dto.PosPaymentCardDto;
import org.efaps.pos.dto.PosPaymentCashDto;
import org.efaps.pos.dto.PosPaymentChangeDto;
import org.efaps.pos.dto.PosPaymentElectronicDto;
import org.efaps.pos.dto.PosPaymentFreeDto;
import org.efaps.pos.dto.PosPaymentLoyaltyPointsDto;
import org.efaps.pos.dto.PosReceiptDto;
import org.efaps.pos.dto.PosSpotDto;
import org.efaps.pos.dto.PosStocktakingDto;
import org.efaps.pos.dto.PosTicketDto;
import org.efaps.pos.dto.PosUserDto;
import org.efaps.pos.dto.PrintCmdDto;
import org.efaps.pos.dto.PrintPosOrderDto;
import org.efaps.pos.dto.PrinterDto;
import org.efaps.pos.dto.Product2CategoryDto;
import org.efaps.pos.dto.ProductDto;
import org.efaps.pos.dto.ProductHeadDto;
import org.efaps.pos.dto.ProductRelationDto;
import org.efaps.pos.dto.PromoDetailDto;
import org.efaps.pos.dto.PromoInfoDto;
import org.efaps.pos.dto.PromotionHeaderDto;
import org.efaps.pos.dto.ReceiptDto;
import org.efaps.pos.dto.SequenceDto;
import org.efaps.pos.dto.SpotDto;
import org.efaps.pos.dto.StockTakingEntryDto;
import org.efaps.pos.dto.TaxDto;
import org.efaps.pos.dto.TaxEntryDto;
import org.efaps.pos.dto.TicketDto;
import org.efaps.pos.dto.UserDto;
import org.efaps.pos.dto.WarehouseDto;
import org.efaps.pos.dto.WorkspaceDto;
import org.efaps.pos.entity.AbstractDocument;
import org.efaps.pos.entity.AbstractDocument.Item;
import org.efaps.pos.entity.AbstractDocument.TaxEntry;
import org.efaps.pos.entity.AbstractPayableDocument;
import org.efaps.pos.entity.Balance;
import org.efaps.pos.entity.CashEntry;
import org.efaps.pos.entity.Category;
import org.efaps.pos.entity.CollectOrder;
import org.efaps.pos.entity.Contact;
import org.efaps.pos.entity.CreditNote;
import org.efaps.pos.entity.Employee;
import org.efaps.pos.entity.InventoryEntry;
import org.efaps.pos.entity.Invoice;
import org.efaps.pos.entity.Job;
import org.efaps.pos.entity.LogEntry;
import org.efaps.pos.entity.Order;
import org.efaps.pos.entity.Pos;
import org.efaps.pos.entity.PosFile;
import org.efaps.pos.entity.Printer;
import org.efaps.pos.entity.Product;
import org.efaps.pos.entity.PromotionEntity;
import org.efaps.pos.entity.Receipt;
import org.efaps.pos.entity.Sequence;
import org.efaps.pos.entity.Stocktaking;
import org.efaps.pos.entity.StocktakingEntry;
import org.efaps.pos.entity.Ticket;
import org.efaps.pos.entity.User;
import org.efaps.pos.entity.Warehouse;
import org.efaps.pos.entity.Workspace;
import org.efaps.pos.entity.Workspace.Card;
import org.efaps.pos.entity.Workspace.Floor;
import org.efaps.pos.entity.Workspace.PrintCmd;
import org.efaps.pos.pojo.AbstractPayment;
import org.efaps.pos.pojo.BOMAction;
import org.efaps.pos.pojo.BOMGroupConfig;
import org.efaps.pos.pojo.Barcode;
import org.efaps.pos.pojo.ConfigurationBOM;
import org.efaps.pos.pojo.Discount;
import org.efaps.pos.pojo.EmployeeRelation;
import org.efaps.pos.pojo.IPayment;
import org.efaps.pos.pojo.Indication;
import org.efaps.pos.pojo.IndicationSet;
import org.efaps.pos.pojo.Payment;
import org.efaps.pos.pojo.PaymentCard;
import org.efaps.pos.pojo.PaymentCash;
import org.efaps.pos.pojo.PaymentChange;
import org.efaps.pos.pojo.PaymentElectronic;
import org.efaps.pos.pojo.PaymentFree;
import org.efaps.pos.pojo.PaymentLoyaltyPoints;
import org.efaps.pos.pojo.Product2Category;
import org.efaps.pos.pojo.ProductRelation;
import org.efaps.pos.pojo.Spot;
import org.efaps.pos.pojo.Tax;
import org.efaps.pos.projection.DocumentHead;
import org.efaps.pos.projection.PayableHead;
import org.efaps.pos.service.CollectorService;
import org.efaps.pos.service.ContactService;
import org.efaps.pos.service.DocumentService;
import org.efaps.pos.service.InventoryService;
import org.efaps.pos.service.PosFileService;
import org.efaps.pos.service.ProductService;
import org.efaps.pos.service.UserService;
import org.efaps.promotionengine.api.IPromotionDetail;
import org.efaps.promotionengine.dto.PromotionInfoDto;
import org.efaps.promotionengine.promotion.Promotion;
import org.springframework.stereotype.Component;

@Component
public final class Converter
{

    private static Converter INSTANCE;

    private final ConfigProperties configProperties;
    private final ProductService productService;
    private final InventoryService inventoryService;
    private final DocumentService documentService;
    private final ContactService contactService;
    private final UserService userService;
    private final CollectorService collectorService;

    public Converter(final ConfigProperties configProperties,
                     final ProductService _productService,
                     final InventoryService _inventoryService,
                     final DocumentService _documentService,
                     final ContactService _contactService,
                     final UserService _userService,
                     final CollectorService _collectorService)
    {
        INSTANCE = this;
        this.configProperties = configProperties;
        productService = _productService;
        inventoryService = _inventoryService;
        documentService = _documentService;
        contactService = _contactService;
        userService = _userService;
        collectorService = _collectorService;
    }

    public static Receipt toEntity(final PosReceiptDto _dto)
    {
        return mapToEntity(_dto, new Receipt().setId(_dto.getId()));
    }

    public static Receipt mapToEntity(final PosReceiptDto dto,
                                      final Receipt receipt)
    {
        map2DocEntity(dto, receipt);
        return receipt.setDiscount(dto.getDiscount() == null ? null : toEntity(dto.getDiscount()));
    }

    public static Invoice toEntity(final PosInvoiceDto _dto)
    {
        return mapToEntity(_dto, new Invoice().setId(_dto.getId()));
    }

    public static Invoice mapToEntity(final PosInvoiceDto dto,
                                      final Invoice invoice)
    {
        map2DocEntity(dto, invoice);
        return invoice.setDiscount(dto.getDiscount() == null ? null : toEntity(dto.getDiscount()));
    }

    public static Ticket toEntity(final PosTicketDto _dto)
    {
        return mapToEntity(_dto, new Ticket().setId(_dto.getId()));
    }

    public static Ticket mapToEntity(final PosTicketDto dto,
                                     final Ticket ticket)
    {
        map2DocEntity(dto, ticket);
        return ticket.setDiscount(dto.getDiscount() == null ? null : toEntity(dto.getDiscount()));
    }

    public static CreditNote toEntity(final PosCreditNoteDto _dto)
    {
        return mapToEntity(_dto, new CreditNote().setId(_dto.getId()).setSourceDocOid(_dto.getSourceDocOid()));
    }

    public static CreditNote mapToEntity(final PosCreditNoteDto dto,
                                         final CreditNote creditNote)
    {
        map2DocEntity(dto, creditNote);
        return creditNote.setDiscount(dto.getDiscount() == null ? null : toEntity(dto.getDiscount()));
    }

    public static Order toEntity(final PosOrderDto _dto)
    {
        return mapToEntity(_dto, new Order().setId(_dto.getId()));
    }

    public static Order mapToEntity(final PosOrderDto dto,
                                    final Order order)
    {
        map2DocEntity(dto, order);
        return order.setSpot(dto.getSpot() == null ? null : toEntity(dto.getSpot()))
                        .setPayableOid(dto.getPayableOid())
                        .setOrderOptionKey(dto.getOrderOptionKey())
                        .setDiscount(dto.getDiscount() == null ? null : toEntity(dto.getDiscount()))
                        .setShoutout(dto.getShoutout());
    }

    public static Spot toEntity(final PosSpotDto _dto)
    {
        return new Spot().setId(_dto.getId()).setLabel(_dto.getLabel());
    }

    public static Spot toEntity(final SpotDto _dto)
    {
        return new Spot().setId(_dto.getOid()).setLabel(_dto.getLabel());
    }

    public static SpotDto toDto(final Spot _entity)
    {
        return SpotDto.builder().withOID(_entity.getId()).withLabel(_entity.getLabel()).build();
    }

    public static AbstractDocument.Item toEntity(final PosDocItemDto dto)
    {
        return new AbstractDocument.Item().setOid(dto.getOid())
                        .setIndex(dto.getIndex())
                        .setParentIdx(dto.getParentIdx())
                        .setCrossPrice(dto.getCrossPrice())
                        .setCrossUnitPrice(dto.getCrossUnitPrice())
                        .setNetPrice(dto.getNetPrice())
                        .setNetUnitPrice(dto.getNetUnitPrice())
                        .setExchangeRate(dto.getExchangeRate())
                        .setCurrency(dto.getCurrency())
                        .setQuantity(dto.getQuantity())
                        .setRemark(dto.getRemark())
                        .setBomOid(dto.getBomOid())
                        .setProductOid(dto.getProduct() == null ? dto.getProductOid() : dto.getProduct().getOid())
                        .setStandInOid(dto.getStandIn() == null ? dto.getStandInOid() : dto.getStandIn().getOid())
                        .setTaxes(dto.getTaxes() == null ? null
                                        : dto.getTaxes().stream().map(Converter::toEntity)
                                                        .collect(Collectors.toSet()));
    }

    public static AbstractDocument.Item toEntity(final DocItemDto dto)
    {
        return new AbstractDocument.Item().setOid(dto.getOid())
                        .setIndex(dto.getIndex())
                        .setParentIdx(dto.getParentIdx())
                        .setCrossPrice(dto.getCrossPrice())
                        .setCrossUnitPrice(dto.getCrossUnitPrice())
                        .setNetPrice(dto.getNetPrice())
                        .setNetUnitPrice(dto.getNetUnitPrice())
                        .setExchangeRate(dto.getExchangeRate())
                        .setCurrency(dto.getCurrency())
                        .setQuantity(dto.getQuantity())
                        .setRemark(dto.getRemark())
                        .setBomOid(dto.getBomOid())
                        .setProductOid(dto.getProductOid())
                        .setStandInOid(dto.getStandInOid())
                        .setTaxes(dto.getTaxes() == null ? null
                                        : dto.getTaxes().stream().map(Converter::toEntity)
                                                        .collect(Collectors.toSet()));
    }

    public static Product toEntity(final ProductDto _dto)
    {
        final Product ret = new Product().setOid(_dto.getOid())
                        .setSKU(_dto.getSku())
                        .setType(_dto.getType())
                        .setImageOid(_dto.getImageOid())
                        .setDescription(_dto.getDescription())
                        .setNote(_dto.getNote())
                        .setNetPrice(_dto.getNetPrice())
                        .setCrossPrice(_dto.getCrossPrice())
                        .setCurrency(_dto.getCurrency())
                        .setCategories(_dto.getCategories().stream().map(Converter::toEntity)
                                        .collect(Collectors.toSet()))
                        .setTaxes(_dto.getTaxes().stream().map(_tax -> _tax == null ? null : toEntity(_tax))
                                        .collect(Collectors.toSet()))
                        .setUoM(_dto.getUoM())
                        .setUoMCode(_dto.getUoMCode())
                        .setRelations(_dto.getRelations().stream().map(Converter::toEntity)
                                        .collect(Collectors.toSet()))
                        .setIndicationSets(_dto.getIndicationSets().stream().map(Converter::toEntity)
                                        .collect(Collectors.toSet()))
                        .setBarcodes(_dto.getBarcodes().stream().map(Converter::toEntity).collect(Collectors.toSet()))
                        .setBomGroupConfigs(_dto.getBomGroupConfigs().stream().map(Converter::toEntity)
                                        .collect(Collectors.toSet()))
                        .setConfigurationBOMs(_dto.getConfigurationBOMs().stream().map(Converter::toEntity)
                                        .collect(Collectors.toSet()))
                        .setIndividual(_dto.getIndividual());
        return ret;
    }

    public static ConfigurationBOM toEntity(ConfigurationBOMDto dto)
    {
        return new ConfigurationBOM().setOid(dto.getOid())
                        .setToProductOid(dto.getToProductOid())
                        .setPosition(dto.getPosition())
                        .setBomGroupOid(dto.getBomGroupOid())
                        .setQuantity(dto.getQuantity())
                        .setUoM(dto.getUoM())
                        .setFlags(dto.getFlags())
                        .setActions(dto.getActions() == null ? null
                                        : dto.getActions().stream().map(Converter::toEntity).toList());
    }

    public static BOMAction toEntity(final BOMActionDto dto)
    {
        return new BOMAction().setType(dto.getType()).setNetAmount(dto.getNetAmount())
                        .setCrossAmount(dto.getCrossAmount());
    }

    public static BOMGroupConfig toEntity(final BOMGroupConfigDto dto)
    {
        return new BOMGroupConfig().setOid(dto.getOid())
                        .setProductOid(dto.getProductOid())
                        .setName(dto.getName())
                        .setDescription(dto.getDescription())
                        .setWeight(dto.getWeight())
                        .setFlags(dto.getFlags())
                        .setMinQuantity(dto.getMinQuantity())
                        .setMaxQuantity(dto.getMaxQuantity());
    }

    public static Product2Category toEntity(final Product2CategoryDto _dto)
    {
        return new Product2Category().setCategoryOid(_dto.getCategoryOid()).setWeight(_dto.getWeight());
    }

    public static Product2CategoryDto toDto(final Product2Category _entity)
    {
        return Product2CategoryDto.builder()
                        .withCategoryOid(_entity.getCategoryOid())
                        .withWeight(_entity.getWeight())
                        .build();
    }

    public static Barcode toEntity(final BarcodeDto _dto)
    {
        return new Barcode().setType(_dto.getType()).setCode(_dto.getCode());
    }

    public static BarcodeDto toDto(final Barcode _entity)
    {
        return BarcodeDto.builder().withType(_entity.getType()).withCode(_entity.getCode()).build();
    }

    public static IndicationSet toEntity(final IndicationSetDto _dto)
    {
        return new IndicationSet().setId(_dto.getOid())
                        .setName(_dto.getName())
                        .setDescription(_dto.getDescription())
                        .setRequired(_dto.isRequired())
                        .setMultiple(_dto.isMultiple())
                        .setImageOid(_dto.getImageOid())
                        .setIndications(_dto.getIndications().stream().map(Converter::toEntity)
                                        .collect(Collectors.toSet()));
    }

    public static IndicationSetDto toDto(final IndicationSet _entity)
    {
        return IndicationSetDto.builder()
                        .withOID(_entity.getId())
                        .withName(_entity.getName())
                        .withDescription(_entity.getDescription())
                        .withRequired(_entity.isRequired())
                        .withMultiple(_entity.isMultiple())
                        .withImageOid(_entity.getImageOid())
                        .withIndications(_entity.getIndications().stream().map(Converter::toDto)
                                        .collect(Collectors.toSet()))
                        .build();
    }

    public static Indication toEntity(final IndicationDto dto)
    {
        return new Indication().setId(dto.getOid())
                        .setValue(dto.getValue())
                        .setDescription(dto.getDescription())
                        .setImageOid(dto.getImageOid())
                        .setDefaultSelected(dto.isDefaultSelected())
                        .setWeight(dto.getWeight());
    }

    public static IndicationDto toDto(final Indication entity)
    {
        return IndicationDto.builder()
                        .withOID(entity.getId())
                        .withValue(entity.getValue())
                        .withDescription(entity.getDescription())
                        .withImageOid(entity.getImageOid())
                        .withDefaultSelected(entity.isDefaultSelected())
                        .withWeight(entity.getWeight())
                        .build();
    }

    public static ProductDto toDto(final Product entity)
    {
        if (entity == null) {
            return null;
        }
        final var prices = INSTANCE.productService.evalPrices(entity);
        return ProductDto.builder()
                        .withSKU(entity.getSKU())
                        .withType(entity.getType())
                        .withDescription(entity.getDescription())
                        .withNote(entity.getNote())
                        .withImageOid(entity.getImageOid())
                        .withOID(entity.getOid())
                        .withNetPrice(prices.getLeft())
                        .withCrossPrice(prices.getRight())
                        .withCurrency(entity.getCurrency())
                        .withCategories(entity.getCategories() == null ? null
                                        : entity.getCategories().stream()
                                                        .map(Converter::toDto)
                                                        .collect(Collectors.toSet()))

                        .withTaxes(entity.getTaxes() == null ? null
                                        : entity.getTaxes().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .withUoM(entity.getUoM())
                        .withUoMCode(entity.getUoMCode())
                        .withRelations(entity.getRelations() == null ? null
                                        : entity.getRelations().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .withIndicationSets(entity.getIndicationSets() == null ? null
                                        : entity.getIndicationSets().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .withBarcodes(entity.getBarcodes() == null ? null
                                        : entity.getBarcodes().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .withBomGroupConfigs(entity.getBomGroupConfigs() == null ? null
                                        : entity.getBomGroupConfigs().stream()
                                                        .sorted((arg,
                                                                 arg1) -> Integer.valueOf(
                                                                                 arg.getWeight())
                                                                                 .compareTo(arg1.getWeight()))
                                                        .map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .withConfigurationBOMs(entity.getConfigurationBOMs() == null ? null
                                        : entity.getConfigurationBOMs()
                                                        .stream()
                                                        .sorted((arg,
                                                                 arg1) -> Integer.valueOf(
                                                                                 arg.getPosition())
                                                                                 .compareTo(arg1.getPosition()))
                                                        .map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .withIndividual(entity.getIndividual())
                        .build();
    }

    public static BOMGroupConfigDto toDto(final BOMGroupConfig entity)
    {
        return BOMGroupConfigDto.builder()
                        .withOID(entity.getOid())
                        .withName(entity.getName())
                        .withDescription(entity.getDescription())
                        .withProductOid(entity.getProductOid())
                        .withWeight(entity.getWeight())
                        .withFlags(entity.getFlags())
                        .withMinQuantity(entity.getMinQuantity())
                        .withMaxQuantity(entity.getMaxQuantity())
                        .build();
    }

    public static ConfigurationBOMDto toDto(final ConfigurationBOM entity)
    {
        return ConfigurationBOMDto.builder()
                        .withOID(entity.getOid())
                        .withBomGroupOid(entity.getBomGroupOid())
                        .withPosition(entity.getPosition())
                        .withQuantity(entity.getQuantity())
                        .withUoM(entity.getUoM())
                        .withToProductOid(entity.getToProductOid())
                        .withFlags(entity.getFlags())
                        .withActions(entity.getActions() == null ? Collections.emptyList()
                                        : entity.getActions()
                                                        .stream().map(Converter::toDto).toList())
                        .build();
    }

    public static BOMActionDto toDto(final BOMAction entity)
    {
        return BOMActionDto.builder()
                        .withType(entity.getType())
                        .withNetAmount(entity.getNetAmount())
                        .withCrossAmount(entity.getCrossAmount())
                        .build();
    }

    public static PosUserDto toDto(final User _user)
    {
        return _user == null ? null
                        : PosUserDto.builder()
                                        .withUsername(_user.getUsername())
                                        .withEmployeeOid(_user.getEmployeeOid())
                                        .withFirstName(_user.getFirstName())
                                        .withSurName(_user.getSurName())
                                        .build();
    }

    public static User toEntity(final UserDto _dto)
    {
        return new User().setOid(_dto.getOid())
                        .setUsername(_dto.getUsername())
                        .setPassword(_dto.getPassword())
                        .setEmployeeOid(_dto.getEmployeeOid())
                        .setFirstName(_dto.getFirstName())
                        .setSurName(_dto.getSurName())
                        .setVisible(_dto.isVisible())
                        .setWorkspaceOids(_dto.getWorkspaceOids())
                        .setPermissions(_dto.getPermissions());
    }

    public static Sequence toEntity(final SequenceDto _dto)
    {
        return new Sequence().setOid(_dto.getOid()).setFormat(_dto.getFormat()).setSeq(_dto.getSeq());
    }

    public static WorkspaceDto toDto(final Workspace _entity)
    {
        return WorkspaceDto.builder()
                        .withOID(_entity.getOid())
                        .withName(_entity.getName())
                        .withPosOid(_entity.getPosOid())
                        .withDocTypes(_entity.getDocTypes())
                        .withSpotConfig(_entity.getSpotConfig())
                        .withSpotCount(_entity.getSpotCount() == null ? 0 : _entity.getSpotCount())
                        .withWarehouseOid(_entity.getWarehouseOid())
                        .withPrintCmds(_entity.getPrintCmds() == null ? Collections.emptySet()
                                        : _entity.getPrintCmds().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .withPosLayout(_entity.getPosLayout())
                        .withDiscounts(_entity.getDiscounts() == null ? Collections.emptySet()
                                        : _entity.getDiscounts().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .withCards(_entity.getCards() == null ? Collections.emptySet()
                                        : _entity.getCards().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .withGridSize(_entity.getGridSize())
                        .withFloors(_entity.getFloors() == null ? Collections.emptyList()
                                        : _entity.getFloors().stream().map(Converter::toDto)
                                                        .collect(Collectors.toList()))
                        .withCategoryOids(_entity.getCategoryOids() == null ? Collections.emptyList()
                                        : _entity.getCategoryOids())
                        .withFlags(_entity.getFlags())
                        .build();
    }

    public static Workspace toEntity(final WorkspaceDto _dto)
    {
        return new Workspace().setOid(_dto.getOid())
                        .setName(_dto.getName())
                        .setPosOid(_dto.getPosOid())
                        .setDocTypes(_dto.getDocTypes())
                        .setSpotConfig(_dto.getSpotConfig())
                        .setSpotCount(_dto.getSpotCount())
                        .setWarehouseOid(_dto.getWarehouseOid())
                        .setPrintCmds(_dto.getPrintCmds() == null ? null
                                        : _dto.getPrintCmds().stream().map(Converter::toEntity)
                                                        .collect(Collectors.toSet()))
                        .setPosLayout(_dto.getPosLayout())
                        .setDiscounts(_dto.getDiscounts() == null ? null
                                        : _dto.getDiscounts().stream().map(Converter::toEntity)
                                                        .collect(Collectors.toSet()))
                        .setCards(_dto.getCards() == null ? null
                                        : _dto.getCards().stream().map(Converter::toEntity)
                                                        .collect(Collectors.toSet()))
                        .setGridSize(_dto.getGridSize())
                        .setFloors(_dto.getFloors() == null ? null
                                        : _dto.getFloors().stream().map(Converter::toEntity)
                                                        .collect(Collectors.toList()))
                        .setCategoryOids(_dto.getCategoryOids())
                        .setFlags(_dto.getFlags());
    }

    public static Floor toEntity(final FloorDto _dto)
    {
        return new Floor().setOid(_dto.getOid())
                        .setImageOid(_dto.getImageOid())
                        .setName(_dto.getName())
                        .setSpots(_dto.getSpots() == null ? null
                                        : _dto.getSpots().stream().map(Converter::toEntity)
                                                        .collect(Collectors.toList()));
    }

    public static FloorDto toDto(final Floor _entity)
    {
        return FloorDto.builder()
                        .withOID(_entity.getOid())
                        .withImageOid(_entity.getImageOid())
                        .withName(_entity.getName())
                        .withSpots(_entity.getSpots() == null ? null
                                        : _entity.getSpots().stream().map(Converter::toDto)
                                                        .collect(Collectors.toList()))
                        .build();
    }

    public static PosDto toDto(final Pos _entity)
    {
        return PosDto.builder()
                        .withOID(_entity.getOid())
                        .withName(_entity.getName())
                        .withCurrency(_entity.getCurrency())
                        .withReceiptSeqOid(_entity.getReceiptSeqOid())
                        .withInvoiceSeqOid(_entity.getInvoiceSeqOid())
                        .withTicketSeqOid(_entity.getTicketSeqOid())
                        .withCreditNote4InvoiceSeqOid(_entity.getCreditNote4InvoiceSeqOid())
                        .withCreditNote4ReceiptSeqOid(_entity.getCreditNote4ReceiptSeqOid())
                        .build();
    }

    public static Pos toEntity(final PosDto _dto)
    {
        final Pos ret = new Pos().setOid(_dto.getOid())
                        .setName(_dto.getName())
                        .setCurrency(_dto.getCurrency())
                        .setDefaultContactOid(_dto.getDefaultContactOid())
                        .setReceiptSeqOid(_dto.getReceiptSeqOid())
                        .setInvoiceSeqOid(_dto.getInvoiceSeqOid())
                        .setTicketSeqOid(_dto.getTicketSeqOid())
                        .setCreditNote4InvoiceSeqOid(_dto.getCreditNote4InvoiceSeqOid())
                        .setCreditNote4ReceiptSeqOid(_dto.getCreditNote4ReceiptSeqOid());
        return ret;
    }

    public static CategoryDto toDto(final Category entity)
    {
        return CategoryDto.builder()
                        .withOID(entity.getOid())
                        .withName(entity.getName())
                        .withDescription(entity.getDescription())
                        .withWeight(entity.getWeight())
                        .withImageOid(entity.getImageOid())
                        .withParentOid(entity.getParentOid())
                        .build();
    }

    public static Category toEntity(final CategoryDto dto)
    {
        return new Category().setName(dto.getName())
                        .setDescription(dto.getDescription())
                        .setOid(dto.getOid())
                        .setWeight(dto.getWeight())
                        .setImageOid(dto.getImageOid())
                        .setParentOid(dto.getParentOid());
    }

    public static Contact toEntity(final ContactDto dto)
    {
        return new Contact().setId(dto.getId())
                        .setOid(dto.getOid())
                        .setName(dto.getName())
                        .setIdType(dto.getIdType())
                        .setIdNumber(dto.getIdNumber())
                        .setEmail(dto.getEmail())
                        .setForename(dto.getForename())
                        .setFirstLastName(dto.getFirstLastName())
                        .setSecondLastName(dto.getSecondLastName());
    }

    public static TaxDto toDto(final Tax _entity)
    {
        return TaxDto.builder()
                        .withOID(_entity.getOid())
                        .withKey(_entity.getKey())
                        .withCatKey(_entity.getCatKey())
                        .withType(_entity.getType())
                        .withName(_entity.getName())
                        .withPercent(_entity.getPercent())
                        .withAmount(_entity.getAmount())
                        .build();
    }

    public static Tax toEntity(final TaxDto _dto)
    {
        return new Tax().setOid(_dto.getOid())
                        .setKey(_dto.getKey())
                        .setCatKey(_dto.getCatKey())
                        .setType(_dto.getType())
                        .setName(_dto.getName())
                        .setPercent(_dto.getPercent())
                        .setAmount(_dto.getAmount());
    }

    public static IPayment toEntity(final IPaymentDto dto)
    {
        final AbstractPayment payment = switch (dto.getType()) {
            case CASH -> {
                yield new PaymentCash();
            }
            case CARD -> {
                yield new PaymentCard();
            }
            case CHANGE -> {
                yield new PaymentChange();
            }
            case ELECTRONIC -> {
                final var paymentDto = (PaymentElectronicAbstractDto) dto;
                final var entity = new PaymentElectronic()
                                .setMappingKey(paymentDto.getMappingKey())
                                .setCardLabel(paymentDto.getCardLabel())
                                .setServiceProvider(paymentDto.getServiceProvider())
                                .setAuthorization(paymentDto.getAuthorization())
                                .setOperationId(paymentDto.getOperationId())
                                .setCardNumber(paymentDto.getCardNumber())
                                .setEquipmentIdent(paymentDto.getEquipmentIdent());
                yield entity;
            }
            case FREE -> {
                yield new PaymentFree();
            }
            case LOYALTY_POINTS -> {
                final var paymentDto = (PaymentLoyaltyPointsAbstractDto) dto;
                final var entity = new PaymentLoyaltyPoints()
                                .setMappingKey(paymentDto.getMappingKey())
                                .setAuthorization(paymentDto.getAuthorization())
                                .setOperationId(paymentDto.getOperationId())
                                .setPointsAmount(paymentDto.getPointsAmount());
                yield entity;
            }
        };
        final var paymentDto = (PaymentAbstractDto) dto;

        payment.setOid(paymentDto.getOid())
                        .setAmount(paymentDto.getAmount())
                        .setCurrency(paymentDto.getCurrency())
                        .setExchangeRate(paymentDto.getExchangeRate())
                        .setInfo(paymentDto.getInfo())
                        .setOperationDateTime(paymentDto.getOperationDateTime());
        if (dto instanceof IPosPaymentDto) {
            payment.setCollectOrderId(((IPosPaymentDto) dto).getCollectOrderId());
        }
        return payment;
    }

    public static IPosPaymentDto toPosDto(final IPayment entity)
    {
        return switch (entity.getType()) {
            case CASH -> {
                final var posEntity = (PaymentCash) entity;
                final var builder = PosPaymentCashDto.builder()
                                .withOID(posEntity.getOid())
                                .withType(posEntity.getType())
                                .withAmount(posEntity.getAmount())
                                .withCurrency(posEntity.getCurrency())
                                .withExchangeRate(posEntity.getExchangeRate())
                                .withCollectOrderId(posEntity.getCollectOrderId())
                                .withInfo(posEntity.getInfo());
                INSTANCE.collectorService.add2PaymentDto(builder, entity);
                yield builder.build();
            }
            case CARD -> {
                final var posEntity = (PaymentCard) entity;
                final var builder = PosPaymentCardDto.builder()
                                .withOID(posEntity.getOid())
                                .withType(posEntity.getType())
                                .withAmount(posEntity.getAmount())
                                .withCurrency(posEntity.getCurrency())
                                .withExchangeRate(posEntity.getExchangeRate())
                                .withCollectOrderId(posEntity.getCollectOrderId())
                                .withInfo(posEntity.getInfo());
                INSTANCE.collectorService.add2PaymentDto(builder, entity);
                yield builder.build();
            }
            case CHANGE -> {
                final var posEntity = (PaymentChange) entity;
                final var builder = PosPaymentChangeDto.builder()
                                .withOID(posEntity.getOid())
                                .withType(posEntity.getType())
                                .withAmount(posEntity.getAmount())
                                .withCurrency(posEntity.getCurrency())
                                .withExchangeRate(posEntity.getExchangeRate())
                                .withCollectOrderId(posEntity.getCollectOrderId())
                                .withInfo(posEntity.getInfo());
                INSTANCE.collectorService.add2PaymentDto(builder, entity);
                yield builder.build();
            }
            case ELECTRONIC -> {
                final var posEntity = (PaymentElectronic) entity;
                final var builder = PosPaymentElectronicDto.builder()
                                .withOID(posEntity.getOid())
                                .withType(posEntity.getType())
                                .withAmount(posEntity.getAmount())
                                .withCurrency(posEntity.getCurrency())
                                .withExchangeRate(posEntity.getExchangeRate())
                                .withCollectOrderId(posEntity.getCollectOrderId())
                                .withInfo(posEntity.getInfo())
                                .withAuthorization(posEntity.getAuthorization())
                                .withCardLabel(posEntity.getCardLabel())
                                .withCardNumber(posEntity.getCardLabel())
                                .withEquipmentIdent(posEntity.getEquipmentIdent())
                                .withOperationDateTime(posEntity.getOperationDateTime())
                                .withServiceProvider(posEntity.getServiceProvider())
                                .withMappingKey(posEntity.getMappingKey());
                INSTANCE.collectorService.add2PaymentDto(builder, entity);
                yield builder.build();
            }
            case FREE -> {
                final var posEntity = (PaymentFree) entity;
                final var builder = PosPaymentFreeDto.builder()
                                .withOID(posEntity.getOid())
                                .withType(posEntity.getType())
                                .withAmount(posEntity.getAmount())
                                .withCurrency(posEntity.getCurrency())
                                .withCollectOrderId(posEntity.getCollectOrderId())
                                .withInfo(posEntity.getInfo());
                INSTANCE.collectorService.add2PaymentDto(builder, entity);
                yield builder.build();
            }
            case LOYALTY_POINTS -> {
                final var posEntity = (PaymentLoyaltyPoints) entity;
                final var builder = PosPaymentLoyaltyPointsDto.builder()
                                .withOID(posEntity.getOid())
                                .withType(posEntity.getType())
                                .withAmount(posEntity.getAmount())
                                .withCurrency(posEntity.getCurrency())
                                .withExchangeRate(posEntity.getExchangeRate())
                                .withCollectOrderId(posEntity.getCollectOrderId())
                                .withInfo(posEntity.getInfo())
                                .withAuthorization(posEntity.getAuthorization())
                                .withOperationId(posEntity.getOperationId())
                                .withPointsAmount(posEntity.getPointsAmount())
                                .withOperationDateTime(posEntity.getOperationDateTime())
                                .withMappingKey(posEntity.getMappingKey());
                INSTANCE.collectorService.add2PaymentDto(builder, entity);
                yield builder.build();
            }
        };

    }

    public static IPaymentDto toDto(final IPayment entity)
    {
        return switch (entity.getType()) {
            case CASH -> {
                final var posEntity = (PaymentCash) entity;
                final var builder = PaymentCashDto.builder()
                                .withOID(posEntity.getOid())
                                .withType(posEntity.getType())
                                .withAmount(posEntity.getAmount())
                                .withCurrency(posEntity.getCurrency())
                                .withExchangeRate(posEntity.getExchangeRate())
                                .withInfo(posEntity.getInfo());
                INSTANCE.collectorService.add2PaymentDto(builder, entity);
                yield builder.build();
            }
            case CARD -> {
                final var posEntity = (PaymentCard) entity;
                final var builder = PaymentCardDto.builder()
                                .withOID(posEntity.getOid())
                                .withType(posEntity.getType())
                                .withAmount(posEntity.getAmount())
                                .withCurrency(posEntity.getCurrency())
                                .withExchangeRate(posEntity.getExchangeRate())
                                .withInfo(posEntity.getInfo());
                INSTANCE.collectorService.add2PaymentDto(builder, entity);
                yield builder.build();
            }
            case CHANGE -> {
                final var posEntity = (PaymentChange) entity;
                final var builder = PaymentChangeDto.builder()
                                .withOID(posEntity.getOid())
                                .withType(posEntity.getType())
                                .withAmount(posEntity.getAmount())
                                .withCurrency(posEntity.getCurrency())
                                .withExchangeRate(posEntity.getExchangeRate())
                                .withInfo(posEntity.getInfo());
                INSTANCE.collectorService.add2PaymentDto(builder, entity);
                yield builder.build();
            }
            case ELECTRONIC -> {
                final var posEntity = (PaymentElectronic) entity;
                final var builder = PaymentElectronicDto.builder()
                                .withOID(posEntity.getOid())
                                .withType(posEntity.getType())
                                .withAmount(posEntity.getAmount())
                                .withCurrency(posEntity.getCurrency())
                                .withExchangeRate(posEntity.getExchangeRate())
                                .withInfo(posEntity.getInfo())
                                .withAuthorization(posEntity.getAuthorization())
                                .withCardLabel(posEntity.getCardLabel())
                                .withCardNumber(posEntity.getCardLabel())
                                .withEquipmentIdent(posEntity.getEquipmentIdent())
                                .withOperationDateTime(posEntity.getOperationDateTime())
                                .withServiceProvider(posEntity.getServiceProvider())
                                .withMappingKey(posEntity.getMappingKey());
                INSTANCE.collectorService.add2PaymentDto(builder, entity);
                yield builder.build();
            }
            case FREE -> {
                final var posEntity = (PaymentFree) entity;
                final var builder = PaymentFreeDto.builder()
                                .withOID(posEntity.getOid())
                                .withType(posEntity.getType())
                                .withAmount(posEntity.getAmount())
                                .withCurrency(posEntity.getCurrency())
                                .withExchangeRate(posEntity.getExchangeRate())
                                .withInfo(posEntity.getInfo());
                INSTANCE.collectorService.add2PaymentDto(builder, entity);
                yield builder.build();
            }
            case LOYALTY_POINTS -> {
                final var posEntity = (PaymentLoyaltyPoints) entity;
                final var builder = PaymentLoyaltyPointsDto.builder()
                                .withOID(posEntity.getOid())
                                .withType(posEntity.getType())
                                .withAmount(posEntity.getAmount())
                                .withCurrency(posEntity.getCurrency())
                                .withExchangeRate(posEntity.getExchangeRate())
                                .withInfo(posEntity.getInfo())
                                .withAuthorization(posEntity.getAuthorization())
                                .withOperationId(posEntity.getOperationId())
                                .withPointsAmount(posEntity.getPointsAmount())
                                .withOperationDateTime(posEntity.getOperationDateTime())
                                .withMappingKey(posEntity.getMappingKey());
                INSTANCE.collectorService.add2PaymentDto(builder, entity);
                yield builder.build();
            }
        };
    }

    public static TaxEntry toEntity(final TaxEntryDto _dto)
    {
        final TaxEntry ret = new TaxEntry().setTax(_dto.getTax() == null ? null : Converter.toEntity(_dto.getTax()))
                        .setBase(_dto.getBase())
                        .setAmount(_dto.getAmount())
                        .setCurrency(_dto.getCurrency())
                        .setExchangeRate(_dto.getExchangeRate());
        return ret;
    }

    public static TaxEntryDto toDto(final TaxEntry _entity)
    {
        return TaxEntryDto.builder()
                        .withTax(Converter.toDto(_entity.getTax()))
                        .withBase(_entity.getBase())
                        .withAmount(_entity.getAmount())
                        .withCurrency(_entity.getCurrency())
                        .withExchangeRate(_entity.getExchangeRate())
                        .build();
    }

    public static PosOrderDto toDto(final Order entity)
    {
        return PosOrderDto.builder()
                        .withId(entity.getId())
                        .withOID(entity.getOid())
                        .withNumber(entity.getNumber())
                        .withContactOid(entity.getContactOid())
                        .withCurrency(entity.getCurrency())
                        .withExchangeRate(entity.getExchangeRate())
                        .withDate(entity.getDate())
                        .withItems(entity.getItems() == null ? Collections.emptySet()
                                        : entity.getItems().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .withStatus(entity.getStatus())
                        .withNetTotal(entity.getNetTotal())
                        .withCrossTotal(entity.getCrossTotal())
                        .withPayableAmount(entity.getPayableAmount())
                        .withTaxes(entity.getTaxes() == null ? null
                                        : entity.getTaxes().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .withSpot(toSpotDto(entity.getSpot()))
                        .withDiscount(entity.getDiscount() == null ? null : toDto(entity.getDiscount()))
                        .withNote(entity.getNote())
                        .withPayableOid(entity.getPayableOid())
                        .withShoutout(entity.getShoutout())
                        .withEmployeeRelations(entity.getEmployeeRelations() == null ? Collections.emptySet()
                                        : entity.getEmployeeRelations().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .withOrderOptionKey(entity.getOrderOptionKey())
                        .build();
    }

    public static PosSpotDto toSpotDto(final Spot _spot)
    {
        return _spot == null ? null : PosSpotDto.builder().withId(_spot.getId()).withLabel(_spot.getLabel()).build();
    }

    public static PosDocItemDto toDto(final AbstractDocument.Item entity)
    {
        return PosDocItemDto.builder()
                        .withOID(entity.getOid())
                        .withIndex(entity.getIndex())
                        .withParentIdx(entity.getParentIdx())
                        .withCrossPrice(entity.getCrossPrice())
                        .withCrossUnitPrice(entity.getCrossUnitPrice())
                        .withNetPrice(entity.getNetPrice())
                        .withNetUnitPrice(entity.getNetUnitPrice())
                        .withCurrency(entity.getCurrency())
                        .withExchangeRate(entity.getExchangeRate())
                        .withQuantity(entity.getQuantity())
                        .withProductOid(entity.getProductOid())
                        .withRemark(entity.getRemark())
                        .withBomOid(entity.getBomOid())
                        .withProduct(entity.getProductOid() == null ? null
                                        : Converter.toDto(INSTANCE.productService.getProduct(entity.getProductOid())))
                        .withStandIn(entity.getStandInOid() == null ? null
                                        : Converter.toDto(INSTANCE.productService.getProduct(entity.getStandInOid())))
                        .withTaxes(entity.getTaxes() == null ? null
                                        : entity.getTaxes().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .build();
    }

    public static PosReceiptDto toDto(final Receipt _entity)
    {
        return PosReceiptDto.builder()
                        .withId(_entity.getId())
                        .withOID(_entity.getOid())
                        .withNumber(_entity.getNumber())
                        .withDate(_entity.getDate())
                        .withCurrency(_entity.getCurrency())
                        .withStatus(_entity.getStatus())
                        .withNetTotal(_entity.getNetTotal())
                        .withCrossTotal(_entity.getCrossTotal())
                        .withExchangeRate(_entity.getExchangeRate())
                        .withPayableAmount(_entity.getPayableAmount())
                        .withContactOid(_entity.getContactOid())
                        .withWorkspaceOid(_entity.getWorkspaceOid())
                        .withItems(_entity.getItems() == null ? null
                                        : _entity.getItems().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .withTaxes(_entity.getTaxes() == null ? null
                                        : _entity.getTaxes().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .withPayments(_entity.getPayments() == null ? null
                                        : _entity.getPayments().stream().map(Converter::toPosDto)
                                                        .collect(Collectors.toSet()))
                        .withBalanceOid(_entity.getBalanceOid())
                        .withDiscount(_entity.getDiscount() == null ? null : toDto(_entity.getDiscount()))
                        .withNote(_entity.getNote())
                        .withEmployeeRelations(_entity.getEmployeeRelations() == null ? Collections.emptySet()
                                        : _entity.getEmployeeRelations().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .build();
    }

    public static Receipt toEntity(final ReceiptDto dto)
    {
        final var entity = new Receipt();
        map2DocEntity(dto, entity);
        return entity;
    }

    public static PosInvoiceDto toDto(final Invoice _entity)
    {
        return PosInvoiceDto.builder()
                        .withId(_entity.getId())
                        .withOID(_entity.getOid())
                        .withNumber(_entity.getNumber())
                        .withDate(_entity.getDate())
                        .withCurrency(_entity.getCurrency())
                        .withNetTotal(_entity.getNetTotal())
                        .withCrossTotal(_entity.getCrossTotal())
                        .withExchangeRate(_entity.getExchangeRate())
                        .withPayableAmount(_entity.getPayableAmount())
                        .withStatus(_entity.getStatus())
                        .withContactOid(_entity.getContactOid())
                        .withWorkspaceOid(_entity.getWorkspaceOid())
                        .withItems(_entity.getItems() == null ? null
                                        : _entity.getItems().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .withTaxes(_entity.getTaxes() == null ? null
                                        : _entity.getTaxes().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .withPayments(_entity.getPayments() == null ? null
                                        : _entity.getPayments().stream().map(Converter::toPosDto)
                                                        .collect(Collectors.toSet()))
                        .withBalanceOid(_entity.getBalanceOid())
                        .withDiscount(_entity.getDiscount() == null ? null : toDto(_entity.getDiscount()))
                        .withNote(_entity.getNote())
                        .withEmployeeRelations(_entity.getEmployeeRelations() == null ? Collections.emptySet()
                                        : _entity.getEmployeeRelations().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .build();
    }

    public static PosTicketDto toDto(final Ticket _entity)
    {
        return PosTicketDto.builder()
                        .withId(_entity.getId())
                        .withOID(_entity.getOid())
                        .withNumber(_entity.getNumber())
                        .withDate(_entity.getDate())
                        .withCurrency(_entity.getCurrency())
                        .withNetTotal(_entity.getNetTotal())
                        .withCrossTotal(_entity.getCrossTotal())
                        .withExchangeRate(_entity.getExchangeRate())
                        .withPayableAmount(_entity.getPayableAmount())
                        .withStatus(_entity.getStatus())
                        .withContactOid(_entity.getContactOid())
                        .withWorkspaceOid(_entity.getWorkspaceOid())
                        .withItems(_entity.getItems() == null ? null
                                        : _entity.getItems().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .withTaxes(_entity.getTaxes() == null ? null
                                        : _entity.getTaxes().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .withPayments(_entity.getPayments() == null ? null
                                        : _entity.getPayments().stream().map(Converter::toPosDto)
                                                        .collect(Collectors.toSet()))
                        .withBalanceOid(_entity.getBalanceOid())
                        .withDiscount(_entity.getDiscount() == null ? null : toDto(_entity.getDiscount()))
                        .withNote(_entity.getNote())
                        .withEmployeeRelations(_entity.getEmployeeRelations() == null ? Collections.emptySet()
                                        : _entity.getEmployeeRelations().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .build();
    }

    public static PosCreditNoteDto toDto(final CreditNote _entity)
    {
        return PosCreditNoteDto.builder()
                        .withId(_entity.getId())
                        .withOID(_entity.getOid())
                        .withNumber(_entity.getNumber())
                        .withDate(_entity.getDate())
                        .withCurrency(_entity.getCurrency())
                        .withStatus(_entity.getStatus())
                        .withNetTotal(_entity.getNetTotal())
                        .withCrossTotal(_entity.getCrossTotal())
                        .withExchangeRate(_entity.getExchangeRate())
                        .withPayableAmount(_entity.getPayableAmount())
                        .withContactOid(_entity.getContactOid())
                        .withWorkspaceOid(_entity.getWorkspaceOid())
                        .withItems(_entity.getItems() == null ? null
                                        : _entity.getItems().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .withTaxes(_entity.getTaxes() == null ? null
                                        : _entity.getTaxes().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .withPayments(_entity.getPayments() == null ? null
                                        : _entity.getPayments().stream().map(Converter::toPosDto)
                                                        .collect(Collectors.toSet()))
                        .withBalanceOid(_entity.getBalanceOid())
                        .withDiscount(_entity.getDiscount() == null ? null : toDto(_entity.getDiscount()))
                        .withNote(_entity.getNote())
                        .withSourceDocOid(_entity.getSourceDocOid())
                        .withEmployeeRelations(_entity.getEmployeeRelations() == null ? Collections.emptySet()
                                        : _entity.getEmployeeRelations().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .build();
    }

    public static ContactDto toDto(final Contact entity)
    {
        return entity == null ? null
                        : ContactDto.builder()
                                        .withId(entity.getId())
                                        .withOID(entity.getOid())
                                        .withName(entity.getName())
                                        .withIdType(entity.getIdType())
                                        .withIdNumber(entity.getIdNumber())
                                        .withEmail(entity.getEmail())
                                        .withForename(entity.getForename())
                                        .withFirstLastName(entity.getFirstLastName())
                                        .withSecondLastName(entity.getSecondLastName())
                                        .build();
    }

    public static OrderDto toOrderDto(final Order entity)
    {
        return OrderDto.builder()
                        .withId(entity.getId())
                        .withOID(entity.getOid())
                        .withNumber(entity.getNumber())
                        .withDate(entity.getDate())
                        .withCurrency(entity.getCurrency())
                        .withStatus(entity.getStatus())
                        .withCrossTotal(entity.getCrossTotal())
                        .withNetTotal(entity.getNetTotal())
                        .withExchangeRate(entity.getExchangeRate())
                        .withPayableAmount(entity.getPayableAmount())
                        .withContactOid(entity.getContactOid())
                        .withWorkspaceOid(entity.getWorkspaceOid())
                        .withItems(entity.getItems() == null ? null
                                        : entity.getItems().stream().map(Converter::toItemDto)
                                                        .collect(Collectors.toSet()))
                        .withTaxes(entity.getTaxes() == null ? null
                                        : entity.getTaxes().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .withPayableOid(entity.getPayableOid())
                        .withNote(entity.getNote())
                        .withEmployeeRelations(entity.getEmployeeRelations() == null ? Collections.emptySet()
                                        : entity.getEmployeeRelations().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .withOrderOptionKey(entity.getOrderOptionKey())
                        .withShoutout(entity.getShoutout())
                        .build();
    }

    public static ReceiptDto toReceiptDto(final Receipt _entity)
    {
        return ReceiptDto.builder()
                        .withId(_entity.getId())
                        .withOID(_entity.getOid())
                        .withNumber(_entity.getNumber())
                        .withDate(_entity.getDate())
                        .withCurrency(_entity.getCurrency())
                        .withStatus(_entity.getStatus())
                        .withCrossTotal(_entity.getCrossTotal())
                        .withExchangeRate(_entity.getExchangeRate())
                        .withPayableAmount(_entity.getPayableAmount())
                        .withNetTotal(_entity.getNetTotal())
                        .withContactOid(_entity.getContactOid())
                        .withWorkspaceOid(_entity.getWorkspaceOid())
                        .withItems(_entity.getItems() == null ? null
                                        : _entity.getItems().stream().map(Converter::toItemDto)
                                                        .collect(Collectors.toSet()))
                        .withTaxes(_entity.getTaxes() == null ? null
                                        : _entity.getTaxes().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .withPayments(_entity.getPayments() == null ? null
                                        : _entity.getPayments().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .withBalanceOid(_entity.getBalanceOid())
                        .withNote(_entity.getNote())
                        .withEmployeeRelations(_entity.getEmployeeRelations() == null ? Collections.emptySet()
                                        : _entity.getEmployeeRelations().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .build();
    }

    public static InvoiceDto toInvoiceDto(final Invoice _entity)
    {
        return InvoiceDto.builder()
                        .withId(_entity.getId())
                        .withOID(_entity.getOid())
                        .withNumber(_entity.getNumber())
                        .withDate(_entity.getDate())
                        .withCurrency(_entity.getCurrency())
                        .withStatus(_entity.getStatus())
                        .withCrossTotal(_entity.getCrossTotal())
                        .withNetTotal(_entity.getNetTotal())
                        .withExchangeRate(_entity.getExchangeRate())
                        .withPayableAmount(_entity.getPayableAmount())
                        .withContactOid(_entity.getContactOid())
                        .withWorkspaceOid(_entity.getWorkspaceOid())
                        .withItems(_entity.getItems() == null ? null
                                        : _entity.getItems().stream().map(Converter::toItemDto)
                                                        .collect(Collectors.toSet()))
                        .withTaxes(_entity.getTaxes() == null ? null
                                        : _entity.getTaxes().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .withPayments(_entity.getPayments() == null ? null
                                        : _entity.getPayments().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .withBalanceOid(_entity.getBalanceOid())
                        .withNote(_entity.getNote())
                        .withEmployeeRelations(_entity.getEmployeeRelations() == null ? Collections.emptySet()
                                        : _entity.getEmployeeRelations().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .build();
    }

    public static TicketDto toTicketDto(final Ticket _entity)
    {
        return TicketDto.builder()
                        .withId(_entity.getId())
                        .withOID(_entity.getOid())
                        .withNumber(_entity.getNumber())
                        .withDate(_entity.getDate())
                        .withCurrency(_entity.getCurrency())
                        .withStatus(_entity.getStatus())
                        .withCrossTotal(_entity.getCrossTotal())
                        .withNetTotal(_entity.getNetTotal())
                        .withExchangeRate(_entity.getExchangeRate())
                        .withPayableAmount(_entity.getPayableAmount())
                        .withContactOid(_entity.getContactOid())
                        .withWorkspaceOid(_entity.getWorkspaceOid())
                        .withItems(_entity.getItems() == null ? null
                                        : _entity.getItems().stream().map(Converter::toItemDto)
                                                        .collect(Collectors.toSet()))
                        .withTaxes(_entity.getTaxes() == null ? null
                                        : _entity.getTaxes().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .withPayments(_entity.getPayments() == null ? null
                                        : _entity.getPayments().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .withBalanceOid(_entity.getBalanceOid())
                        .withNote(_entity.getNote())
                        .withEmployeeRelations(_entity.getEmployeeRelations() == null ? Collections.emptySet()
                                        : _entity.getEmployeeRelations().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .build();
    }

    public static CreditNoteDto toCreditNoteDto(final CreditNote _entity)
    {
        return CreditNoteDto.builder()
                        .withId(_entity.getId())
                        .withOID(_entity.getOid())
                        .withNumber(_entity.getNumber())
                        .withDate(_entity.getDate())
                        .withCurrency(_entity.getCurrency())
                        .withStatus(_entity.getStatus())
                        .withCrossTotal(_entity.getCrossTotal())
                        .withNetTotal(_entity.getNetTotal())
                        .withExchangeRate(_entity.getExchangeRate())
                        .withPayableAmount(_entity.getPayableAmount())
                        .withContactOid(_entity.getContactOid())
                        .withWorkspaceOid(_entity.getWorkspaceOid())
                        .withItems(_entity.getItems() == null ? null
                                        : _entity.getItems().stream().map(Converter::toItemDto)
                                                        .collect(Collectors.toSet()))
                        .withTaxes(_entity.getTaxes() == null ? null
                                        : _entity.getTaxes().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .withPayments(_entity.getPayments() == null ? null
                                        : _entity.getPayments().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .withBalanceOid(_entity.getBalanceOid())
                        .withNote(_entity.getNote())
                        .withSourceDocOid(_entity.getSourceDocOid())
                        .withEmployeeRelations(_entity.getEmployeeRelations() == null ? Collections.emptySet()
                                        : _entity.getEmployeeRelations().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .build();
    }

    public static DocItemDto toItemDto(final AbstractDocument.Item entity)
    {
        return DocItemDto.builder()
                        .withOID(entity.getOid())
                        .withIndex(entity.getIndex())
                        .withParentIdx(entity.getParentIdx())
                        .withCrossPrice(entity.getCrossPrice())
                        .withCrossUnitPrice(entity.getCrossUnitPrice())
                        .withNetPrice(entity.getNetPrice())
                        .withNetUnitPrice(entity.getNetUnitPrice())
                        .withCurrency(entity.getCurrency())
                        .withExchangeRate(entity.getExchangeRate())
                        .withQuantity(entity.getQuantity())
                        .withProductOid(entity.getProductOid())
                        .withStandInOid(entity.getStandInOid())
                        .withRemark(entity.getRemark())
                        .withBomOid(entity.getBomOid())
                        .withTaxes(entity.getTaxes() == null ? null
                                        : entity.getTaxes().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .build();
    }

    public static Warehouse toEntity(final WarehouseDto _dto)
    {
        return new Warehouse().setName(_dto.getName()).setOid(_dto.getOid());
    }

    public static WarehouseDto toDto(final Warehouse _entity)
    {
        return WarehouseDto.builder().withOID(_entity.getOid()).withName(_entity.getName()).build();
    }

    public static InventoryEntry toEntity(final InventoryEntryDto _dto)
    {
        return new InventoryEntry().setOid(_dto.getOid())
                        .setQuantity(_dto.getQuantity())
                        .setProductOid(_dto.getProductOid())
                        .setWarehouseOid(_dto.getWarehouseOid());
    }

    public static PosInventoryEntryDto toDto(final InventoryEntry _entity)
    {
        return PosInventoryEntryDto.builder()
                        .withId(_entity.getId())
                        .withOID(_entity.getOid())
                        .withQuantity(_entity.getQuantity())
                        .withProduct(_entity.getProductOid() == null ? null
                                        : Converter.toDto(INSTANCE.productService.getProduct(_entity.getProductOid())))
                        .withWarehouse(_entity.getWarehouseOid() == null ? null
                                        : Converter.toDto(INSTANCE.inventoryService
                                                        .getWarehouse(_entity.getWarehouseOid())))
                        .build();
    }

    public static JobDto toDto(final Job _entity)
    {
        final Order order = INSTANCE.documentService.getOrderById(_entity.getDocumentId());
        return JobDto.builder()
                        .withDocumentId(_entity.getDocumentId())
                        .withDocumentNumber(order.getNumber())
                        .withShoutout(_entity.getShoutout())
                        .withSpotNumber(order.getSpot() == null ? null
                                        : StringUtils.isEmpty(order.getSpot().getLabel()) ? order.getSpot().getId()
                                                        : order.getSpot().getLabel())
                        .withId(_entity.getId())
                        .withNote(order.getNote())
                        .withItems(_entity.getItems() == null ? Collections.emptySet()
                                        : _entity.getItems().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .build();
    }

    public static Printer toEntity(final PrinterDto _dto)
    {
        return new Printer().setOid(_dto.getOid()).setName(_dto.getName()).setType(_dto.getType());
    }

    public static PrinterDto toDto(final Printer _entity)
    {
        return PrinterDto.builder()
                        .withOID(_entity.getOid())
                        .withName(_entity.getName())
                        .withType(_entity.getType())
                        .build();
    }

    public static PrintCmdDto toDto(final PrintCmd _entity)
    {
        return PrintCmdDto.builder()
                        .withPrinterOid(_entity.getPrinterOid())
                        .withTarget(_entity.getTarget())
                        .withTargetOid(_entity.getTargetOid())
                        .withReportOid(_entity.getReportOid())
                        .build();
    }

    public static PrintCmd toEntity(final PrintCmdDto _dto)
    {
        return new PrintCmd().setPrinterOid(_dto.getPrinterOid())
                        .setTarget(_dto.getTarget())
                        .setTargetOid(_dto.getTargetOid())
                        .setReportOid(_dto.getReportOid());
    }

    public static Discount toEntity(final DiscountDto _dto)
    {
        return new Discount().setLabel(_dto.getLabel())
                        .setProductOid(_dto.getProductOid())
                        .setType(_dto.getType())
                        .setValue(_dto.getValue());
    }

    public static Card toEntity(final CardDto _dto)
    {
        return new Card().setLabel(_dto.getLabel()).setCardTypeId(_dto.getCardTypeId());
    }

    public static CardDto toDto(final Card _entity)
    {
        return CardDto.builder().withLabel(_entity.getLabel()).withCardTypeId(_entity.getCardTypeId()).build();
    }

    public static DiscountDto toDto(final Discount _entity)
    {
        return DiscountDto.builder()
                        .withLabel(_entity.getLabel())
                        .withProductOid(_entity.getProductOid())
                        .withType(_entity.getType())
                        .withValue(_entity.getValue())
                        .build();
    }

    public static Balance toEntity(final BalanceDto _dto)
    {
        return new Balance().setOid(_dto.getOid())
                        .setId(_dto.getId())
                        .setNumber(_dto.getNumber())
                        .setKey(_dto.getKey())
                        .setUserOid(_dto.getUserOid())
                        .setStartAt(Utils.toLocal(_dto.getStartAt()))
                        .setEndAt(Utils.toLocal(_dto.getEndAt()))
                        .setStatus(_dto.getStatus());
    }

    public static BalanceDto toBalanceDto(final Balance _entity)
    {
        return BalanceDto.builder()
                        .withOID(_entity.getOid())
                        .withId(_entity.getId())
                        .withNumber(_entity.getNumber())
                        .withKey(_entity.getKey())
                        .withUserOid(_entity.getUserOid())
                        .withStartAt(Utils.toOffset(_entity.getStartAt()))
                        .withEndAt(Utils.toOffset(_entity.getEndAt()))
                        .withStatus(_entity.getStatus())
                        .build();
    }

    public static PosBalanceDto toDto(final Balance _entity)
    {
        final User user = INSTANCE.userService.getUserByOid(_entity.getUserOid());
        return PosBalanceDto.builder()
                        .withOID(_entity.getOid())
                        .withId(_entity.getId())
                        .withNumber(_entity.getNumber())
                        .withKey(_entity.getKey())
                        .withUser(toDto(user))
                        .withStartAt(_entity.getStartAt())
                        .withEndAt(_entity.getEndAt())
                        .withStatus(_entity.getStatus())
                        .build();
    }

    public static ProductRelationDto toDto(final ProductRelation _entity)
    {
        return ProductRelationDto.builder()
                        .withLabel(_entity.getLabel())
                        .withProductOid(_entity.getProductOid())
                        .withQuantity(_entity.getQuantity())
                        .withType(_entity.getType())
                        .build();
    }

    public static ProductRelation toEntity(final ProductRelationDto _dto)
    {
        return new ProductRelation().setLabel(_dto.getLabel())
                        .setProductOid(_dto.getProductOid())
                        .setQuantity(_dto.getQuantity())
                        .setType(_dto.getType());
    }

    public static ContactDto getContactDto(final String key)
    {
        return key == null ? null : Converter.toDto(INSTANCE.contactService.findContact(key, true));
    }

    public static CollectOrderDto toDto(final CollectOrder _entity)
    {
        return toDto(_entity, null);
    }

    public static CollectOrderDto toDto(final CollectOrder _entity,
                                        final Map<String, Object> additionalInfo)
    {
        return _entity == null ? null
                        : CollectOrderDto.builder()
                                        .withId(_entity.getId())
                                        .withOrderId(_entity.getOrderId())
                                        .withAmount(_entity.getAmount())
                                        .withCurrency(_entity.getCurrency())
                                        .withState(_entity.getState())
                                        .withCollected(_entity.getCollected())
                                        .withCollectorKey(_entity.getCollectorKey())
                                        .withAdditionalInfo(additionalInfo == null ? Collections.emptyMap()
                                                        : additionalInfo)
                                        .build();
    }

    public static PayableHeadDto toDto(final PayableHead _projection)
    {
        DocumentHeadDto order = null;
        if (_projection.getOrders() != null && _projection.getOrders().length > 0) {
            order = toDto(_projection.getOrders()[0]);
        }
        return PayableHeadDto.builder()
                        .withId(_projection.getId())
                        .withNumber(_projection.getNumber())
                        .withCrossTotal(_projection.getCrossTotal())
                        .withNetTotal(_projection.getNetTotal())
                        .withCurrency(_projection.getCurrency())
                        .withExchangeRate(_projection.getExchangeRate())
                        .withPayableAmount(_projection.getPayableAmount())
                        .withDate(_projection.getDate())
                        .withStatus(_projection.getStatus())
                        .withOrder(order)
                        .build();
    }

    public static DocumentHeadDto toDto(final DocumentHead _projection)
    {
        final Builder<?, ?> builder = new DocumentHeadDto.Builder<>();
        return builder.withId(_projection.getId())
                        .withNumber(_projection.getNumber())
                        .withCrossTotal(_projection.getCrossTotal())
                        .withNetTotal(_projection.getNetTotal())
                        .withCurrency(_projection.getCurrency())
                        .withExchangeRate(_projection.getExchangeRate())
                        .withPayableAmount(_projection.getPayableAmount())
                        .withDate(_projection.getDate())
                        .withStatus(_projection.getStatus())
                        .build();
    }

    public static CashEntry toEntity(final CashEntryDto dto)
    {
        return new CashEntry().setBalanceOid(dto.getBalanceOid())
                        .setAmount(dto.getAmount())
                        .setCurrency(dto.getCurrency())
                        .setEntryType(dto.getEntryType());
    }

    public static CashEntryDto toDto(final CashEntry entity)
    {
        return CashEntryDto.builder()
                        .withBalanceOid(entity.getBalanceOid())
                        .withAmount(entity.getAmount())
                        .withCurrency(entity.getCurrency())
                        .withEntryType(entity.getEntryType())
                        .build();
    }

    public static Employee toEntity(final EmployeeDto dto)
    {
        return new Employee().setOid(dto.getOid()).setFirstName(dto.getFirstName()).setSurName(dto.getSurName());
    }

    public static EmployeeDto toDto(final Employee entity)
    {
        return EmployeeDto.builder()
                        .withOID(entity.getOid())
                        .withFirstName(entity.getFirstName())
                        .withSurName(entity.getSurName())
                        .build();
    }

    public static EmployeeRelation toEntity(EmployeeRelationDto dto)
    {
        return new EmployeeRelation().setEmployeeOid(dto.getEmployeeOid()).setType(dto.getType());
    }

    public static EmployeeRelationDto toDto(EmployeeRelation entity)
    {
        return EmployeeRelationDto.builder().withEmployeeOid(entity.getEmployeeOid()).withType(entity.getType())
                        .build();
    }

    public static void map2DocEntity(AbstractDocumentDto dto,
                                     AbstractDocument<?> entity)
    {
        entity.setOid(dto.getOid());
        entity.setNumber(dto.getNumber());
        entity.setCurrency(dto.getCurrency());
        entity.setStatus(dto.getStatus());
        entity.setDate(dto.getDate());
        entity.setItems(dto.getItems().stream().map(Converter::toEntity)
                        .collect(Collectors.toList()));
        entity.setNetTotal(dto.getNetTotal());
        entity.setCrossTotal(dto.getCrossTotal());
        entity.setExchangeRate(dto.getExchangeRate());
        entity.setPayableAmount(dto.getPayableAmount());
        entity.setContactOid(dto.getContactOid());
        entity.setWorkspaceOid(dto.getWorkspaceOid());
        entity.setTaxes(dto.getTaxes() == null ? null
                        : dto.getTaxes().stream().map(Converter::toEntity).collect(Collectors.toSet()));
        entity.setEmployeeRelations(dto.getEmployeeRelations() == null ? null
                        : dto.getEmployeeRelations()
                                        .stream()
                                        .map(Converter::toEntity)
                                        .collect(Collectors.toSet()));
        entity.setNote(dto.getNote());

        if (entity instanceof AbstractPayableDocument && dto instanceof AbstractPayableDocumentDto) {
            final var payableEntity = (AbstractPayableDocument<?>) entity;
            final var payableDto = (AbstractPayableDocumentDto) dto;
            payableEntity.setBalanceOid(payableDto.getBalanceOid());
            payableEntity.setPayments(payableDto.getPayments() == null ? null
                            : payableDto.getPayments()
                                            .stream()
                                            .map(Converter::toEntity)
                                            .collect(Collectors.toSet()));
        }
    }

    protected static Item toEntity(AbstractDocItemDto dto)
    {
        if (dto instanceof final PosDocItemDto posDto) {
            return toEntity(posDto);
        } else if (dto instanceof final DocItemDto docDto) {
            return toEntity(docDto);
        }
        return null;
    }

    public static PosStocktakingDto toDto(final Stocktaking entity)
    {
        return toBuilder(entity).build();
    }

    public static PosStocktakingDto.Builder toBuilder(final Stocktaking entity)
    {
        return PosStocktakingDto.builder()
                        .withId(entity.getId())
                        .withNumber(entity.getNumber())
                        .withUserOid(entity.getUserOid())
                        .withStartAt(entity.getStartAt())
                        .withEndAt(entity.getEndAt())
                        .withStatus(entity.getStatus())
                        .withWarehouseOid(entity.getWarehouseOid());
    }

    public static StockTakingEntryDto toDto(StocktakingEntry entity)
    {
        final var product = INSTANCE.productService.getProduct(entity.getProductOid());
        final var createdAt = LocalDateTime.ofInstant(entity.getCreatedDate(),
                        ZoneId.of(INSTANCE.configProperties.getBeInst().getTimeZone()));
        return StockTakingEntryDto.builder()
                        .withId(entity.getId())
                        .withProduct(ProductHeadDto.builder()
                                        .withOid(product.getOid())
                                        .withSku(product.getSKU())
                                        .withDescription(product.getDescription())
                                        .withUoM(product.getUoM())
                                        .build())
                        .withQuantity(entity.getQuantity())
                        .withComment(entity.getComment())
                        .withCreatedAt(createdAt)
                        .build();
    }

    public static LogEntry toEntity(final LogEntryDto dto)
    {
        return new LogEntry()
                        .setIdent(dto.getIdent())
                        .setKey(dto.getKey())
                        .setValue(dto.getValue())
                        .setLevel(dto.getLevel());
    }

    public static LogEntryDto toDto(final LogEntry entity)
    {
        return LogEntryDto.builder()
                        .withIdent(entity.getIdent())
                        .withKey(entity.getKey())
                        .withValue(entity.getValue())
                        .withLevel(entity.getLevel())
                        .withMessage(entity.getMessage())
                        .withCreatedAt(Utils.toOffset(entity.getCreatedDate(),
                                        INSTANCE.configProperties.getBeInst().getTimeZone()))
                        .build();
    }

    public static PromotionEntity toEntity(final Promotion dto)
    {
        return new PromotionEntity()
                        .setOid(dto.getOid())
                        .setName(dto.getName())
                        .setDescription(dto.getDescription())
                        .setLabel(dto.getLabel())
                        .setPriority(dto.getPriority())
                        .setStartDateTime(dto.getStartDateTime())
                        .setEndDateTime(dto.getEndDateTime())
                        .setSourceConditions(dto.getSourceConditions())
                        .setTargetConditions(dto.getTargetConditions())
                        .setActions(dto.getActions());
    }

    public static Promotion toDto(final PromotionEntity entity)
    {
        return Promotion.builder()
                        .withOid(entity.getOid())
                        .withName(entity.getName())
                        .withDescription(entity.getDescription())
                        .withLabel(entity.getLabel())
                        .withPriority(entity.getPriority())
                        .withStartDateTime(entity.getStartDateTime())
                        .withEndDateTime(entity.getEndDateTime())
                        .withSourceConditions(entity.getSourceConditions())
                        .withTargetConditions(entity.getTargetConditions())
                        .withActions(entity.getActions())
                        .build();
    }

    public static PromotionHeaderDto toHeaderDto(final PromotionEntity entity)
    {
        return PromotionHeaderDto.builder()
                        .withOid(entity.getOid())
                        .withName(entity.getName())
                        .withDescription(entity.getDescription())
                        .withLabel(entity.getLabel())
                        .withPriority(entity.getPriority())
                        .withStartDateTime(entity.getStartDateTime())
                        .withEndDateTime(entity.getEndDateTime())
                        .build();
    }

    public static AbstractPayableDocumentDto toDto(final AbstractPayableDocument<?> entity)
    {
        if (entity instanceof Receipt) {
            return toDto((Receipt) entity);
        } else if (entity instanceof Invoice) {
            return toDto((Invoice) entity);
        } else if (entity instanceof Ticket) {
            return toDto((Ticket) entity);
        } else if (entity instanceof CreditNote) {
            return toDto((CreditNote) entity);
        }
        return null;
    }

    public static void clone(final AbstractDocument<?> fromEntity,
                             final AbstractDocument<?> toEntity)
    {
        toEntity.setContactOid(fromEntity.getContactOid())
                        .setCrossTotal(fromEntity.getCrossTotal())
                        .setCurrency(fromEntity.getCurrency())
                        .setDate(fromEntity.getDate())
                        .setDiscount(fromEntity.getDiscount())
                        .setEmployeeRelations(fromEntity.getEmployeeRelations())
                        .setExchangeRate(fromEntity.getExchangeRate())
                        .setItems(fromEntity.getItems().stream()
                                        .map(Converter::clone)
                                        .toList())
                        .setNetTotal(fromEntity.getNetTotal())
                        .setNote(fromEntity.getNote())
                        .setPayableAmount(fromEntity.getPayableAmount())
                        .setTaxes(fromEntity.getTaxes())
                        .setWorkspaceOid(fromEntity.getWorkspaceOid());

        if (fromEntity instanceof AbstractPayableDocument<?> && toEntity instanceof AbstractPayableDocument<?>) {
            ((AbstractPayableDocument<?>) toEntity)
                            .setPayments(((AbstractPayableDocument<?>) fromEntity).getPayments() == null ? null
                                            : ((AbstractPayableDocument<?>) fromEntity).getPayments().stream()
                                                            .map(Converter::clone)
                                                            .collect(Collectors.toSet()));
        }
    }

    public static Item clone(final Item fromItem)
    {
        return new Item()
                        .setCrossPrice(fromItem.getCrossPrice())
                        .setCrossUnitPrice(fromItem.getCrossUnitPrice())
                        .setCurrency(fromItem.getCurrency())
                        .setExchangeRate(fromItem.getExchangeRate())
                        .setIndex(fromItem.getIndex())
                        .setNetPrice(fromItem.getNetPrice())
                        .setNetUnitPrice(fromItem.getNetUnitPrice())
                        .setParentIdx(fromItem.getParentIdx())
                        .setProductOid(fromItem.getProductOid())
                        .setStandInOid(fromItem.getStandInOid())
                        .setQuantity(fromItem.getQuantity())
                        .setRemark(fromItem.getRemark())
                        .setBomOid(fromItem.getBomOid())
                        .setTaxes(fromItem.getTaxes());
    }

    public static IPayment clone(final IPayment fromPayment)
    {
        return switch (fromPayment.getType()) {
            case CARD -> {
                final var from = (PaymentCard) fromPayment;
                yield new PaymentCard()
                                .setOid(from.getOid())
                                .setAmount(from.getAmount())
                                .setCollectOrderId(from.getCollectOrderId())
                                .setCurrency(from.getCurrency())
                                .setType(from.getType());
            }

            case CASH -> {
                final var from = (PaymentCash) fromPayment;
                yield new PaymentCash()
                                .setOid(from.getOid())
                                .setAmount(from.getAmount())
                                .setCollectOrderId(from.getCollectOrderId())
                                .setCurrency(from.getCurrency())
                                .setType(from.getType());
            }
            case CHANGE -> {
                final var from = (PaymentChange) fromPayment;
                yield new PaymentChange()
                                .setOid(from.getOid())
                                .setAmount(from.getAmount())
                                .setCollectOrderId(from.getCollectOrderId())
                                .setCurrency(from.getCurrency())
                                .setType(from.getType());
            }
            case ELECTRONIC -> {
                final var from = (PaymentElectronic) fromPayment;
                yield new PaymentElectronic()
                                .setOid(from.getOid())
                                .setAmount(from.getAmount())
                                .setCollectOrderId(from.getCollectOrderId())
                                .setCurrency(from.getCurrency())
                                .setType(from.getType());
            }
            case FREE -> {
                final var from = (PaymentFree) fromPayment;
                yield new PaymentFree()
                                .setOid(from.getOid())
                                .setAmount(from.getAmount())
                                .setCollectOrderId(from.getCollectOrderId())
                                .setCurrency(from.getCurrency())
                                .setType(from.getType());
            }
            case LOYALTY_POINTS -> {
                final var from = (PaymentLoyaltyPoints) fromPayment;
                yield new PaymentLoyaltyPoints()
                                .setOid(from.getOid())
                                .setAmount(from.getAmount())
                                .setCollectOrderId(from.getCollectOrderId())
                                .setCurrency(from.getCurrency())
                                .setType(from.getType());
            }
        };
    }

    public static Payment clone(final Payment fromPayment)
    {
        return new Payment().setAmount(fromPayment.getAmount())
                        .setCardLabel(fromPayment.getCardLabel())
                        .setCardTypeId(fromPayment.getCardTypeId())
                        .setCollectOrderId(fromPayment.getCollectOrderId())
                        .setCurrency(fromPayment.getCurrency())
                        .setExchangeRate(fromPayment.getExchangeRate())
                        .setMappingKey(fromPayment.getMappingKey())
                        .setType(fromPayment.getType());
    }

    public static PromoInfoDto toDto(final PromotionInfoDto promoInfo)
    {
        return PromoInfoDto.builder()
                        .withNetTotalDiscount(promoInfo.getNetTotalDiscount())
                        .withCrossTotalDiscount(promoInfo.getCrossTotalDiscount())
                        .withPromotionOids(promoInfo.getPromotionOids())
                        .withDetails(promoInfo.getDetails().stream().map(Converter::toDto).toList())
                        .build();
    }

    public static PromoDetailDto toDto(final IPromotionDetail detail)
    {
        return PromoDetailDto.builder()
                        .withNetBase(detail.getNetBase())
                        .withNetUnitBase(detail.getNetUnitBase())
                        .withNetUnitDiscount(detail.getNetUnitDiscount())
                        .withNetDiscount(detail.getNetDiscount())
                        .withCrossUnitDiscount(detail.getCrossUnitDiscount())
                        .withCrossDiscount(detail.getCrossDiscount())
                        .withPositionIndex(detail.getPositionIndex())
                        .withPromotionOid(detail.getPromotionOid())
                        .build();
    }

    public static PosFile toEntity(final FileDto dto)
    {
        return new PosFile()
                        .setId(dto.getOid())
                        .setOid(dto.getOid())
                        .setName(dto.getName())
                        .setDescription(dto.getDescription())
                        .setFileName(dto.getFileName().trim())
                        .setTags(dto.getTags());
    }

    public static PosFileDto toDto(final PosFile entity)
    {
        final var fileName = PosFileService.evalFileName(entity);
        final var path = INSTANCE.configProperties.getBeInst().getFileConfig().getPath();
        return PosFileDto.builder()
                        .withOid(entity.getOid())
                        .withName(entity.getName())
                        .withDescription(entity.getDescription())
                        .withFileName(entity.getFileName())
                        .withPath(path + fileName)
                        .withTags(entity.getTags())
                        .build();
    }

    public static PrintPosOrderDto toPrintDto(final Order entity)
    {
        return (PrintPosOrderDto) PrintPosOrderDto.builder()
                        .withId(entity.getId())
                        .withOID(entity.getOid())
                        .withNumber(entity.getNumber())
                        .withContactOid(entity.getContactOid())
                        .withCurrency(entity.getCurrency())
                        .withExchangeRate(entity.getExchangeRate())
                        .withDate(entity.getDate())
                        .withItems(entity.getItems() == null ? Collections.emptySet()
                                        : entity.getItems().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .withStatus(entity.getStatus())
                        .withNetTotal(entity.getNetTotal())
                        .withCrossTotal(entity.getCrossTotal())
                        .withPayableAmount(entity.getPayableAmount())
                        .withTaxes(entity.getTaxes() == null ? null
                                        : entity.getTaxes().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .withSpot(toSpotDto(entity.getSpot()))
                        .withDiscount(entity.getDiscount() == null ? null : toDto(entity.getDiscount()))
                        .withNote(entity.getNote())
                        .withPayableOid(entity.getPayableOid())
                        .withShoutout(entity.getShoutout())
                        .withEmployeeRelations(entity.getEmployeeRelations() == null ? Collections.emptySet()
                                        : entity.getEmployeeRelations().stream().map(Converter::toDto)
                                                        .collect(Collectors.toSet()))
                        .withOrderOptionKey(entity.getOrderOptionKey())
                        .build();
    }
}
