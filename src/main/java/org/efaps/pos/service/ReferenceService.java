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

import java.time.LocalDate;

import org.efaps.pos.dto.DocType;
import org.efaps.pos.entity.AbstractPayableDocument;
import org.efaps.pos.interfaces.IReference;
import org.springframework.stereotype.Service;

@Service
public class ReferenceService
{

    private static ReferenceService INSTANCE;

    private final DocumentService documentService;

    public ReferenceService(final DocumentService documentService)
    {
        INSTANCE = this;
        this.documentService = documentService;
    }

    public static IReference getReferenceByIdent(final String ident)
    {
        IReference ret = null;
        if (ident != null) {
            final var opt = INSTANCE.documentService.findReceipt(ident, false);
            if (opt.isPresent()) {
                final AbstractPayableDocument<?> doc = opt.get();
                ret = new IReference()
                {

                    @Override
                    public LocalDate getDate()
                    {
                        return doc.getDate();
                    }

                    @Override
                    public DocType getDocType()
                    {
                        return DocType.RECEIPT;
                    }

                    @Override
                    public String getNumber()
                    {
                        return doc.getNumber();
                    }

                };
            } else {
                final var opt2 = INSTANCE.documentService.findInvoice(ident, false);
                if (opt2.isPresent()) {
                    final AbstractPayableDocument<?> doc = opt2.get();
                    ret = new IReference()
                    {

                        @Override
                        public LocalDate getDate()
                        {
                            return doc.getDate();
                        }

                        @Override
                        public DocType getDocType()
                        {
                            return DocType.INVOICE;
                        }

                        @Override
                        public String getNumber()
                        {
                            return doc.getNumber();
                        }

                    };
                }
            }
        }
        return ret;
    }
}
