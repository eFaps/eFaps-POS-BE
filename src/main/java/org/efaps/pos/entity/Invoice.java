package org.efaps.pos.entity;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "invoices")
public class Invoice
    extends AbstractDocument<Invoice>
{

}
