/*
 * Copyright 2003 - 2018 The eFaps Team
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
 *
 */
package org.efaps.pos.service;

import org.efaps.pos.entity.Receipt;
import org.efaps.pos.entity.Sequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;


@Service
public class DocumentService
{
    private final MongoTemplate mongoTemplate;

    @Autowired
    public DocumentService(final MongoTemplate _mongoTemplate)
    {
        this.mongoTemplate = _mongoTemplate;
    }

    public Receipt createReceipt(final Receipt _receipt)
    {
        _receipt.setNumber(getNextNumber(getNumberKey()));
        this.mongoTemplate.insert(_receipt);
        return this.mongoTemplate.findById(_receipt.getId(), Receipt.class);
    }

    public String getNextNumber(final String _numberKey) {
        final Sequence sequence = this.mongoTemplate.findAndModify(new Query(Criteria.where("_id").is(_numberKey)),
                        new Update().inc("seq", 1),
                        FindAndModifyOptions.options().returnNew(true),
                        Sequence.class);
        return String.format(sequence.getFormat() == null ? "%05d" : sequence.getFormat(), sequence.getSeq());
    }

    public String getNumberKey() {
        return "5243.1-Receipt";
    }
}
