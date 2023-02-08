/*
 * Copyright 2003 - 2022 The eFaps Team
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

package org.efaps.pos.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.efaps.pos.dto.BalanceStatus;
import org.efaps.pos.dto.BalanceSummaryDetailDto;
import org.efaps.pos.dto.BalanceSummaryDto;
import org.efaps.pos.dto.CashEntryDto;
import org.efaps.pos.dto.PaymentInfoDto;
import org.efaps.pos.dto.PosUserDto;
import org.efaps.pos.dto.TaxEntryDto;
import org.efaps.pos.entity.AbstractDocument.TaxEntry;
import org.efaps.pos.pojo.Payment;
import org.efaps.pos.entity.AbstractPayableDocument;
import org.efaps.pos.entity.Balance;
import org.efaps.pos.entity.CreditNote;
import org.efaps.pos.entity.Invoice;
import org.efaps.pos.entity.Receipt;
import org.efaps.pos.entity.Ticket;
import org.efaps.pos.entity.User;
import org.efaps.pos.repository.BalanceRepository;
import org.efaps.pos.repository.CashEntryRepository;
import org.efaps.pos.util.Converter;
import org.efaps.pos.util.PaymentGroup;
import org.springframework.stereotype.Service;

@Service
public class BalanceService
{

    private final BalanceRepository repository;
    private final CashEntryRepository cashEntryRepository;
    private final SequenceService sequenceService;
    private final DocumentService documentService;
    private final UserService userService;

    public BalanceService(final BalanceRepository _repository,
                          final CashEntryRepository _cashEntryRepository,
                          final SequenceService _sequenceService,
                          final DocumentService _documentService,
                          final UserService _userService)
    {
        repository = _repository;
        cashEntryRepository = _cashEntryRepository;
        sequenceService = _sequenceService;
        documentService = _documentService;
        userService = _userService;
    }

    public Optional<Balance> getCurrent(final User _principal, final boolean _createNew)
    {
        final Optional<Balance> ret;
        final Optional<Balance> balanceOpt = repository.findOneByUserOidAndStatus(_principal.getOid(),
                        BalanceStatus.OPEN);
        if (!balanceOpt.isPresent() && _createNew) {
            final String number = sequenceService.getNextNumber("Balance", false);
            final Balance balance = new Balance()
                            .setStartAt(LocalDateTime.now())
                            .setUserOid(_principal.getOid())
                            .setStatus(BalanceStatus.OPEN)
                            .setKey("TODO")
                            .setNumber(number);
            ret = Optional.of(repository.save(balance));
        } else {
            ret = balanceOpt;
        }
        return ret;
    }

    public Balance update(final Balance _retrievedBalance)
    {
        Balance ret = _retrievedBalance;
        final Optional<Balance> balanceOpt = repository.findById(_retrievedBalance.getId());
        if (balanceOpt.isPresent()) {
            final Balance balance = balanceOpt.get();
            balance.setStatus(BalanceStatus.CLOSED)
                            .setEndAt(LocalDateTime.now())
                            .setSynced(false);
            ret = repository.save(balance);
        }
        return ret;
    }

    public BalanceSummaryDto getSummary(final String _balanceId)
    {
        final Balance balance = repository.findById(_balanceId).orElseThrow();

        final PosUserDto user = Converter.toDto(userService.getUserByOid(balance.getUserOid()));

        final var cashEntries = cashEntryRepository.findByBalanceOidIn(balance.getId(), balance.getOid()).stream()
                        .map(entity -> {
                            return Converter.toDto(entity);
                        }).collect(Collectors.toList());

        final Collection<Invoice> invoices = documentService.getInvoices4Balance(balance.getOid() == null
                        ? balance.getId()
                        : balance.getOid());
        final Collection<Receipt> receipts = documentService.getReceipts4Balance(balance.getOid() == null
                        ? balance.getId()
                        : balance.getOid());
        final Collection<Ticket> tickets = documentService.getTickets4Balance(balance.getOid() == null
                        ? balance.getId()
                        : balance.getOid());
        final Collection<CreditNote> creditNotes = documentService.getCreditNotes4Balance(balance.getOid() == null
                        ? balance.getId()
                        : balance.getOid());

        final List<AbstractPayableDocument<?>> all = new ArrayList<>();
        all.addAll(invoices);
        all.addAll(receipts);
        all.addAll(tickets);

        final BalanceSummaryDetailDto detail = getDetail(all);
        final BalanceSummaryDetailDto invoiceDetail = getDetail(invoices);
        final BalanceSummaryDetailDto receiptDetail = getDetail(receipts);
        final BalanceSummaryDetailDto ticketDetail = getDetail(tickets);
        final BalanceSummaryDetailDto creditNoteDetail = getDetail(creditNotes);

        return BalanceSummaryDto.builder()
                        .withUser(user)
                        .withCashEntries(cashEntries)
                        .withDetail(detail)
                        .withReceiptDetail(receiptDetail)
                        .withInvoiceDetail(invoiceDetail)
                        .withTicketDetail(ticketDetail)
                        .withCreditNoteDetaill(creditNoteDetail)
                        .withBalance(Converter.toBalanceDto(balance))
                        .build();
    }

    protected BalanceSummaryDetailDto getDetail(final Collection<? extends AbstractPayableDocument<?>> _payable)
    {
        final List<Payment> pay = _payable.stream()
                        .map(invoice -> invoice.getPayments())
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());
        final int count = pay.size();

        final Map<PaymentGroup, Long> counted = pay.stream()
                        .collect(Collectors.groupingBy(payment -> getPaymentGroup(payment), Collectors.counting()));

        final Map<PaymentGroup, BigDecimal> summed = pay.stream()
                        .collect(Collectors.groupingBy(payment -> getPaymentGroup(payment),
                                        Collectors.reducing(
                                                        BigDecimal.ZERO,
                                                        Payment::getAmount,
                                                        BigDecimal::add)));

        final Map<PaymentGroup, PaymentInfoDto> infos = new HashMap<>();

        for (final Entry<PaymentGroup, Long> entry : counted.entrySet()) {
            infos.put(entry.getKey(), entry.getKey().getInfo(entry.getValue().intValue(), null));
        }
        for (final Entry<PaymentGroup, BigDecimal> entry : summed.entrySet()) {
            if (infos.containsKey(entry.getKey())) {
                infos.put(entry.getKey(),
                                entry.getKey().getInfo(infos.get(entry.getKey()).getCount(), entry.getValue()));
            } else {
                infos.put(entry.getKey(), entry.getKey().getInfo(0, entry.getValue()));
            }
        }

        final BigDecimal netTotal = _payable.stream()
                        .map(AbstractPayableDocument::getNetTotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        final BigDecimal crossTotal = _payable.stream()
                        .map(AbstractPayableDocument::getCrossTotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        final List<TaxEntry> taxes = _payable.stream().map(invoice -> invoice.getTaxes())
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());

        taxes.stream().collect(Collectors.groupingBy(tax -> tax.getTax().getKey()));

        final Collection<List<TaxEntry>> taxmap = _payable.stream().map(invoice -> invoice.getTaxes())
                        .flatMap(Collection::stream)
                        .collect(Collectors.groupingBy(tax -> tax.getTax().getKey()))
                        .values();
        final List<TaxEntryDto> taxEntries = new ArrayList<>();
        for (final List<TaxEntry> taxlist : taxmap) {

            final BigDecimal amount = taxlist.stream()
                            .map(TaxEntry::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
            final BigDecimal base = taxlist.stream()
                            .map(TaxEntry::getBase)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

            taxEntries.add(TaxEntryDto.builder()
                            .withAmount(amount)
                            .withBase(base)
                            .withTax(Converter.toDto(taxlist.get(0).getTax()))
                            .build());
        }

        return BalanceSummaryDetailDto.builder()
                        .withDocumentCount(_payable.size())
                        .withPaymentCount(count)
                        .withNetTotal(netTotal)
                        .withCrossTotal(crossTotal)
                        .withPayments(infos.values())
                        .withtTaxEntries(taxEntries)
                        .build();
    }

    protected PaymentGroup getPaymentGroup(final Payment _payment)
    {
        return PaymentGroup.builder()
                        .withType(_payment.getType())
                        .withCardTypeId(_payment.getCardTypeId())
                        .withCardLabel(_payment.getCardLabel())
                        .build();
    }

    public List<Balance> getBalances()
    {
        return repository.findAll();
    }

    public void addCashEntries(final String _id, final List<CashEntryDto> cashEntries)
    {
        final var entities = cashEntries.stream()
                        .filter(dto -> dto.getAmount() != null)
                        .map(dto -> Converter.toEntity(dto))
                        .collect(Collectors.toList());
        cashEntryRepository.saveAll(entities);
    }
}
