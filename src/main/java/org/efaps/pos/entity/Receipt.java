package org.efaps.pos.entity;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "receipts")
public class Receipt
    extends AbstractDocument<Receipt>
{

}
