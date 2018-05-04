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
package org.efaps.pos.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.efaps.pos.dto.PosOrderDto;
import org.efaps.pos.dto.PosReceiptDto;
import org.efaps.pos.service.DocumentService;
import org.efaps.pos.util.Converter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DocumentController
{
    private final DocumentService documentService;

    public DocumentController(final DocumentService _service) {
        this.documentService = _service;
    }

    @PostMapping(path = "workspaces/{oid}/documents/receipts", produces = MediaType.APPLICATION_JSON_VALUE)
    public PosReceiptDto createReceipt(@PathVariable("oid") final String _oid, @RequestBody final PosReceiptDto _receiptDto) {
        return Converter.toDto(this.documentService.createReceipt(_oid, Converter.toEntity(_receiptDto)));
    }

    @PostMapping(path = "documents/orders", produces = MediaType.APPLICATION_JSON_VALUE)
    public PosOrderDto createOrder(@RequestBody final PosOrderDto _orderDto) {
        return Converter.toDto(this.documentService.createOrder(Converter.toEntity(_orderDto)));
    }

    @PutMapping(path = "documents/orders/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PosOrderDto updateOrder(@PathVariable(name= "orderId") final String _orderId,
                                   @RequestBody final PosOrderDto _orderDto) {
        return Converter.toDto(this.documentService.updateOrder(Converter.toEntity(_orderDto).setId(_orderId)));
    }

    @GetMapping(path = "documents/orders", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PosOrderDto> getOrder() {
        return this.documentService.getOrders().stream()
                        .map(_order -> Converter.toDto(_order))
                        .collect(Collectors.toList());
    }
}