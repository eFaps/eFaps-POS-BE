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
        AbstractPayableDocument<T> doc = null;
        final var opt = INSTANCE.documentService.findReceipt(ident);
        if (opt.isPresent()) {
            doc = opt.get();
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
            final var opt2 = INSTANCE.documentService.findInvoice(ident);
            if (opt2.isPresent()) {
                doc = opt2.get();
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
        return ret;
    }
}
