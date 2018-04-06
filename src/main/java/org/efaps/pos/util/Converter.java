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

import org.efaps.pos.dto.PosUserDto;
import org.efaps.pos.dto.ProductDto;
import org.efaps.pos.entity.Product;
import org.efaps.pos.entity.User;

public final class Converter
{

    private Converter()
    {
    }

    public static Product toEntity(final ProductDto _dto)
    {
        final Product ret = new Product()
                        .setOid(_dto.getOid())
                        .setSKU(_dto.getSKU())
                        .setImageOid(_dto.getImageOid())
                        .setDescription(_dto.getDescription());
        return ret;
    }

    public static ProductDto toDto(final Product _entity)
    {
        return ProductDto.builder()
                        .withSKU(_entity.getSKU())
                        .withDescription(_entity.getDescription())
                        .withImageOid(_entity.getImageOid())
                        .withOID(_entity.getOid())
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
}
