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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Collections;

import org.efaps.pos.dto.CategoryDto;
import org.efaps.pos.dto.OrderDto;
import org.efaps.pos.dto.PosDto;
import org.efaps.pos.dto.PosUserDto;
import org.efaps.pos.dto.ProductDto;
import org.efaps.pos.dto.ReceiptDto;
import org.efaps.pos.dto.WorkspaceDto;
import org.efaps.pos.entity.Category;
import org.efaps.pos.entity.Order;
import org.efaps.pos.entity.Pos;
import org.efaps.pos.entity.Product;
import org.efaps.pos.entity.Receipt;
import org.efaps.pos.entity.User;
import org.efaps.pos.entity.Workspace;
import org.junit.jupiter.api.Test;

public class ConverterTest
{
    @Test
    public void testProductToEntity() {
        final ProductDto dto = ProductDto.builder()
                        .withOID("Asda")
                        .withSKU("100612.001")
                        .withDescription("This is the product Description")
                        .withImageOid("1234.1")
                        .withNetPrice(new BigDecimal("12.50"))
                        .withCrossPrice(new BigDecimal("14.40"))
                        .withCategoryOids(Collections.singleton("555.1651"))
                        .build();

        final Product product = Converter.toEntity(dto);
        assertEquals(dto.getOid(), product.getOid());
        assertEquals(dto.getSku(), product.getSKU());
        assertEquals(dto.getDescription(), product.getDescription());
        assertEquals(dto.getImageOid(), product.getImageOid());
        assertEquals(dto.getNetPrice(), product.getNetPrice());
        assertEquals(dto.getCrossPrice(), product.getCrossPrice());
        assertEquals(dto.getCategoryOids(), product.getCategoryOids());
    }

    @Test
    public void testProductToDto() {
        final Product entity = new Product()
                        .setSKU("100612.001")
                        .setDescription("This is the product Description")
                        .setImageOid("1234.1")
                        .setNetPrice(new BigDecimal("12.50"))
                        .setCrossPrice(new BigDecimal("14.40"))
                        .setOid("165165.14651")
                        .setCategoryOids(Collections.singleton("5515.1651"));

        final ProductDto dto = Converter.toDto(entity);
        assertEquals(entity.getOid(), dto.getOid());
        assertEquals(entity.getSKU(), dto.getSku());
        assertEquals(entity.getDescription(), dto.getDescription());
        assertEquals(entity.getImageOid(), dto.getImageOid());
        assertEquals(entity.getNetPrice(), dto.getNetPrice());
        assertEquals(entity.getCrossPrice(), dto.getCrossPrice());
        assertEquals(entity.getCategoryOids(), dto.getCategoryOids());
    }

    @Test
    public void testUserToDto() {
        final User entity = new User()
                        .setUsername("a username")
                        .setFirstName("Juan")
                        .setSurName("Perez")
                        .setOid("165165.14651");

        final PosUserDto dto = Converter.toDto(entity);
        assertEquals(entity.getUsername(), dto.getUsername());
        assertEquals(entity.getFirstName(), dto.getFirstName());
        assertEquals(entity.getSurName(), dto.getSurName());
    }

    @Test
    public void testWorkspaceToDto() {
        final Workspace entity = new Workspace()
                        .setOid("165165.14651")
                        .setName("Caja 1")
                        .setPosOid("9999.514651");

        final WorkspaceDto dto = Converter.toDto(entity);
        assertEquals(entity.getOid(), dto.getOid());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getPosOid(), dto.getPosOid());
    }

    @Test
    public void testPosToDto() {
        final Pos entity = new Pos()
                        .setName("Caja 1")
                        .setOid("165165.14651");

        final PosDto dto = Converter.toDto(entity);
        assertEquals(entity.getOid(), dto.getOid());
        assertEquals(entity.getName(), dto.getName());
    }

    @Test
    public void testCategoryToDto() {
        final Category entity = new Category()
                        .setName("Caja 1")
                        .setOid("165165.14651");

        final CategoryDto dto = Converter.toDto(entity);
        assertEquals(entity.getOid(), dto.getOid());
        assertEquals(entity.getName(), dto.getName());
    }

    @Test
    public void testReceiptToEntity() {
        final ReceiptDto dto = ReceiptDto.builder()
                        .withOID("16515.5165")
                        .withNumber("B001-1651651")
                        .build();

        final Receipt entity = Converter.toEntity(dto);
        assertEquals(dto.getOid(), entity.getOid());
        assertEquals(dto.getNumber(), entity.getNumber());
    }

    @Test
    public void testReceiptToDto() {
        final Receipt entity = new Receipt()
                        .setOid("165165.14651")
                        .setNumber("B001-165165");

        final ReceiptDto dto = Converter.toDto(entity);
        assertEquals(entity.getOid(), dto.getOid());
        assertEquals(entity.getNumber(), dto.getNumber());
    }

    @Test
    public void testOrderToEntity() {
        final OrderDto dto = OrderDto.builder()
                        .withOID("16515.5165")
                        .withNumber("B001-1651651")
                        .build();

        final Order entity = Converter.toEntity(dto);
        assertEquals(dto.getOid(), entity.getOid());
        assertEquals(dto.getNumber(), entity.getNumber());
    }

    @Test
    public void testOrderToDto() {
        final Order entity = new Order()
                        .setOid("165165.14651")
                        .setNumber("B001-165165");

        final OrderDto dto = Converter.toDto(entity);
        assertEquals(entity.getOid(), dto.getOid());
        assertEquals(entity.getNumber(), dto.getNumber());
    }
}
