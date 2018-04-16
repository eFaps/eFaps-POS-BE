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
import org.efaps.pos.dto.DocStatus;
import org.efaps.pos.dto.PosDocItemDto;
import org.efaps.pos.dto.PosDto;
import org.efaps.pos.dto.PosOrderDto;
import org.efaps.pos.dto.PosReceiptDto;
import org.efaps.pos.dto.PosUserDto;
import org.efaps.pos.dto.ProductDto;
import org.efaps.pos.dto.TaxDto;
import org.efaps.pos.dto.WorkspaceDto;
import org.efaps.pos.entity.AbstractDocument.Item;
import org.efaps.pos.entity.Category;
import org.efaps.pos.entity.Order;
import org.efaps.pos.entity.Pos;
import org.efaps.pos.entity.Product;
import org.efaps.pos.entity.Receipt;
import org.efaps.pos.entity.Tax;
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
                        .withTaxes(Collections.singleton(TaxDto.builder().build()))
                        .build();

        final Product product = Converter.toEntity(dto);
        assertEquals(dto.getOid(), product.getOid());
        assertEquals(dto.getSku(), product.getSKU());
        assertEquals(dto.getDescription(), product.getDescription());
        assertEquals(dto.getImageOid(), product.getImageOid());
        assertEquals(dto.getNetPrice(), product.getNetPrice());
        assertEquals(dto.getCrossPrice(), product.getCrossPrice());
        assertEquals(dto.getCategoryOids(), product.getCategoryOids());
        assertEquals(dto.getTaxes().size(), product.getTaxes().size());
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
        final PosReceiptDto dto = PosReceiptDto.builder()
                        .withOID("16515.5165")
                        .withNumber("B001-1651651")
                        .withStatus(DocStatus.CLOSED)
                        .build();

        final Receipt entity = Converter.toEntity(dto);
        assertEquals(dto.getOid(), entity.getOid());
        assertEquals(dto.getNumber(), entity.getNumber());
        assertEquals(dto.getStatus(), entity.getStatus());
    }

    @Test
    public void testOrderToEntity() {
        final PosOrderDto dto = PosOrderDto.builder()
                        .withOID("16515.5165")
                        .withNumber("B001-1651651")
                        .withItems(Collections.singleton(PosDocItemDto.builder().build()))
                        .withStatus(DocStatus.CLOSED)
                        .build();

        final Order entity = Converter.toEntity(dto);
        assertEquals(dto.getOid(), entity.getOid());
        assertEquals(dto.getNumber(), entity.getNumber());
        assertEquals(1, entity.getItems().size());
        assertEquals(dto.getStatus(), entity.getStatus());
    }

    @Test
    public void testItemToEntity() {
        final PosDocItemDto dto = PosDocItemDto.builder()
                        .withOID("16515.5165")
                        .withIndex(2)
                        .withCrossPrice(new BigDecimal("1.11"))
                        .withCrossUnitPrice(new BigDecimal("1.12"))
                        .withNetPrice(new BigDecimal("1.13"))
                        .withNetUnitPrice(new BigDecimal("1.14"))
                        .withQuantity(new BigDecimal("1.15"))
                        .withProductOid("ProductOid")
                        .build();

        final Item entity = Converter.toEntity(dto);
        assertEquals(dto.getOid(), entity.getOid());
        assertEquals(dto.getIndex(), entity.getIndex());
        assertEquals(dto.getCrossPrice(), entity.getCrossPrice());
        assertEquals(dto.getCrossUnitPrice(), entity.getCrossUnitPrice());
        assertEquals(dto.getNetPrice(), entity.getNetPrice());
        assertEquals(dto.getNetUnitPrice(), entity.getNetUnitPrice());
        assertEquals(dto.getQuantity(), entity.getQuantity());
        assertEquals(dto.getProductOid(), entity.getProductOid());
    }

    @Test
    public void testItemToEntityWithProduct() {
        final PosDocItemDto dto = PosDocItemDto.builder()
                        .withProductOid("ProductOid")
                        .withProduct(ProductDto.builder().withOID("ThisOid").build())
                        .build();

        final Item entity = Converter.toEntity(dto);
        assertEquals("ThisOid", entity.getProductOid());
    }

    @Test
    public void testTaxToEntity() {
        final TaxDto dto = TaxDto.builder()
                        .withOID("653.25")
                        .withName("VAT")
                        .withPercent(new BigDecimal("18"))
                        .build();

        final Tax entity = Converter.toEntity(dto);
        assertEquals(dto.getOid(), entity.getOid());
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getPercent(), entity.getPercent());
    }

    @Test
    public void testTaxToDto() {
        final Tax entity = new Tax()
                        .setOid("123.44")
                        .setName("VAT")
                        .setPercent(new BigDecimal("18"));

        final TaxDto dto = Converter.toDto(entity);
        assertEquals(entity.getOid(), dto.getOid());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getPercent(), dto.getPercent());
    }
}
