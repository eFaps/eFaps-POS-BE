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
package org.efaps.pos.entity;

import org.efaps.pos.pojo.Spot;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "orders")
public class Order
    extends AbstractDocument<Order>
{

    private Spot spot;
    private String payableOid;
    private String shoutout;
    private String orderOptionKey;

    public Spot getSpot()
    {
        return spot;
    }

    public Order setSpot(final Spot _spot)
    {
        spot = _spot;
        return this;
    }

    public String getPayableOid()
    {
        return payableOid;
    }

    public Order setPayableOid(final String payableOid)
    {
        this.payableOid = payableOid;
        return this;
    }

    public String getShoutout()
    {
        return shoutout;
    }

    public Order setShoutout(final String shoutout)
    {
        this.shoutout = shoutout;
        return this;
    }

    public String getOrderOptionKey()
    {
        return orderOptionKey;
    }

    public Order setOrderOptionKey(final String orderOptionKey)
    {
        this.orderOptionKey = orderOptionKey;
        return this;
    }
}
