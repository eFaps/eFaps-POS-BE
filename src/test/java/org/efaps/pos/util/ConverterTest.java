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

import org.efaps.pos.dto.PosUserDto;
import org.efaps.pos.dto.ProductDto;
import org.efaps.pos.dto.WorkspaceDto;
import org.efaps.pos.entity.Product;
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
                        .build();

        final Product product = Converter.toEntity(dto);
        assertEquals(dto.getOid(), product.getOid());
        assertEquals(dto.getSKU(), product.getSKU());
        assertEquals(dto.getDescription(), product.getDescription());
        assertEquals(dto.getImageOid(), product.getImageOid());
    }

    @Test
    public void testProductToDto() {
        final Product entity = new Product()
                        .setSKU("100612.001")
                        .setDescription("This is the product Description")
                        .setImageOid("1234.1")
                        .setOid("165165.14651");

        final ProductDto dto = Converter.toDto(entity);
        assertEquals(entity.getOid(), dto.getOid());
        assertEquals(entity.getSKU(), dto.getSKU());
        assertEquals(entity.getDescription(), dto.getDescription());
        assertEquals(entity.getImageOid(), dto.getImageOid());
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
                        .setName("Caja 1")
                        .setOid("165165.14651");

        final WorkspaceDto dto = Converter.toDto(entity);
        assertEquals(entity.getOid(), dto.getOid());
        assertEquals(entity.getName(), dto.getName());
    }
}
