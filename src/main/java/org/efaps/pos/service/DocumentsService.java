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

import java.util.Collections;
import java.util.Optional;

import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.config.ConfigProperties;
import org.efaps.pos.entity.AbstractDocument;
import org.efaps.pos.entity.Contact;
import org.efaps.pos.entity.Order;
import org.efaps.pos.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DocumentsService
{

    private static final Logger LOG = LoggerFactory.getLogger(DocumentsService.class);

    private final ConfigProperties configProperties;
    private final EFapsClient eFapsClient;
    private final DocumentHelperService documentHelperService;
    private final ContactService contactService;

    public DocumentsService(final ConfigProperties configProperties,
                            final EFapsClient eFapsClient,
                            final DocumentHelperService documentHelperService,
                            final ContactService contactService)
    {
        this.configProperties = configProperties;
        this.eFapsClient = eFapsClient;
        this.documentHelperService = documentHelperService;
        this.contactService = contactService;
    }

    protected ConfigProperties getConfigProperties()
    {
        return configProperties;
    }

    protected EFapsClient getEFapsClient()
    {
        return eFapsClient;
    }

    protected DocumentHelperService getDocumentHelperService()
    {
        return documentHelperService;
    }

    public boolean validateContact(final AbstractDocument<?> document,
                                   final Boolean ensure)
    {
        boolean ret = false;
        if (Utils.isOid(document.getContactOid())) {
            ret = contactService.findOneByOid(document.getContactOid()).isPresent();
        } else if (document.getContactOid() != null) {
            final Optional<Contact> optContact = contactService.findById(document.getContactOid());
            if (optContact.isPresent()) {
                final var contact = optContact.get();
                if (Utils.isOid(contact.getOid())) {
                    document.setContactOid(contact.getOid());
                    persist(document);
                    ret = true;
                } else if (ensure) {
                    LOG.info("Ensure valid contact by syncing {}", contact);
                    final var syncedContacts = contactService.syncContactsUp(Collections.singleton(contact));
                    if (!syncedContacts.isEmpty()) {
                        document.setContactOid(syncedContacts.get(0).getOid());
                        persist(document);
                        ret = true;
                    }
                }
            }
        }
        if (!ret && !(document instanceof Order)) {
            LOG.info("Invalid contact for {}", document);
        }
        return ret;
    }

    protected abstract void persist(final AbstractDocument<?> document);

}
