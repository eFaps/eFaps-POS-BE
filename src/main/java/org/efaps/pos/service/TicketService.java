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
import org.efaps.pos.dto.TicketDto;
import org.efaps.pos.entity.AbstractDocument;
import org.efaps.pos.entity.SyncInfo;
import org.efaps.pos.entity.Ticket;
import org.efaps.pos.repository.TicketRepository;
import org.efaps.pos.util.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TicketService
    extends PayablesService
{

    private static final Logger LOG = LoggerFactory.getLogger(TicketService.class);
    private final TicketRepository ticketRepository;

    public TicketService(final ConfigProperties configProperties,
                         final EFapsClient eFapsClient,
                         final DocumentHelperService documentHelperService,
                         final ContactService contactService,
                         final BalanceService balanceService,
                         final TicketRepository ticketRepository)
    {
        super(configProperties, eFapsClient, documentHelperService, contactService, balanceService);
        this.ticketRepository = ticketRepository;
    }

    public boolean syncTickets(final SyncInfo syncInfo)
    {
        final Collection<Ticket> tosync = ticketRepository.findByOidIsNull();
        syncTickets(tosync, false);
        return true;
    }

    public boolean syncTickets(final Collection<Ticket> tosync,
                               boolean ensure)
    {
        boolean ret = false;
        LOG.info("Syncing Tickets");
        for (final Ticket dto : tosync) {
            LOG.debug("Syncing Ticket: {}", dto);
            if (validateContacts(dto, ensure) && verifyBalance(dto, ensure)) {
                final TicketDto recDto = getEFapsClient().postTicket(Converter.toTicketDto(dto));
                LOG.debug("received Ticket: {}", recDto);
                if (recDto.getOid() != null) {
                    final Optional<Ticket> opt = ticketRepository.findById(recDto.getId());
                    if (opt.isPresent()) {
                        final Ticket receipt = opt.get();
                        receipt.setOid(recDto.getOid());
                        receipt.setStatus(DocStatus.CLOSED);
                        ticketRepository.save(receipt);
                    }
                }
            } else {
                LOG.debug("skipped Ticket: {}", dto);
            }
            ret = true;
        }
        return ret;
    }

    @Override
    protected void persist(final AbstractDocument<?> document)
    {
        if (document instanceof Ticket) {
            ticketRepository.save((Ticket) document);
        }
    }
}
