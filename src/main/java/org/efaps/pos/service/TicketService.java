package org.efaps.pos.service;

import java.util.Collection;
import java.util.Optional;

import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.dto.DocStatus;
import org.efaps.pos.dto.TicketDto;
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

    public TicketService(final EFapsClient eFapsClient,
                         final DocumentHelperService documentHelperService,
                         final ContactService contactService,
                         final BalanceService balanceService,
                         final TicketRepository ticketRepository)
    {
        super(eFapsClient, documentHelperService, contactService, balanceService);
        this.ticketRepository = ticketRepository;
    }

    public boolean syncTickets(final SyncInfo syncInfo)
    {
        boolean ret = false;
        LOG.info("Syncing Tickets");
        final Collection<Ticket> tosync = ticketRepository.findByOidIsNull();
        for (final Ticket dto : tosync) {
            LOG.debug("Syncing Ticket: {}", dto);
            if (validateContact(dto) && verifyBalance(dto)) {
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
}
