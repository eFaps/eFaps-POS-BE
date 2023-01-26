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
import org.efaps.pos.dto.ProductDto;
import org.efaps.pos.dto.ProductType;
import org.efaps.pos.service.ProductService;
import org.efaps.pos.util.Converter;
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
    public List<ProductDto> getProducts()
    {
        return service.getProducts().stream()
                        .map(product -> Converter.toDto(product))
                        .collect(Collectors.toList());
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
                        .map(product -> Converter.toDto(product))
                        .collect(Collectors.toList());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, params = { "category" })
    public List<ProductDto> getProductsByCategory(@RequestParam(name = "category") final String _categoryOid)
    {
        return service.findProductsByCategory(_categoryOid).stream()
                        .map(product -> Converter.toDto(product))
                        .collect(Collectors.toList());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, params = { "barcode" })
    public List<ProductDto> getProductsByBarcode(@RequestParam(name = "barcode") final String barcode)
    {
        return service.findProductsByBarcode(barcode).stream()
                        .map(product -> Converter.toDto(product))
                        .collect(Collectors.toList());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, params = { "type" })
    public List<ProductDto> getProductsByType(@RequestParam(name = "type") final ProductType type)
    {
        return service.findProductsByType(type).stream()
                        .map(product -> Converter.toDto(product))
                        .collect(Collectors.toList());
    }

}
