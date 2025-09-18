package org.efaps.pos.service;

import java.util.Optional;

import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.entity.AbstractPayableDocument;
import org.efaps.pos.entity.Balance;
import org.efaps.pos.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PayablesService
    extends DocumentsService
{

    private static final Logger LOG = LoggerFactory.getLogger(PayablesService.class);

    private final BalanceService balanceService;

    public PayablesService(final EFapsClient eFapsClient,
                           final DocumentHelperService documentHelperService,
                           final ContactService contactService,
                           final BalanceService balanceService)
    {
        super(eFapsClient, documentHelperService, contactService);
        this.balanceService = balanceService;
    }

    protected boolean verifyBalance(final AbstractPayableDocument<?> document)
    {
        boolean ret = true;
        if (!Utils.isOid(document.getBalanceOid())) {
            ret = false;
            final Optional<Balance> balanceOpt = balanceService.findById(document.getBalanceOid());
            if (balanceOpt.isPresent()) {
                final Balance balance = balanceOpt.get();
                if (Utils.isOid(balance.getOid())) {
                    document.setBalanceOid(balance.getOid());
                    ret = true;
                } else {
                    LOG.error("The found Balance does not have an OID {}", balance);
                }
            }
        }
        return ret;
    }
}
