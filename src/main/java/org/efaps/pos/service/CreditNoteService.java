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

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.config.ConfigProperties;
import org.efaps.pos.dto.DocStatus;
import org.efaps.pos.dto.RedeemValidityDto;
import org.efaps.pos.dto.RedeemValidityStatus;
import org.efaps.pos.entity.AbstractDocument;
import org.efaps.pos.entity.CreditNote;
import org.efaps.pos.entity.Origin;
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
            if (validateContacts(creditNote, false) && verifyBalance(creditNote, false) && verifySourceDoc(creditNote)) {
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

    @Override
    protected void persist(final AbstractDocument<?> document)
    {
        if (document instanceof CreditNote) {
            creditNoteRepository.save((CreditNote) document);
        }
    }

    public RedeemValidityDto redeemValidity(final String ident)
    {
        LOG.info("Checking redeem validity for {}", ident);
        final var creditNoteOpt = getDocumentHelperService().getCreditNote(ident);
        if (creditNoteOpt.isPresent()) {
            final var creditNote = creditNoteOpt.get();
            // if already redeemed locally
            if (creditNote.getRedeemedById() != null) {
                return RedeemValidityDto.builder()
                                .withStatus(RedeemValidityStatus.FULLY)
                                .build();
            }

            // if it is a locally created creditnote and not redeemed and not
            // synced or less than 30 minutes old
            if (Origin.LOCAL.equals(creditNote.getOrigin()) && (creditNote.getOid() == null
                            || creditNote.getCreatedDate().plus(Duration.ofMinutes(30)).isAfter(Instant.now()))) {
                return RedeemValidityDto.builder()
                                .withStatus(RedeemValidityStatus.OPEN)
                                .build();
            }
            return getEFapsClient().getCreditNoteRedeemValidity(creditNote.getOid());
        } else if (Utils.isOid(ident)) {
            return getEFapsClient().getCreditNoteRedeemValidity(ident);
        }
        return null;
    }

    public List<CreditNote> retrieveCreditNotes(final String number)
    {
        List<CreditNote> creditNotes = creditNoteRepository.findByNumber(number);
        if (creditNotes.isEmpty()) {
            final var retrieved = getEFapsClient().retrieveCreditNotes(number);
            if (retrieved != null) {
                // if we pull from cloud set the id = oid
                creditNotes = retrieved.stream()
                                .map(Converter::toEntity)
                                .map(entity -> {
                                    entity.setId(entity.getOid());
                                    return entity;
                                })
                                .toList();
            }
        }
        return creditNotes;
    }
}
