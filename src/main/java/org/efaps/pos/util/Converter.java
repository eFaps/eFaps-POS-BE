/*
 * Copyright 2003 - 2018 The eFaps Team
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

import org.efaps.pos.dto.CategoryDto;
import org.efaps.pos.dto.CompanyDto;
import org.efaps.pos.dto.ContactDto;
import org.efaps.pos.dto.DocItemDto;
import org.efaps.pos.dto.InvoiceDto;
import org.efaps.pos.dto.PosDocItemDto;
import org.efaps.pos.dto.PosDto;
import org.efaps.pos.dto.PosInvoiceDto;
import org.efaps.pos.dto.PosOrderDto;
import org.efaps.pos.dto.PosReceiptDto;
import org.efaps.pos.dto.PosTicketDto;
import org.efaps.pos.dto.PosUserDto;
import org.efaps.pos.dto.ProductDto;
import org.efaps.pos.dto.ReceiptDto;
import org.efaps.pos.dto.SequenceDto;
import org.efaps.pos.dto.TaxDto;
import org.efaps.pos.dto.TaxEntryDto;
import org.efaps.pos.dto.TicketDto;
import org.efaps.pos.dto.UserDto;
import org.efaps.pos.dto.WorkspaceDto;
import org.efaps.pos.entity.AbstractDocument;
import org.efaps.pos.entity.AbstractDocument.TaxEntry;
import org.efaps.pos.entity.Category;
import org.efaps.pos.entity.Contact;
import org.efaps.pos.entity.Invoice;
import org.efaps.pos.entity.Order;
import org.efaps.pos.entity.Pos;
import org.efaps.pos.entity.Pos.Company;
import org.efaps.pos.entity.Product;
import org.efaps.pos.entity.Receipt;
import org.efaps.pos.entity.Sequence;
import org.efaps.pos.entity.Tax;
import org.efaps.pos.entity.Ticket;
import org.efaps.pos.entity.User;
import org.efaps.pos.entity.Workspace;
import org.efaps.pos.service.ProductService;
import org.springframework.stereotype.Component;

@Component
public final class Converter
{
    private static Converter INSTANCE;

    private final ProductService productService;

    public Converter(final ProductService _productService)
    {
        INSTANCE = this;
        this.productService = _productService;
    }

    public static Receipt toEntity(final PosReceiptDto _dto)
    {
        final Receipt ret = new Receipt()
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
                        .setTaxes(_dto.getTaxes() ==  null
                            ? null
                            : _dto.getTaxes().stream()
                                .map(_tax -> Converter.toEntity(_tax))
                                .collect(Collectors.toSet()));
        return ret;
    }

    public static Invoice toEntity(final PosInvoiceDto _dto)
    {
        final Invoice ret = new Invoice()
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
                        .setTaxes(_dto.getTaxes() ==  null
                            ? null
                            : _dto.getTaxes().stream()
                                .map(_tax -> Converter.toEntity(_tax))
                                .collect(Collectors.toSet()));
        return ret;
    }

    public static Ticket toEntity(final PosTicketDto _dto)
    {
        final Ticket ret = new Ticket()
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
                        .setTaxes(_dto.getTaxes() ==  null
                            ? null
                            : _dto.getTaxes().stream()
                                .map(_tax -> Converter.toEntity(_tax))
                                .collect(Collectors.toSet()));
        return ret;
    }


    public static Order toEntity(final PosOrderDto _dto)
    {
        final Order ret = new Order()
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
                                .collect(Collectors.toSet()));
        return ret;
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
                        .setImageOid(_dto.getImageOid())
                        .setDescription(_dto.getDescription())
                        .setNetPrice(_dto.getNetPrice())
                        .setCrossPrice(_dto.getCrossPrice())
                        .setCategoryOids(_dto.getCategoryOids())
                        .setTaxes(_dto.getTaxes().stream()
                                        .map(_tax -> _tax == null ? null : toEntity(_tax))
                                        .collect(Collectors.toSet()))
                        .setUoM(_dto.getUoM());
        return ret;
    }

    public static ProductDto toDto(final Product _entity)
    {
        return _entity == null
                    ? null
                    : ProductDto.builder()
                        .withSKU(_entity.getSKU())
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
                        .build();
    }

    public static PosUserDto toDto(final User _user)
    {
        return PosUserDto.builder()
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
                        .setRoles(_dto.getRoles());
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
                        .build();
    }

    public static Workspace toEntity(final WorkspaceDto _dto)
    {
        return new Workspace()
                        .setOid(_dto.getOid())
                        .setName(_dto.getName())
                        .setPosOid(_dto.getPosOid())
                        .setDocTypes(_dto.getDocTypes());
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
                        .withCompany(_entity.getCompany() == null
                            ? null
                            : CompanyDto.builder()
                                        .withName(_entity.getCompany().getName())
                                        .withTaxNumber(_entity.getCompany().getTaxNumber())
                                        .build())
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
                        .setTicketSeqOid(_dto.getTicketSeqOid())
                        .setCompany(new Company()
                                    .setName(_dto.getCompany() == null ? null : _dto.getCompany().getName())
                                    .setTaxNumber(_dto.getCompany() == null ? null : _dto.getCompany().getTaxNumber()));
        return ret;
    }

    public static CategoryDto toDto(final Category _entity)
    {
        return CategoryDto.builder()
                        .withOID(_entity.getOid())
                        .withName(_entity.getName())
                        .build();
    }

    public static Category toEntity(final CategoryDto _dto)
    {
        return new Category().setName(_dto.getName()).setOid(_dto.getOid());
    }

    public static Contact toEntity(final ContactDto _dto)
    {
        return new Contact().setOid(_dto.getOid())
                        .setName(_dto.getName())
                        .setTaxNumber(_dto.getTaxNumber());
    }

    public static TaxDto toDto(final Tax _entity)
    {
        return TaxDto.builder()
                        .withOID(_entity.getOid())
                        .withName(_entity.getName())
                        .withPercent(_entity.getPercent())
                        .build();
    }

    public static Tax toEntity(final TaxDto _dto)
    {
        final Tax ret = new Tax()
                        .setOid(_dto.getOid())
                        .setName(_dto.getName())
                        .setPercent(_dto.getPercent());
        return ret;
    }

    public static TaxEntry toEntity(final TaxEntryDto _dto)
    {
        final TaxEntry ret = new TaxEntry()
                        .setTax(_dto.getTax() == null ? null : Converter.toEntity(_dto.getTax()))
                        .setAmount(_dto.getAmount());
        return ret;
    }

    public static TaxEntryDto toDto(final TaxEntry _entity)
    {
        return TaxEntryDto.builder()
                        .withTax(Converter.toDto(_entity.getTax()))
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
                        .build();
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
                        .build();
    }

    public static ContactDto toDto(final Contact _entity) {
        return _entity == null
                    ? null
                    : ContactDto.builder()
                        .withOID(_entity.getOid())
                        .withName(_entity.getName())
                        .withTaxNumber(_entity.getTaxNumber())
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
                        .withTaxes(_entity.getTaxes() == null
                            ? null
                            : _entity.getTaxes().stream()
                                .map(_tax -> Converter.toDto(_tax))
                                .collect(Collectors.toSet()))
                        .build();
    }
}
