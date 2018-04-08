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

import java.util.stream.Collectors;

import org.efaps.pos.dto.CategoryDto;
import org.efaps.pos.dto.DocItemDto;
import org.efaps.pos.dto.OrderDto;
import org.efaps.pos.dto.PosDto;
import org.efaps.pos.dto.PosUserDto;
import org.efaps.pos.dto.ProductDto;
import org.efaps.pos.dto.ReceiptDto;
import org.efaps.pos.dto.WorkspaceDto;
import org.efaps.pos.entity.AbstractDocument;
import org.efaps.pos.entity.Category;
import org.efaps.pos.entity.Order;
import org.efaps.pos.entity.Pos;
import org.efaps.pos.entity.Product;
import org.efaps.pos.entity.Receipt;
import org.efaps.pos.entity.User;
import org.efaps.pos.entity.Workspace;

public final class Converter
{

    private Converter()
    {
    }

    public static Receipt toEntity(final ReceiptDto _dto)
    {
        final Receipt ret = new Receipt()
                        .setOid(_dto.getOid())
                        .setNumber(_dto.getNumber());
        return ret;
    }

    public static OrderDto toDto(final Order _entity)
    {
        return OrderDto.builder()
                        .withOID(_entity.getOid())
                        .withNumber(_entity.getNumber())
                        .withItems(_entity.getItems().stream()
                                        .map(_item -> Converter.toDto(_item))
                                        .collect(Collectors.toSet()))
                        .build();
    }

    public static Order toEntity(final OrderDto _dto)
    {
        final Order ret = new Order()
                        .setOid(_dto.getOid())
                        .setNumber(_dto.getNumber())
                        .setItems(_dto.getItems().stream()
                                        .map(_item -> Converter.toEntity((DocItemDto) _item))
                                        .collect(Collectors.toSet()));
        return ret;
    }

    public static AbstractDocument.Item toEntity(final DocItemDto _dto) {
        return new AbstractDocument.Item()
                        .setOid(_dto.getOid())
                        .setIndex(_dto.getIndex());
    }

    public static DocItemDto toDto(final AbstractDocument.Item _entity) {
        return DocItemDto.builder()
                        .withOID(_entity.getOid())
                        .withIndex(_entity.getIndex())
                        .build();
    }

    public static ReceiptDto toDto(final Receipt _entity)
    {
        return ReceiptDto.builder()
                        .withOID(_entity.getOid())
                        .withNumber(_entity.getNumber())
                        .build();
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
                        .setCategoryOids(_dto.getCategoryOids());
        return ret;
    }

    public static ProductDto toDto(final Product _entity)
    {
        return ProductDto.builder()
                        .withSKU(_entity.getSKU())
                        .withDescription(_entity.getDescription())
                        .withImageOid(_entity.getImageOid())
                        .withOID(_entity.getOid())
                        .withNetPrice(_entity.getNetPrice())
                        .withCrossPrice(_entity.getCrossPrice())
                        .withCategoryOids(_entity.getCategoryOids())
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

    public static WorkspaceDto toDto(final Workspace _entity)
    {
        return WorkspaceDto.builder()
                        .withOID(_entity.getOid())
                        .withName(_entity.getName())
                        .withPosOid(_entity.getPosOid())
                        .build();
    }

    public static PosDto toDto(final Pos _entity)
    {
        return PosDto.builder()
                        .withOID(_entity.getOid())
                        .withName(_entity.getName())
                        .build();
    }

    public static CategoryDto toDto(final Category _entity)
    {
        return CategoryDto.builder()
                        .withOID(_entity.getOid())
                        .withName(_entity.getName())
                        .build();
    }
}
