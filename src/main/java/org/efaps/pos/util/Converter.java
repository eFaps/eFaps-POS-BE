/*
 * Copyright 2003 - 2019 The eFaps Team
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
package org.efaps.pos.util;

import java.util.Collections;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.efaps.pos.dto.BalanceDto;
import org.efaps.pos.dto.CardDto;
import org.efaps.pos.dto.CategoryDto;
import org.efaps.pos.dto.CollectOrderDto;
import org.efaps.pos.dto.ContactDto;
import org.efaps.pos.dto.DiscountDto;
import org.efaps.pos.dto.DocItemDto;
import org.efaps.pos.dto.DocumentHeadDto;
import org.efaps.pos.dto.DocumentHeadDto.Builder;
import org.efaps.pos.dto.FloorDto;
import org.efaps.pos.dto.IndicationDto;
import org.efaps.pos.dto.IndicationSetDto;
import org.efaps.pos.dto.InventoryEntryDto;
import org.efaps.pos.dto.InvoiceDto;
import org.efaps.pos.dto.JobDto;
import org.efaps.pos.dto.OrderDto;
import org.efaps.pos.dto.PayableHeadDto;
import org.efaps.pos.dto.PaymentDto;
import org.efaps.pos.dto.PosBalanceDto;
import org.efaps.pos.dto.PosDocItemDto;
import org.efaps.pos.dto.PosDto;
import org.efaps.pos.dto.PosInventoryEntryDto;
import org.efaps.pos.dto.PosInvoiceDto;
import org.efaps.pos.dto.PosOrderDto;
import org.efaps.pos.dto.PosReceiptDto;
import org.efaps.pos.dto.PosSpotDto;
import org.efaps.pos.dto.PosTicketDto;
import org.efaps.pos.dto.PosUserDto;
import org.efaps.pos.dto.PrintCmdDto;
import org.efaps.pos.dto.PrinterDto;
import org.efaps.pos.dto.ProductDto;
import org.efaps.pos.dto.ProductRelationDto;
import org.efaps.pos.dto.ReceiptDto;
import org.efaps.pos.dto.SequenceDto;
import org.efaps.pos.dto.SpotDto;
import org.efaps.pos.dto.TaxDto;
import org.efaps.pos.dto.TaxEntryDto;
import org.efaps.pos.dto.TicketDto;
import org.efaps.pos.dto.UserDto;
import org.efaps.pos.dto.WarehouseDto;
import org.efaps.pos.dto.WorkspaceDto;
import org.efaps.pos.entity.AbstractDocument;
import org.efaps.pos.entity.AbstractDocument.TaxEntry;
import org.efaps.pos.entity.Balance;
import org.efaps.pos.entity.Category;
import org.efaps.pos.entity.CollectOrder;
import org.efaps.pos.entity.Contact;
import org.efaps.pos.entity.Discount;
import org.efaps.pos.entity.Indication;
import org.efaps.pos.entity.IndicationSet;
import org.efaps.pos.entity.InventoryEntry;
import org.efaps.pos.entity.Invoice;
import org.efaps.pos.entity.Job;
import org.efaps.pos.entity.Order;
import org.efaps.pos.entity.Payment;
import org.efaps.pos.entity.Pos;
import org.efaps.pos.entity.Printer;
import org.efaps.pos.entity.Product;
import org.efaps.pos.entity.ProductRelation;
import org.efaps.pos.entity.Receipt;
import org.efaps.pos.entity.Sequence;
import org.efaps.pos.entity.Spot;
import org.efaps.pos.entity.Tax;
import org.efaps.pos.entity.Ticket;
import org.efaps.pos.entity.User;
import org.efaps.pos.entity.Warehouse;
import org.efaps.pos.entity.Workspace;
import org.efaps.pos.entity.Workspace.Card;
import org.efaps.pos.entity.Workspace.Floor;
import org.efaps.pos.entity.Workspace.PrintCmd;
import org.efaps.pos.projection.DocumentHead;
import org.efaps.pos.projection.PayableHead;
import org.efaps.pos.service.ContactService;
import org.efaps.pos.service.DocumentService;
import org.efaps.pos.service.InventoryService;
import org.efaps.pos.service.ProductService;
import org.efaps.pos.service.UserService;
import org.springframework.stereotype.Component;

@Component
public final class Converter
{
    private static Converter INSTANCE;

    private final ProductService productService;
    private final InventoryService inventoryService;
    private final DocumentService documentService;
    private final ContactService contactService;
    private final UserService userService;

    public Converter(final ProductService _productService,
                     final InventoryService _inventoryService,
                     final DocumentService _documentService,
                     final ContactService _contactService,
                     final UserService _userService)
    {
        INSTANCE = this;
        productService = _productService;
        inventoryService = _inventoryService;
        documentService = _documentService;
        contactService = _contactService;
        userService = _userService;
    }

    public static Receipt toEntity(final PosReceiptDto _dto)
    {
        return new Receipt()
                        .setId(_dto.getId())
                        .setOid(_dto.getOid())
                        .setNumber(_dto.getNumber())
                        .setCurrency(_dto.getCurrency())
                        .setStatus(_dto.getStatus())
                        .setDate(_dto.getDate())
                        .setItems(_dto.getItems().stream()
                                        .map(_item -> Converter.toEntity((PosDocItemDto) _item))
                                        .collect(Collectors.toSet()))
                        .setNetTotal(_dto.getNetTotal())
                        .setCrossTotal(_dto.getCrossTotal())
                        .setContactOid(_dto.getContactOid())
                        .setWorkspaceOid(_dto.getWorkspaceOid())
                        .setTaxes(_dto.getTaxes() ==  null
                            ? null
                            : _dto.getTaxes().stream()
                                .map(_tax -> Converter.toEntity(_tax))
                                .collect(Collectors.toSet()))
                        .setPayments(_dto.getPayments() == null
                            ? null
                            : _dto.getPayments().stream()
                                .map(_payment -> Converter.toEntity(_payment))
                                .collect(Collectors.toSet()))
                        .setBalanceOid(_dto.getBalanceOid())
                        .setDiscount(_dto.getDiscount() == null ? null : toEntity(_dto.getDiscount()));
    }

    public static Invoice toEntity(final PosInvoiceDto _dto)
    {
        return new Invoice()
                        .setId(_dto.getId())
                        .setOid(_dto.getOid())
                        .setNumber(_dto.getNumber())
                        .setCurrency(_dto.getCurrency())
                        .setStatus(_dto.getStatus())
                        .setDate(_dto.getDate())
                        .setItems(_dto.getItems().stream()
                                        .map(_item -> Converter.toEntity((PosDocItemDto) _item))
                                        .collect(Collectors.toSet()))
                        .setNetTotal(_dto.getNetTotal())
                        .setCrossTotal(_dto.getCrossTotal())
                        .setContactOid(_dto.getContactOid())
                        .setWorkspaceOid(_dto.getWorkspaceOid())
                        .setTaxes(_dto.getTaxes() ==  null
                            ? null
                            : _dto.getTaxes().stream()
                                .map(_tax -> Converter.toEntity(_tax))
                                .collect(Collectors.toSet()))
                        .setPayments(_dto.getPayments() == null
                            ? null
                            : _dto.getPayments().stream()
                                .map(_payment -> Converter.toEntity(_payment))
                                .collect(Collectors.toSet()))
                        .setBalanceOid(_dto.getBalanceOid())
                        .setDiscount(_dto.getDiscount() == null ? null : toEntity(_dto.getDiscount()));
    }

    public static Ticket toEntity(final PosTicketDto _dto)
    {
       return new Ticket()
                        .setId(_dto.getId())
                        .setOid(_dto.getOid())
                        .setNumber(_dto.getNumber())
                        .setCurrency(_dto.getCurrency())
                        .setStatus(_dto.getStatus())
                        .setDate(_dto.getDate())
                        .setItems(_dto.getItems().stream()
                                        .map(_item -> Converter.toEntity((PosDocItemDto) _item))
                                        .collect(Collectors.toSet()))
                        .setNetTotal(_dto.getNetTotal())
                        .setCrossTotal(_dto.getCrossTotal())
                        .setContactOid(_dto.getContactOid())
                        .setWorkspaceOid(_dto.getWorkspaceOid())
                        .setTaxes(_dto.getTaxes() ==  null
                            ? null
                            : _dto.getTaxes().stream()
                                .map(_tax -> Converter.toEntity(_tax))
                                .collect(Collectors.toSet()))
                        .setPayments(_dto.getPayments() == null
                            ? null
                            : _dto.getPayments().stream()
                                .map(_payment -> Converter.toEntity(_payment))
                                .collect(Collectors.toSet()))
                        .setBalanceOid(_dto.getBalanceOid())
                        .setDiscount(_dto.getDiscount() == null ? null : toEntity(_dto.getDiscount()));
    }

    public static Order toEntity(final PosOrderDto _dto)
    {
        return new Order()
                        .setId(_dto.getId())
                        .setOid(_dto.getOid())
                        .setNumber(_dto.getNumber())
                        .setCurrency(_dto.getCurrency())
                        .setDate(_dto.getDate())
                        .setCurrency(_dto.getCurrency())
                        .setItems(_dto.getItems().stream()
                                        .map(_item -> Converter.toEntity((PosDocItemDto) _item))
                                        .collect(Collectors.toSet()))
                        .setNetTotal(_dto.getNetTotal())
                        .setCrossTotal(_dto.getCrossTotal())
                        .setStatus(_dto.getStatus())
                        .setTaxes(_dto.getTaxes() ==  null
                            ? null
                            : _dto.getTaxes().stream()
                                .map(_tax -> Converter.toEntity(_tax))
                                .collect(Collectors.toSet()))
                        .setSpot(_dto.getSpot() == null ? null : toEntity(_dto.getSpot()))
                        .setPayableOid(_dto.getPayableOid())
                        .setDiscount(_dto.getDiscount() == null ? null : toEntity(_dto.getDiscount()))
                        .setShoutout(_dto.getShoutout());
    }

    public static Spot toEntity(final PosSpotDto _dto)
    {
        return new Spot().setId(_dto.getId())
                        .setLabel(_dto.getLabel());
    }

    public static Spot toEntity(final SpotDto _dto)
    {
        return new Spot().setId(_dto.getOid())
                        .setLabel(_dto.getLabel());
    }

    public static SpotDto toDto(final Spot _entity)
    {
        return SpotDto.builder()
                        .withOID(_entity.getId())
                        .withLabel(_entity.getLabel())
                        .build();
    }

    public static AbstractDocument.Item toEntity(final PosDocItemDto _dto) {
        return new AbstractDocument.Item()
                        .setOid(_dto.getOid())
                        .setIndex(_dto.getIndex())
                        .setCrossPrice(_dto.getCrossPrice())
                        .setCrossUnitPrice(_dto.getCrossUnitPrice())
                        .setNetPrice(_dto.getNetPrice())
                        .setNetUnitPrice(_dto.getNetUnitPrice())
                        .setQuantity(_dto.getQuantity())
                        .setRemark(_dto.getRemark())
                        .setProductOid(_dto.getProduct() == null
                            ? _dto.getProductOid()
                            : _dto.getProduct().getOid())
                        .setTaxes(_dto.getTaxes() ==  null
                            ? null
                            : _dto.getTaxes().stream()
                                .map(_tax -> Converter.toEntity(_tax))
                                .collect(Collectors.toSet()));
    }

    public static Product toEntity(final ProductDto _dto)
    {
        final Product ret = new Product()
                        .setOid(_dto.getOid())
                        .setSKU(_dto.getSku())
                        .setType(_dto.getType())
                        .setImageOid(_dto.getImageOid())
                        .setDescription(_dto.getDescription())
                        .setNetPrice(_dto.getNetPrice())
                        .setCrossPrice(_dto.getCrossPrice())
                        .setCategoryOids(_dto.getCategoryOids())
                        .setTaxes(_dto.getTaxes().stream()
                                        .map(_tax -> _tax == null ? null : toEntity(_tax))
                                        .collect(Collectors.toSet()))
                        .setUoM(_dto.getUoM())
                        .setUoMCode(_dto.getUoMCode())
                        .setRelations(_dto.getRelations().stream()
                                        .map(rel -> toEntity(rel))
                                        .collect(Collectors.toSet()))
                        .setIndicationSets(_dto.getIndicationSets().stream()
                                        .map(rel -> toEntity(rel))
                                        .collect(Collectors.toSet()));
        return ret;
    }


    public static IndicationSet toEntity(final IndicationSetDto _dto)
    {
        return new IndicationSet().setId(_dto.getOid())
                        .setName(_dto.getName())
                        .setRequired(_dto.isRequired())
                        .setIndications(_dto.getIndications().stream()
                                        .map(dto -> toEntity(dto))
                                        .collect(Collectors.toSet()));
    }

    public static IndicationSetDto toDto(final IndicationSet _entity)
    {
        return IndicationSetDto.builder()
                        .withOID(_entity.getId())
                        .withName(_entity.getName())
                        .withRequired(_entity.isRequired())
                        .withIndications(_entity.getIndications().stream()
                                        .map(entity -> toDto(entity))
                                        .collect(Collectors.toSet()))
                        .build();
    }

    public static Indication toEntity(final IndicationDto _dto)
    {
        return new Indication().setId(_dto.getOid())
                        .setValue(_dto.getValue());
    }

    public static IndicationDto toDto(final Indication _entity)
    {
        return IndicationDto.builder()
                        .withOID(_entity.getId())
                        .withValue(_entity.getValue())
                        .build();
    }

    public static ProductDto toDto(final Product _entity)
    {
        return _entity == null
                    ? null
                    : ProductDto.builder()
                        .withSKU(_entity.getSKU())
                        .withType(_entity.getType())
                        .withDescription(_entity.getDescription())
                        .withImageOid(_entity.getImageOid())
                        .withOID(_entity.getOid())
                        .withNetPrice(_entity.getNetPrice())
                        .withCrossPrice(_entity.getCrossPrice())
                        .withCategoryOids(_entity.getCategoryOids())
                        .withTaxes(_entity.getTaxes() == null
                                ? null
                                :_entity.getTaxes().stream()
                                        .map(_tax -> toDto(_tax))
                                        .collect(Collectors.toSet()))
                        .withUoM(_entity.getUoM())
                        .withUoMCode(_entity.getUoMCode())
                        .withRelations(_entity.getRelations() == null
                                ? null
                                : _entity.getRelations().stream()
                                        .map(rel -> toDto(rel))
                                        .collect(Collectors.toSet()))
                        .withIndicationSets(_entity.getIndicationSets() == null
                                ? null
                                : _entity.getIndicationSets().stream()
                                        .map(set -> toDto(set))
                                        .collect(Collectors.toSet()))
                        .build();
    }

    public static PosUserDto toDto(final User _user)
    {
        return _user == null
                    ? null
                    : PosUserDto.builder()
                        .withUsername(_user.getUsername())
                        .withFirstName(_user.getFirstName())
                        .withSurName(_user.getSurName())
                        .build();
    }

    public static User toEntity(final UserDto _dto)
    {
        return new User().setOid(_dto.getOid())
                        .setUsername(_dto.getUsername())
                        .setPassword(_dto.getPassword())
                        .setFirstName(_dto.getFirstName())
                        .setSurName(_dto.getSurName())
                        .setVisible(_dto.isVisible())
                        .setRoles(_dto.getRoles())
                        .setWorkspaceOids(_dto.getWorkspaceOids());
    }

    public static Sequence toEntity(final SequenceDto _dto)
    {
        return new Sequence().setOid(_dto.getOid())
                        .setFormat(_dto.getFormat())
                        .setSeq(_dto.getSeq());
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
                        .withPrintCmds(_entity.getPrintCmds() == null
                            ? Collections.emptySet()
                            : _entity.getPrintCmds().stream()
                                .map(cmd -> Converter.toDto(cmd))
                                .collect(Collectors.toSet()))
                        .withPosLayout(_entity.getPosLayout())
                        .withDiscounts(_entity.getDiscounts() == null
                            ? Collections.emptySet()
                            : _entity.getDiscounts().stream()
                                .map(discount -> Converter.toDto(discount))
                                .collect(Collectors.toSet()))
                        .withCards(_entity.getCards() == null
                            ? Collections.emptySet()
                            : _entity.getCards().stream()
                                .map(card -> Converter.toDto(card))
                                .collect(Collectors.toSet()))
                        .withGridShowPrice(_entity.isGridShowPrice())
                        .withGridSize(_entity.getGridSize())
                        .withFloors(_entity.getFloors() == null
                                ? Collections.emptyList()
                                : _entity.getFloors().stream()
                                        .map(floor -> Converter.toDto(floor))
                                        .collect(Collectors.toList()))
                        .build();
    }

    public static Workspace toEntity(final WorkspaceDto _dto)
    {
        return new Workspace()
                        .setOid(_dto.getOid())
                        .setName(_dto.getName())
                        .setPosOid(_dto.getPosOid())
                        .setDocTypes(_dto.getDocTypes())
                        .setSpotConfig(_dto.getSpotConfig())
                        .setSpotCount(_dto.getSpotCount())
                        .setWarehouseOid(_dto.getWarehouseOid())
                        .setPrintCmds(_dto.getPrintCmds() == null
                            ? null
                            : _dto.getPrintCmds().stream()
                                    .map(cmd -> Converter.toEntity(cmd))
                                    .collect(Collectors.toSet()))
                        .setPosLayout(_dto.getPosLayout())
                        .setDiscounts(_dto.getDiscounts() == null
                            ? null
                            : _dto.getDiscounts().stream()
                                    .map(discount -> Converter.toEntity(discount))
                                    .collect(Collectors.toSet()))
                        .setCards(_dto.getCards() == null
                            ? null
                            : _dto.getCards().stream()
                                    .map(card -> Converter.toEntity(card))
                                    .collect(Collectors.toSet()))
                        .setGridShowPrice(_dto.isGridShowPrice())
                        .setGridSize(_dto.getGridSize())
                        .setFloors(_dto.getFloors() == null
                            ? null
                            : _dto.getFloors().stream()
                                    .map(floor -> Converter.toEntity(floor))
                                    .collect(Collectors.toList()));
    }

    public static Floor toEntity(final FloorDto _dto) {
        return new Floor()
                        .setOid(_dto.getOid())
                        .setImageOid(_dto.getImageOid())
                        .setName(_dto.getName())
                        .setSpots(_dto.getSpots() == null
                            ? null
                            : _dto.getSpots().stream()
                                    .map(spot -> Converter.toEntity(spot))
                                    .collect(Collectors.toList()));
    }

    public static FloorDto toDto(final Floor _entity)
    {
        return FloorDto.builder()
                        .withOID(_entity.getOid())
                        .withImageOid(_entity.getImageOid())
                        .withName(_entity.getName())
                        .withSpots(_entity.getSpots() == null
                            ? null
                            : _entity.getSpots().stream()
                                    .map(spot -> Converter.toDto(spot))
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
                        .build();
    }

    public static Pos toEntity(final PosDto _dto)
    {
        final Pos ret = new Pos()
                        .setOid(_dto.getOid())
                        .setName(_dto.getName())
                        .setCurrency(_dto.getCurrency())
                        .setDefaultContactOid(_dto.getDefaultContactOid())
                        .setReceiptSeqOid(_dto.getReceiptSeqOid())
                        .setInvoiceSeqOid(_dto.getInvoiceSeqOid())
                        .setTicketSeqOid(_dto.getTicketSeqOid());
        return ret;
    }

    public static CategoryDto toDto(final Category _entity)
    {
        return CategoryDto.builder()
                        .withOID(_entity.getOid())
                        .withName(_entity.getName())
                        .withWeight(_entity.getWeight())
                        .build();
    }

    public static Category toEntity(final CategoryDto _dto)
    {
        return new Category()
                        .setName(_dto.getName())
                        .setOid(_dto.getOid())
                        .setWeight(_dto.getWeight());
    }

    public static Contact toEntity(final ContactDto _dto)
    {
        return new Contact()
                        .setId(_dto.getId())
                        .setOid(_dto.getOid())
                        .setName(_dto.getName())
                        .setIdType(_dto.getIdType())
                        .setIdNumber(_dto.getIdNumber())
                        .setEmail(_dto.getEmail());
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
        return new Tax()
                        .setOid(_dto.getOid())
                        .setKey(_dto.getKey())
                        .setCatKey(_dto.getCatKey())
                        .setType(_dto.getType())
                        .setName(_dto.getName())
                        .setPercent(_dto.getPercent())
                        .setAmount(_dto.getAmount());
    }

    public static Payment toEntity(final PaymentDto _dto)
    {
        return new Payment()
                        .setOid(_dto.getOid())
                        .setType(_dto.getType())
                        .setAmount(_dto.getAmount())
                        .setCardTypeId(_dto.getCardTypeId())
                        .setCardLabel(_dto.getCardLabel());
    }

    public static PaymentDto toDto(final Payment _entity)
    {
        return PaymentDto.builder()
                        .withOID(_entity.getOid())
                        .withType(_entity.getType())
                        .withAmount(_entity.getAmount())
                        .withCardTypeId(_entity.getCardTypeId())
                        .withCardLabel(_entity.getCardLabel())
                        .build();
    }

    public static TaxEntry toEntity(final TaxEntryDto _dto)
    {
        final TaxEntry ret = new TaxEntry()
                        .setTax(_dto.getTax() == null ? null : Converter.toEntity(_dto.getTax()))
                        .setBase(_dto.getBase())
                        .setAmount(_dto.getAmount());
        return ret;
    }

    public static TaxEntryDto toDto(final TaxEntry _entity)
    {
        return TaxEntryDto.builder()
                        .withTax(Converter.toDto(_entity.getTax()))
                        .withBase(_entity.getBase())
                        .withAmount(_entity.getAmount())
                        .build();
    }

    public static PosOrderDto toDto(final Order _entity)
    {
        return PosOrderDto.builder()
                        .withId(_entity.getId())
                        .withOID(_entity.getOid())
                        .withNumber(_entity.getNumber())
                        .withCurrency(_entity.getCurrency())
                        .withDate(_entity.getDate())
                        .withItems(_entity.getItems() == null
                            ? Collections.emptySet()
                            : _entity.getItems().stream()
                                        .map(_item -> toDto(_item))
                                        .collect(Collectors.toSet()))
                        .withStatus(_entity.getStatus())
                        .withNetTotal(_entity.getNetTotal())
                        .withCrossTotal(_entity.getCrossTotal())
                        .withTaxes(_entity.getTaxes() == null
                            ? null
                            : _entity.getTaxes().stream()
                                .map(_tax -> Converter.toDto(_tax))
                                .collect(Collectors.toSet()))
                        .withSpot(toSpotDto(_entity.getSpot()))
                        .withDiscount(_entity.getDiscount() == null ? null : toDto(_entity.getDiscount()))
                        .withPayableOid(_entity.getPayableOid())
                        .withShoutout(_entity.getShoutout())
                        .build();
    }

    public static PosSpotDto toSpotDto(final Spot _spot) {
        return _spot == null
                        ? null
                        : PosSpotDto.builder()
                            .withId(_spot.getId())
                            .withLabel(_spot.getLabel()).build();
    }

    public static PosDocItemDto toDto(final AbstractDocument.Item _entity) {
        return PosDocItemDto.builder()
                        .withOID(_entity.getOid())
                        .withIndex(_entity.getIndex())
                        .withCrossPrice(_entity.getCrossPrice())
                        .withCrossUnitPrice(_entity.getCrossUnitPrice())
                        .withNetPrice(_entity.getNetPrice())
                        .withNetUnitPrice(_entity.getNetUnitPrice())
                        .withQuantity(_entity.getQuantity())
                        .withProductOid(_entity.getProductOid())
                        .withRemark(_entity.getRemark())
                        .withProduct(_entity.getProductOid() == null
                            ? null
                            : Converter.toDto(INSTANCE.productService.getProduct(_entity.getProductOid())))
                        .withTaxes(_entity.getTaxes() == null
                            ? null
                            : _entity.getTaxes().stream()
                                .map(_tax -> Converter.toDto(_tax))
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
                        .withContactOid(_entity.getContactOid())
                        .withWorkspaceOid(_entity.getWorkspaceOid())
                        .withItems(_entity.getItems() == null
                            ? null
                            : _entity.getItems().stream()
                                        .map(_item -> toDto(_item))
                                        .collect(Collectors.toSet()))
                        .withTaxes(_entity.getTaxes() == null
                            ? null
                            : _entity.getTaxes().stream()
                                .map(_tax -> Converter.toDto(_tax))
                                .collect(Collectors.toSet()))
                        .withPayments(_entity.getPayments() == null
                            ? null
                            : _entity.getPayments().stream()
                                 .map(_item -> toDto(_item))
                                 .collect(Collectors.toSet()))
                        .withBalanceOid(_entity.getBalanceOid())
                        .withDiscount(_entity.getDiscount() == null ? null : toDto(_entity.getDiscount()))
                        .build();
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
                        .withStatus(_entity.getStatus())
                        .withContactOid(_entity.getContactOid())
                        .withWorkspaceOid(_entity.getWorkspaceOid())
                        .withItems(_entity.getItems() == null
                            ? null
                            : _entity.getItems().stream()
                                        .map(_item -> toDto(_item))
                                        .collect(Collectors.toSet()))
                        .withTaxes(_entity.getTaxes() == null
                            ? null
                            : _entity.getTaxes().stream()
                                .map(_tax -> Converter.toDto(_tax))
                                .collect(Collectors.toSet()))
                        .withPayments(_entity.getPayments() == null
                            ? null
                            : _entity.getPayments().stream()
                                .map(_item -> toDto(_item))
                                .collect(Collectors.toSet()))
                        .withBalanceOid(_entity.getBalanceOid())
                        .withDiscount(_entity.getDiscount() == null ? null : toDto(_entity.getDiscount()))
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
                        .withStatus(_entity.getStatus())
                        .withContactOid(_entity.getContactOid())
                        .withWorkspaceOid(_entity.getWorkspaceOid())
                        .withItems(_entity.getItems() == null
                            ? null
                            : _entity.getItems().stream()
                                        .map(_item -> toDto(_item))
                                        .collect(Collectors.toSet()))
                        .withTaxes(_entity.getTaxes() == null
                            ? null
                            : _entity.getTaxes().stream()
                                .map(_tax -> Converter.toDto(_tax))
                                .collect(Collectors.toSet()))
                        .withPayments(_entity.getPayments() == null
                            ? null
                            : _entity.getPayments().stream()
                                .map(_item -> toDto(_item))
                                .collect(Collectors.toSet()))
                        .withBalanceOid(_entity.getBalanceOid())
                        .withDiscount(_entity.getDiscount() == null ? null : toDto(_entity.getDiscount()))
                        .build();
    }

    public static ContactDto toDto(final Contact _entity) {
        return _entity == null
                    ? null
                    : ContactDto.builder()
                        .withId(_entity.getId())
                        .withOID(_entity.getOid())
                        .withName(_entity.getName())
                        .withIdType(_entity.getIdType())
                        .withIdNumber(_entity.getIdNumber())
                        .withEmail(_entity.getEmail())
                        .build();
    }

    public static OrderDto toOrderDto(final Order _entity)
    {
        return OrderDto.builder()
                        .withId(_entity.getId())
                        .withOID(_entity.getOid())
                        .withNumber(_entity.getNumber())
                        .withDate(_entity.getDate())
                        .withCurrency(_entity.getCurrency())
                        .withStatus(_entity.getStatus())
                        .withCrossTotal(_entity.getCrossTotal())
                        .withNetTotal(_entity.getNetTotal())
                        .withContactOid(_entity.getContactOid())
                        .withWorkspaceOid(_entity.getWorkspaceOid())
                        .withItems(_entity.getItems() == null
                            ? null
                            : _entity.getItems().stream()
                                    .map(_item -> toItemDto(_item))
                                    .collect(Collectors.toSet()))
                        .withTaxes(_entity.getTaxes() == null
                            ? null
                            : _entity.getTaxes().stream()
                                    .map(_tax -> Converter.toDto(_tax))
                                    .collect(Collectors.toSet()))
                        .withPayableOid(_entity.getPayableOid())
                        .build();
    }

    public static ReceiptDto toReceiptDto(final Receipt _entity) {
        return ReceiptDto.builder()
                        .withId(_entity.getId())
                        .withOID(_entity.getOid())
                        .withNumber(_entity.getNumber())
                        .withDate(_entity.getDate())
                        .withCurrency(_entity.getCurrency())
                        .withStatus(_entity.getStatus())
                        .withCrossTotal(_entity.getCrossTotal())
                        .withNetTotal(_entity.getNetTotal())
                        .withContactOid(_entity.getContactOid())
                        .withWorkspaceOid(_entity.getWorkspaceOid())
                        .withItems(_entity.getItems() == null
                            ? null
                            : _entity.getItems().stream()
                                    .map(_item -> toItemDto(_item))
                                    .collect(Collectors.toSet()))
                        .withTaxes(_entity.getTaxes() == null
                            ? null
                            : _entity.getTaxes().stream()
                                .map(_tax -> Converter.toDto(_tax))
                                .collect(Collectors.toSet()))
                        .withPayments(_entity.getPayments() == null
                            ? null
                            : _entity.getPayments().stream()
                                .map(_item -> toDto(_item))
                                .collect(Collectors.toSet()))
                        .withBalanceOid(_entity.getBalanceOid())
                        .build();
    }

    public static InvoiceDto toInvoiceDto(final Invoice _entity) {
        return InvoiceDto.builder()
                        .withId(_entity.getId())
                        .withOID(_entity.getOid())
                        .withNumber(_entity.getNumber())
                        .withDate(_entity.getDate())
                        .withCurrency(_entity.getCurrency())
                        .withStatus(_entity.getStatus())
                        .withCrossTotal(_entity.getCrossTotal())
                        .withNetTotal(_entity.getNetTotal())
                        .withContactOid(_entity.getContactOid())
                        .withWorkspaceOid(_entity.getWorkspaceOid())
                        .withItems(_entity.getItems() == null
                            ? null
                            : _entity.getItems().stream()
                                    .map(_item -> toItemDto(_item))
                                    .collect(Collectors.toSet()))
                        .withTaxes(_entity.getTaxes() == null
                            ? null
                            : _entity.getTaxes().stream()
                                .map(_tax -> Converter.toDto(_tax))
                                .collect(Collectors.toSet()))
                        .withPayments(_entity.getPayments() == null
                            ? null
                            : _entity.getPayments().stream()
                                .map(_item -> toDto(_item))
                                .collect(Collectors.toSet()))
                        .withBalanceOid(_entity.getBalanceOid())
                        .build();
    }

    public static TicketDto toTicketDto(final Ticket _entity) {
        return TicketDto.builder()
                        .withId(_entity.getId())
                        .withOID(_entity.getOid())
                        .withNumber(_entity.getNumber())
                        .withDate(_entity.getDate())
                        .withCurrency(_entity.getCurrency())
                        .withStatus(_entity.getStatus())
                        .withCrossTotal(_entity.getCrossTotal())
                        .withNetTotal(_entity.getNetTotal())
                        .withContactOid(_entity.getContactOid())
                        .withWorkspaceOid(_entity.getWorkspaceOid())
                        .withItems(_entity.getItems() == null
                            ? null
                            : _entity.getItems().stream()
                                    .map(_item -> toItemDto(_item))
                                    .collect(Collectors.toSet()))
                        .withTaxes(_entity.getTaxes() == null
                            ? null
                            : _entity.getTaxes().stream()
                                .map(_tax -> Converter.toDto(_tax))
                                .collect(Collectors.toSet()))
                        .withPayments(_entity.getPayments() == null
                            ? null
                            : _entity.getPayments().stream()
                                .map(_item -> toDto(_item))
                                .collect(Collectors.toSet()))
                        .withBalanceOid(_entity.getBalanceOid())
                        .build();
    }

    public static DocItemDto toItemDto(final AbstractDocument.Item _entity) {
        return DocItemDto.builder()
                        .withOID(_entity.getOid())
                        .withIndex(_entity.getIndex())
                        .withCrossPrice(_entity.getCrossPrice())
                        .withCrossUnitPrice(_entity.getCrossUnitPrice())
                        .withNetPrice(_entity.getNetPrice())
                        .withNetUnitPrice(_entity.getNetUnitPrice())
                        .withQuantity(_entity.getQuantity())
                        .withProductOid(_entity.getProductOid())
                        .withRemark(_entity.getRemark())
                        .withTaxes(_entity.getTaxes() == null
                            ? null
                            : _entity.getTaxes().stream()
                                .map(_tax -> Converter.toDto(_tax))
                                .collect(Collectors.toSet()))
                        .build();
    }

    public static Warehouse toEntity(final WarehouseDto _dto)
    {
        return new Warehouse()
                        .setName(_dto.getName())
                        .setOid(_dto.getOid());
    }

    public static WarehouseDto toDto(final Warehouse _entity)
    {
        return WarehouseDto.builder()
                        .withOID(_entity.getOid())
                        .withName(_entity.getName())
                        .build();
    }

    public static InventoryEntry toEntity(final InventoryEntryDto _dto)
    {
        return new InventoryEntry()
                        .setOid(_dto.getOid())
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
                        .withProduct(_entity.getProductOid() == null
                            ? null
                            : Converter.toDto(INSTANCE.productService.getProduct(_entity.getProductOid())))
                        .withWarehouse(_entity.getWarehouseOid() == null
                            ? null
                            : Converter.toDto(INSTANCE.inventoryService.getWarehouse(_entity.getWarehouseOid())))
                        .build();
    }

    public static JobDto toDto(final Job _entity) {
        final Order order = INSTANCE.documentService.getOrder(_entity.getDocumentId());
        return JobDto.builder()
                        .withDocumentId(_entity.getDocumentId())
                        .withDocumentNumber(order.getNumber())
                        .withShoutout(_entity.getShoutout())
                        .withSpotNumber(order.getSpot() == null
                            ? null
                            : StringUtils.isEmpty(order.getSpot().getLabel())
                                            ? order.getSpot().getId()
                                            : order.getSpot().getLabel())
                        .withId(_entity.getId())
                        .withItems(_entity.getItems() == null
                            ?  Collections.emptySet()
                            : _entity.getItems().stream()
                                .map(item -> Converter.toDto(item))
                                .collect(Collectors.toSet()))
                        .build();
    }

    public static Printer toEntity(final PrinterDto _dto)
    {
        return new Printer()
                        .setOid(_dto.getOid())
                        .setName(_dto.getName())
                        .setType(_dto.getType());
    }

    public static PrinterDto toDto(final Printer _entity) {
        return PrinterDto.builder()
                        .withOID(_entity.getOid())
                        .withName(_entity.getName())
                        .withType(_entity.getType())
                        .build();
    }

    public static PrintCmdDto toDto(final PrintCmd _entity) {
        return PrintCmdDto.builder()
                        .withPrinterOid(_entity.getPrinterOid())
                        .withTarget(_entity.getTarget())
                        .withTargetOid(_entity.getTargetOid())
                        .withReportOid(_entity.getReportOid())
                        .build();
    }

    public static PrintCmd toEntity(final PrintCmdDto _dto)
    {
        return new PrintCmd()
                        .setPrinterOid(_dto.getPrinterOid())
                        .setTarget(_dto.getTarget())
                        .setTargetOid(_dto.getTargetOid())
                        .setReportOid(_dto.getReportOid());
    }

    public static Discount toEntity(final DiscountDto _dto)
    {
        return new Discount()
                        .setLabel(_dto.getLabel())
                        .setProductOid(_dto.getProductOid())
                        .setType(_dto.getType())
                        .setValue(_dto.getValue());
    }

    public static Card toEntity(final CardDto _dto)
    {
        return new Card()
                        .setLabel(_dto.getLabel())
                        .setCardTypeId(_dto.getCardTypeId());
    }

    public static CardDto toDto(final Card _entity)
    {
        return CardDto.builder()
                        .withLabel(_entity.getLabel())
                        .withCardTypeId(_entity.getCardTypeId())
                        .build();
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

    public static Balance toEntity(final BalanceDto _dto) {
        return new Balance()
                        .setOid(_dto.getOid())
                        .setId(_dto.getId())
                        .setNumber(_dto.getNumber())
                        .setKey(_dto.getKey())
                        .setUserOid(_dto.getUserOid())
                        .setStartAt(_dto.getStartAt())
                        .setEndAt(_dto.getEndAt())
                        .setStatus(_dto.getStatus());
    }

    public static BalanceDto toBalanceDto(final Balance _entity) {
        return BalanceDto.builder()
                        .withOID(_entity.getOid())
                        .withId(_entity.getId())
                        .withNumber(_entity.getNumber())
                        .withKey(_entity.getKey())
                        .withUserOid(_entity.getUserOid())
                        .withStartAt(_entity.getStartAt())
                        .withEndAt(_entity.getEndAt())
                        .withStatus(_entity.getStatus())
                        .build();
    }

    public static PosBalanceDto toDto(final Balance _entity) {
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

    public static ProductRelationDto toDto(final ProductRelation _entity) {
        return ProductRelationDto.builder()
                        .withLabel(_entity.getLabel())
                        .withProductOid(_entity.getProductOid())
                        .build();
    }

    public static ProductRelation toEntity(final ProductRelationDto _dto) {
        return new ProductRelation()
                        .setLabel(_dto.getLabel())
                        .setProductOid(_dto.getProductOid());
    }

    public static ContactDto getContactDto(final String _key)
    {
        return Converter.toDto(INSTANCE.contactService.findContact(_key));
    }

    public static CollectOrderDto toDto(final CollectOrder _entity) {
        return _entity == null
            ? null
            : CollectOrderDto.builder()
                        .withId(_entity.getId())
                        .withAmount(_entity.getAmount())
                        .withState(_entity.getState())
                        .withCollected(_entity.getCollected())
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
                        .withDate(_projection.getDate())
                        .withStatus(_projection.getStatus())
                        .withOrder(order)
                        .build();
    }

    public static DocumentHeadDto toDto(final DocumentHead _projection)
    {
        final Builder<?,?> builder = new DocumentHeadDto.Builder<>();
        return builder.withId(_projection.getId())
                        .withNumber(_projection.getNumber())
                        .withCrossTotal(_projection.getCrossTotal())
                        .withNetTotal(_projection.getNetTotal())
                        .withCurrency(_projection.getCurrency())
                        .withDate(_projection.getDate())
                        .withStatus(_projection.getStatus())
                        .build();
    }
}
