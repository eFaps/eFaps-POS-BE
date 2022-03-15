package org.efaps.pos.repository;

import org.efaps.pos.entity.CreditNote;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CreditNoteRepository
    extends MongoRepository<CreditNote, String>
{

}
