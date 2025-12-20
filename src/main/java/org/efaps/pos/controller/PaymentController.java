/*
 * Copyright © 2003 - 2024 The eFaps Team (-)
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
 */
package org.efaps.pos.controller;

import java.util.List;

import org.efaps.pos.config.IApi;
import org.efaps.pos.dto.AbstractPayableDocumentDto;
import org.efaps.pos.dto.IPosPaymentDto;
import org.efaps.pos.entity.User;
import org.efaps.pos.error.PreconditionException;
import org.efaps.pos.listener.ConversionEvent;
import org.efaps.pos.service.BalanceService;
import org.efaps.pos.service.ConversionService;
import org.efaps.pos.service.DocumentService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(IApi.BASEPATH + "payments")
public class PaymentController
{

    private final ConversionService conversionService;
    private final DocumentService documentService;
    private final BalanceService balanceService;

    public PaymentController(final ConversionService conversionService, final DocumentService documentService,
                             final BalanceService balanceService)
    {
        this.conversionService = conversionService;
        this.documentService = documentService;
        this.balanceService = balanceService;
    }

    @PostMapping
    public AbstractPayableDocumentDto registerPayment(final Authentication authentication,
                                                      @RequestBody final List<IPosPaymentDto> paymentDtos,
                                                      @RequestParam(name = "orderId") final String orderId)
        throws PreconditionException
    {
        final var balanceId = balanceService.getCurrent((User) authentication.getPrincipal(), true).get().getId();
        final var payable = documentService.payAndEmit(balanceId, orderId, paymentDtos);
        return conversionService.toDto(ConversionEvent.PAYANDEMIT, payable);
    }

}
