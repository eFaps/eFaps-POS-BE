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

import org.efaps.pos.dto.ProductDto;
import org.efaps.pos.entity.Product;
import org.junit.jupiter.api.Test;

public class ConverterTest
{
    @Test
    public void testProduct() {
        final ProductDto dto = ProductDto.builder()
                        .withDescription("This is the product Description")
                        .withOID("123.4561")
                        .build();

        final Product product = Converter.fromDto(dto);
        assertEquals(dto.getOid(), product.getOid());
        assertEquals(dto.getDescription(), product.getDescription());
    }
}
