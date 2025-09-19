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

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.config.ConfigProperties;
import org.efaps.pos.dto.DocStatus;
import org.efaps.pos.dto.InvoiceDto;
import org.efaps.pos.entity.Invoice;
import org.efaps.pos.entity.SyncInfo;
import org.efaps.pos.repository.InvoiceRepository;
import org.efaps.pos.util.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class InvoiceService
    extends PayablesService
{

    private static final Logger LOG = LoggerFactory.getLogger(InvoiceService.class);
    private final InvoiceRepository invoiceRepository;

    public InvoiceService(final ConfigProperties configProperties,
                          final EFapsClient eFapsClient,
                          final DocumentHelperService documentHelperService,
                          final ContactService contactService,
                          final BalanceService balanceService,
                          final InvoiceRepository invoiceRepository)
    {
        super(configProperties, eFapsClient, documentHelperService, contactService, balanceService);
        this.invoiceRepository = invoiceRepository;
        LOG.info("Started {} with defer sync of {}s", this.getClass().getSimpleName(),
                        getConfigProperties().getBeInst().getInvoice().getDeferSync());
    }

    public boolean syncInvoices(final SyncInfo syncInfo)
    {
        final Collection<Invoice> tosync = invoiceRepository.findByOidIsNull().stream()
                        .filter(entity -> entity.getLastModifiedDate()
                                        .plusSeconds(getConfigProperties().getBeInst().getInvoice().getDeferSync())
                                        .isBefore(Instant.now()))
                        .toList();
        syncInvoices(tosync);
        return true;
    }

    public List<Invoice> syncInvoices(final Collection<Invoice> invoices)
    {
        final List<Invoice> updates = new ArrayList<>();
        for (final Invoice invoice : invoices) {
            LOG.debug("Syncing Invoice: {}", invoice);
            if (validateContact(invoice) && verifyBalance(invoice)) {
                final InvoiceDto recDto = getEFapsClient().postInvoice(Converter.toInvoiceDto(invoice));
                LOG.debug("received Invoice: {}", recDto);
                if (recDto.getOid() != null) {
                    final Optional<Invoice> opt = invoiceRepository.findById(recDto.getId());
                    if (opt.isPresent()) {
                        final Invoice receipt = opt.get();
                        receipt.setOid(recDto.getOid());
                        receipt.setStatus(DocStatus.CLOSED);
                        invoiceRepository.save(receipt);
                    }
                }
            } else {
                LOG.debug("skipped Invoice: {}", invoice);
            }
        }
        return updates;
    }
}
