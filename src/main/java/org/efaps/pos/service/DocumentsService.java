package org.efaps.pos.service;

import java.util.Optional;

import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.entity.AbstractDocument;
import org.efaps.pos.entity.Contact;
import org.efaps.pos.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DocumentsService
{

    private static final Logger LOG = LoggerFactory.getLogger(DocumentsService.class);

    private final EFapsClient eFapsClient;
    private final DocumentHelperService documentHelperService;

    private final ContactService contactService;

    public DocumentsService(final EFapsClient eFapsClient,
                            final DocumentHelperService documentHelperService,
                            final ContactService contactService)
    {
        this.eFapsClient = eFapsClient;
        this.documentHelperService = documentHelperService;
        this.contactService = contactService;
    }

    protected EFapsClient getEFapsClient()
    {
        return eFapsClient;
    }

    protected DocumentHelperService getDocumentHelperService()
    {
        return documentHelperService;
    }

    protected boolean validateContact(final AbstractDocument<?> document)
    {
        boolean ret = false;
        if (Utils.isOid(document.getContactOid())) {
            ret = contactService.findOneByOid(document.getContactOid()).isPresent();
        } else if (document.getContactOid() != null) {
            final Optional<Contact> optContact = contactService.findById(document.getContactOid());
            if (optContact.isPresent()) {
                final String contactOid = optContact.get().getOid();
                if (Utils.isOid(contactOid)) {
                    document.setContactOid(contactOid);
                    ret = true;
                }
            }
        }
        if (!ret) {
            LOG.info("Invalid contact for {}", document);
        }
        return ret;
    }

}
