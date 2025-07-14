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

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.efaps.pos.client.Checkout;
import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.dto.Currency;
import org.efaps.pos.dto.MoneyAmountDto;
import org.efaps.pos.dto.SalesReportDetailDto;
import org.efaps.pos.dto.SalesReportDto;
import org.efaps.pos.dto.SalesReportEntryDto;
import org.efaps.pos.dto.SalesReportInfoDto;
import org.efaps.pos.dto.StoreStatus;
import org.efaps.pos.dto.StoreStatusRequestDto;
import org.efaps.pos.entity.AbstractPayableDocument;
import org.efaps.pos.entity.Workspace;
import org.efaps.pos.entity.Workspace.PrintCmd;
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

    private final EFapsClient eFapsClient;
    private final CreditNoteRepository creditNoteRepository;
    private final InvoiceRepository invoiceRepository;
    private final ReceiptRepository receiptRepository;
    private final TicketRepository ticketRepository;
    private final GridFsService gridFsService;
    private final WorkspaceService workspaceService;

    public ReportService(final EFapsClient eFapsClient,
                         final CreditNoteRepository creditNoteRepository,
                         final InvoiceRepository invoiceRepository,
                         final ReceiptRepository receiptRepository,
                         final TicketRepository ticketRepository,
                         final GridFsService gridFsService,
                         final WorkspaceService workspaceService)
    {
        this.eFapsClient = eFapsClient;
        this.creditNoteRepository = creditNoteRepository;
        this.invoiceRepository = invoiceRepository;
        this.receiptRepository = receiptRepository;
        this.ticketRepository = ticketRepository;
        this.gridFsService = gridFsService;
        this.workspaceService = workspaceService;
    }

    public SalesReportDto getSalesReport(final LocalDate date)
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
                                    .withCurrency(payment.getCurrency())
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
        final Map<Currency, BigDecimal> totalsMap = new HashMap<>();

        for (final var entry : values.asMap().entrySet()) {
            details.add(SalesReportDetailDto.builder()
                            .withType(entry.getKey().getType())
                            .withLabel(entry.getKey().getLabel())
                            .withCurrency(entry.getKey().getCurrency())
                            .withEntries(entry.getValue())
                            .build());

            final var amount = entry.getValue().stream()
                            .map(dto -> dto.getPayment().getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
            if (!totalsMap.containsKey(entry.getKey().getCurrency())) {
                totalsMap.put(entry.getKey().getCurrency(), BigDecimal.ZERO);
            }
            totalsMap.put(entry.getKey().getCurrency(), totalsMap.get(entry.getKey().getCurrency()).add(amount));
            infos.add(SalesReportInfoDto.builder()
                            .withType(entry.getKey().getType())
                            .withLabel(entry.getKey().getLabel())
                            .withCurrency(entry.getKey().getCurrency())
                            .withCount(entry.getValue().size())
                            .withAmount(amount)
                            .build());
        }

        final var totals = totalsMap.entrySet().stream()
                        .map(entry -> MoneyAmountDto.builder()
                                        .withAmount(entry.getValue())
                                        .withCurrency(entry.getKey())
                                        .build())
                        .toList();

        return SalesReportDto.builder()
                        .withInfos(infos)
                        .withDetails(details)
                        .withTotals(totals)
                        .build();
    }

    public void sync()
    {
        LOG.info("Syncing Reports");
        final var workspaces = workspaceService.getWorkspaces();
        final var reportOids = workspaces.stream()
                        .map(Workspace::getPrintCmds)
                        .flatMap(Set::stream)
                        .map(PrintCmd::getReportOid)
                        .collect(Collectors.toSet());

        final var response = eFapsClient.evalStoreStatus(StoreStatusRequestDto.builder()
                        .withOids(new ArrayList<>(reportOids))
                        .build());

        for (final var status : response.getStatus()) {
            if (status.isExisting()) {
                final var file = gridFsService.getGridFSFile(status.getOid());
                // no modification date --> sync always
                if (status.getModifiedAt() == null || file == null) {
                    retrieveReport(status);
                } else if (file.getMetadata().containsKey("modifiedAt")) {
                    final OffsetDateTime local = ((Date) file.getMetadata().get("modifiedAt")).toInstant()
                                    .atOffset(ZoneOffset.UTC);
                    if (!local.withNano(0).equals(status.getModifiedAt().withNano(0))) {
                        retrieveReport(status);
                    }
                } else {
                    retrieveReport(status);
                }
            }
        }
    }

    protected void retrieveReport(final StoreStatus status)
    {
        final Checkout checkout = eFapsClient.checkout(status.getOid());
        if (checkout != null) {
            gridFsService.updateContent(status.getOid(),
                            new ByteArrayInputStream(checkout.getContent()),
                            checkout.getFilename(),
                            checkout.getContentType().toString(),
                            status.getModifiedAt());
        }
    }

}
