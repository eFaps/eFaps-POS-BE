package org.efaps.pos.service;

import java.util.Collection;
import java.util.Optional;

import org.efaps.pos.client.EFapsClient;
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
public class InvoiceService extends PayablesService
{

    private static final Logger LOG = LoggerFactory.getLogger(InvoiceService.class);
    private final InvoiceRepository invoiceRepository;

    public InvoiceService(final EFapsClient eFapsClient,
                          final DocumentHelperService documentHelperService,
                          final ContactService contactService,
                          final BalanceService balanceService,
                          final InvoiceRepository invoiceRepository)
    {
        super(eFapsClient, documentHelperService, contactService, balanceService);
        this.invoiceRepository = invoiceRepository;
    }

    public boolean syncInvoices(final SyncInfo syncInfo)
    {
        boolean ret = false;
        final Collection<Invoice> tosync = invoiceRepository.findByOidIsNull();
        for (final Invoice invoice : tosync) {
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
            ret = true;
        }
        return ret;
    }
}
