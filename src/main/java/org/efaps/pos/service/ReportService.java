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
package org.efaps.pos.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.efaps.pos.dto.SalesReportDetailDto;
import org.efaps.pos.dto.SalesReportDto;
import org.efaps.pos.dto.SalesReportEntryDto;
import org.efaps.pos.dto.SalesReportInfoDto;
import org.efaps.pos.entity.AbstractPayableDocument;
import org.efaps.pos.repository.CreditNoteRepository;
import org.efaps.pos.repository.InvoiceRepository;
import org.efaps.pos.repository.ReceiptRepository;
import org.efaps.pos.repository.TicketRepository;
import org.efaps.pos.util.Converter;
import org.efaps.pos.util.PaymentGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ReportService
{

    private static final Logger LOG = LoggerFactory.getLogger(ReportService.class);

    private final CreditNoteRepository creditNoteRepository;
    private final InvoiceRepository invoiceRepository;
    private final ReceiptRepository receiptRepository;
    private final TicketRepository ticketRepository;

    public ReportService(final CreditNoteRepository creditNoteRepository,
                         final InvoiceRepository invoiceRepository,
                         final ReceiptRepository receiptRepository,
                         final TicketRepository ticketRepository)
    {
        this.creditNoteRepository = creditNoteRepository;
        this.invoiceRepository = invoiceRepository;
        this.receiptRepository = receiptRepository;
        this.ticketRepository = ticketRepository;
    }

    public Object getSalesReport(final LocalDate date)
    {
        final List<AbstractPayableDocument<?>> payables = new ArrayList<>();
        payables.addAll(creditNoteRepository.findByDate(date));
        payables.addAll(invoiceRepository.findByDate(date));
        payables.addAll(receiptRepository.findByDate(date));
        payables.addAll(ticketRepository.findByDate(date));

        final MultiValuedMap<PaymentGroup, SalesReportEntryDto> values = new ArrayListValuedHashMap<>();
        payables.stream().forEach(payable -> {
            LOG.info("payable: {}", payable);
            if (payable.getPayments() != null) {
                payable.getPayments().forEach(payment -> {
                    LOG.info("payment: {}", payment);
                    final var paymentDto = Converter.toPosDto(payment);
                    final var paymentGroup = PaymentGroup.builder()
                                    .withType(payment.getType())
                                    .withLabel(payment.getLabel())
                                    .build();
                    final var payableDto = Converter.toDto(payable);
                    values.put(paymentGroup, SalesReportEntryDto.builder()
                                    .withPayment(paymentDto)
                                    .withPayable(payableDto)
                                    .build());
                });
            }
        });
        final List<SalesReportInfoDto> infos = new ArrayList<>();
        final List<SalesReportDetailDto> details = new ArrayList<>();
        var total = BigDecimal.ZERO;
        for (final var entry : values.asMap().entrySet()) {
            details.add(SalesReportDetailDto.builder()
                            .withType(entry.getKey().getType())
                            .withLabel(entry.getKey().getLabel())
                            .withEntries(entry.getValue())
                            .build());

            final var amount = entry.getValue().stream()
                            .map(dto -> dto.getPayment().getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
            total = total.add(amount);
            infos.add(SalesReportInfoDto.builder()
                            .withType(entry.getKey().getType())
                            .withLabel(entry.getKey().getLabel())
                            .withCount(entry.getValue().size())
                            .withAmount(amount)
                            .build());
        }

        return SalesReportDto.builder()
                        .withInfos(infos)
                        .withDetails(details)
                        .withTotal(total)
                        .build();
    }

}
