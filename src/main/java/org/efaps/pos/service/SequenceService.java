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

import org.efaps.pos.dto.DocType;
import org.efaps.pos.entity.Pos;
import org.efaps.pos.entity.Sequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class SequenceService
{
    private final MongoTemplate mongoTemplate;
    private final PosService posService;

    @Autowired
    public SequenceService(final MongoTemplate _mongoTemplate, final PosService _posService) {
        mongoTemplate = _mongoTemplate;
        posService = _posService;
    }

    public String getNextOrder() {
        return getNextNumber("Order", false);
    }

    public String getNext(final String _workspaceOid, final DocType _docType, final DocType _sourceDocType)
    {
        final Pos pos = posService.getPos4Workspace(_workspaceOid);
        final String next;
        switch (_docType) {
            case RECEIPT:
                if (pos.getReceiptSeqOid() != null) {
                    next = getNextNumber(pos.getReceiptSeqOid(), true);
                } else {
                    next = getNextNumber("Receipt", false);
                }
                break;
            case INVOICE:
                if (pos.getInvoiceSeqOid() != null) {
                    next = getNextNumber(pos.getInvoiceSeqOid(), true);
                } else {
                    next = getNextNumber("Invoice", false);
                }
                break;
            case TICKET:
                if (pos.getTicketSeqOid() != null) {
                    next = getNextNumber(pos.getTicketSeqOid(), true);
                } else {
                    next = getNextNumber("Ticket", false);
                }
                break;
            case CREDITNOTE:
                if (DocType.INVOICE.equals(_sourceDocType)) {
                    if (pos.getCreditNote4InvoiceSeqOid()!= null) {
                        next = getNextNumber(pos.getCreditNote4InvoiceSeqOid(), true);
                    } else {
                        next = getNextNumber("CreditNote4Invoice", false);
                    }
                } else {
                    if (pos.getCreditNote4ReceiptSeqOid()!= null) {
                        next = getNextNumber(pos.getCreditNote4ReceiptSeqOid(), true);
                    } else {
                        next = getNextNumber("CreditNote4Receipt", false);
                    }
                }
                break;
            default:
                next = getNextNumber("UNKNOWN", false);
                break;
        }
        return next;
    }

    public String getNextNumber(final String _key, final boolean _isOid) {
        final Sequence sequence = mongoTemplate.findAndModify(
                        new Query(Criteria.where(_isOid ? "oid" : "_id").is(_key)),
                        new Update().inc("seq", 1),
                        FindAndModifyOptions.options().returnNew(true),
                        Sequence.class);
        if (sequence == null) {
            if (_isOid) {
                mongoTemplate.insert(new Sequence().setOid(_key).setSeq(0));
            } else {
                mongoTemplate.insert(new Sequence().setId(_key).setSeq(0));
            }
            return getNextNumber(_key, _isOid);
        }
        return String.format(sequence.getFormat() == null ? "%05d" : sequence.getFormat(), sequence.getSeq());
    }
}
