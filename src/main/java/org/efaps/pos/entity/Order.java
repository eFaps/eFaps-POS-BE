package org.efaps.pos.entity;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "orders")
public class Order
    extends AbstractDocument<Order>
{

}
