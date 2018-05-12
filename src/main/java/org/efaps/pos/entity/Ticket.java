package org.efaps.pos.entity;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tickets")
public class Ticket
    extends AbstractDocument<Ticket>
{

}
