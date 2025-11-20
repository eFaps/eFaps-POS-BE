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
import org.efaps.pos.dto.ReceiptDto;
import org.efaps.pos.entity.AbstractDocument;
import org.efaps.pos.entity.Receipt;
import org.efaps.pos.entity.SyncInfo;
import org.efaps.pos.repository.ReceiptRepository;
import org.efaps.pos.util.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ReceiptService
    extends PayablesService
{

    private static final Logger LOG = LoggerFactory.getLogger(ReceiptService.class);
    private final ReceiptRepository receiptRepository;

    public ReceiptService(final ConfigProperties configProperties,
                          final EFapsClient eFapsClient,
                          final DocumentHelperService documentHelperService,
                          final ContactService contactService,
                          final BalanceService balanceService,
                          final ReceiptRepository receiptRepository)
    {
        super(configProperties, eFapsClient, documentHelperService, contactService, balanceService);
        this.receiptRepository = receiptRepository;
        LOG.info("Started {} with defer sync of {}s", this.getClass().getSimpleName(),
                        getConfigProperties().getBeInst().getReceipt().getDeferSync());
    }

    public boolean syncReceipts(final SyncInfo syncInfo)
    {
        LOG.info("Syncing Receipts");
        final Collection<Receipt> tosync = receiptRepository.findByOidIsNull().stream()
                        .filter(entity -> entity.getLastModifiedDate()
                                        .plusSeconds(getConfigProperties().getBeInst().getReceipt().getDeferSync())
                                        .isBefore(Instant.now()))
                        .toList();
        syncReceipts(tosync, false);
        return true;
    }

    public List<Receipt> syncReceipts(final Collection<Receipt> receipts,
                                      boolean ensure)
    {
        final List<Receipt> updates = new ArrayList<>();
        for (final Receipt receipt : receipts) {
            if (validateContact(receipt, ensure) && verifyBalance(receipt, ensure)) {
                LOG.debug("Syncing Receipt: {}", receipt);
                final ReceiptDto recDto = getEFapsClient().postReceipt(Converter.toReceiptDto(receipt));
                LOG.debug("received Receipt: {}", recDto);
                if (recDto.getOid() != null) {
                    final Optional<Receipt> receiptOpt = receiptRepository.findById(recDto.getId());
                    if (receiptOpt.isPresent()) {
                        final Receipt retReceipt = receiptOpt.get();
                        retReceipt.setOid(recDto.getOid());
                        retReceipt.setStatus(DocStatus.CLOSED);
                        receiptRepository.save(retReceipt);
                        updates.add(retReceipt);
                    }
                }
            } else {
                LOG.debug("skipped Receipt: {}", receipt);
            }
        }
        return updates;
    }

    @Override
    protected void persist(final AbstractDocument<?> document)
    {
        if (document instanceof Receipt) {
            receiptRepository.save((Receipt) document);
        }
    }
}
