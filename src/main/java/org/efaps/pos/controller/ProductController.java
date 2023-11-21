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
package org.efaps.pos.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.efaps.pos.config.IApi;
import org.efaps.pos.dto.Product2CategoryDto;
import org.efaps.pos.dto.ProductDto;
import org.efaps.pos.dto.ProductType;
import org.efaps.pos.service.ProductService;
import org.efaps.pos.util.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * The Class ProductController.
 */
@RestController
@RequestMapping(IApi.BASEPATH + "products")
public class ProductController
{

    private final ProductService service;

    public ProductController(final ProductService _service)
    {
        service = _service;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<ProductDto> getProducts(final Pageable pageable)
    {
        return service.getProducts(pageable)
                        .map(Converter::toDto);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "{productOid}")
    public ProductDto getProduct(@PathVariable(name = "productOid") final String _productOid)
    {
        return Converter.toDto(service.getProduct(_productOid));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, params = { "term" })
    public List<ProductDto> findProducts(@RequestParam(name = "term") final String _term,
                                         @RequestParam(name = "textsearch", required = false) final boolean textSearch)
    {
        return service.findProducts(_term, textSearch).stream()
                        .map(Converter::toDto)
                        .collect(Collectors.toList());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, params = { "category" })
    public List<ProductDto> getProductsByCategory(@RequestParam(name = "category") final String categoryOid)
    {
        return service.findProductsByCategory(categoryOid).stream()
                        .map(Converter::toDto)
                        .sorted((productDto0,
                                 productDto1) -> {
                            final var sort0 = productDto0.getCategories()
                                            .stream()
                                            .filter(catDto -> catDto.getCategoryOid().equals(categoryOid))
                                            .map(Product2CategoryDto::getWeight)
                                            .findFirst().orElse(0);
                            final var sort1 = productDto1.getCategories()
                                            .stream()
                                            .filter(catDto -> catDto.getCategoryOid().equals(categoryOid))
                                            .map(Product2CategoryDto::getWeight)
                                            .findFirst().orElse(0);
                            return sort0.compareTo(sort1);
                        })
                        .collect(Collectors.toList());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, params = { "barcode" })
    public List<ProductDto> getProductsByBarcode(@RequestParam(name = "barcode") final String barcode)
    {
        return service.findProductsByBarcode(barcode).stream()
                        .map(Converter::toDto)
                        .collect(Collectors.toList());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, params = { "type" })
    public List<ProductDto> getProductsByType(@RequestParam(name = "type") final ProductType type)
    {
        return service.findProductsByType(type).stream()
                        .map(Converter::toDto)
                        .collect(Collectors.toList());
    }

}
