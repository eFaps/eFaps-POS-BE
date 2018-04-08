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

import org.efaps.pos.dto.OrderDto;
import org.efaps.pos.dto.ReceiptDto;
import org.efaps.pos.service.DocumentService;
import org.efaps.pos.util.Converter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("documents")
public class DocumentController
{
    private final DocumentService service;

    public DocumentController(final DocumentService _service) {
        this.service = _service;
    }

    @PostMapping(path = "receipts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ReceiptDto createReceipt(@RequestBody final ReceiptDto _receiptDto) {
        return Converter.toDto(this.service.createReceipt(Converter.toEntity(_receiptDto)));
    }

    @PostMapping(path = "orders", produces = MediaType.APPLICATION_JSON_VALUE)
    public OrderDto createReceipt(@RequestBody final OrderDto _orderDto) {
        return Converter.toDto(this.service.createOrder(Converter.toEntity(_orderDto)));
    }
}
