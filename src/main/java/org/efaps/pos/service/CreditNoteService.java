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

import java.util.Collection;
import java.util.Optional;

import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.config.ConfigProperties;
import org.efaps.pos.dto.DocStatus;
import org.efaps.pos.entity.CreditNote;
import org.efaps.pos.entity.SyncInfo;
import org.efaps.pos.repository.CreditNoteRepository;
import org.efaps.pos.util.Converter;
import org.efaps.pos.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CreditNoteService
    extends PayablesService
{

    private static final Logger LOG = LoggerFactory.getLogger(CreditNoteService.class);
    private final CreditNoteRepository creditNoteRepository;

    public CreditNoteService(final ConfigProperties configProperties,
                             final EFapsClient eFapsClient,
                             final ContactService contactService,
                             final DocumentHelperService documentHelperService,
                             final BalanceService balanceService,
                             final CreditNoteRepository creditNoteRepository)
    {
        super(configProperties, eFapsClient, documentHelperService, contactService, balanceService);
        this.creditNoteRepository = creditNoteRepository;
    }

    public boolean syncCreditNotes(final SyncInfo syncInfo)
    {
        boolean ret = false;
        LOG.info("Syncing CreditNotes");
        final Collection<CreditNote> tosync = creditNoteRepository.findByOidIsNull();
        for (final CreditNote creditNote : tosync) {
            LOG.debug("Syncing CreditNote: {}", creditNote);
            if (validateContact(creditNote) && verifyBalance(creditNote) && verifySourceDoc(creditNote)) {
                final var recDto = getEFapsClient().postCreditNote(Converter.toCreditNoteDto(creditNote));
                LOG.debug("received CreditNote: {}", recDto);
                if (recDto.getOid() != null) {
                    final Optional<CreditNote> opt = creditNoteRepository.findById(recDto.getId());
                    if (opt.isPresent()) {
                        final CreditNote receipt = opt.get();
                        receipt.setOid(recDto.getOid());
                        receipt.setStatus(DocStatus.CLOSED);
                        creditNoteRepository.save(receipt);
                    }
                }
            } else {
                LOG.debug("skipped CreditNote: {}", creditNote);
            }
        }
        ret = true;
        return ret;
    }

    private boolean verifySourceDoc(final CreditNote creditNote)
    {
        boolean ret = false;
        if (Utils.isOid(creditNote.getSourceDocOid())) {
            ret = true;
        } else {
            final var payableOpt = getDocumentHelperService().getPayableById(creditNote.getSourceDocOid());
            if (payableOpt.isPresent() && Utils.isOid(payableOpt.get().getOid())) {
                creditNote.setSourceDocOid(payableOpt.get().getOid());
                creditNoteRepository.save(creditNote);
                ret = true;
            }
        }
        return ret;
    }
}
