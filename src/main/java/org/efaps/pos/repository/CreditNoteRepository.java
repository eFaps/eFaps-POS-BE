package org.efaps.pos.repository;

import java.util.Collection;
import java.util.List;

import org.efaps.pos.entity.CreditNote;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CreditNoteRepository
    extends MongoRepository<CreditNote, String>
{
    List<CreditNote> findBySourceDocOid(String _oid);

    Collection<CreditNote> findByOidIsNull();

    Collection<CreditNote> findByBalanceOid(String _balanceOid);
}
