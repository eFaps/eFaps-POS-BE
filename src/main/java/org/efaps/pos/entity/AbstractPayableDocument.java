/*
 * Copyright 2003 - 2019 The eFaps Team
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

package org.efaps.pos.entity;

import java.util.Set;

import org.efaps.pos.pojo.Payment;

public abstract class AbstractPayableDocument<T extends AbstractPayableDocument<T>>
    extends AbstractDocument<T>
{

    private String balanceOid;

    private Set<Payment> payments;

    public Set<Payment> getPayments()
    {
        return this.payments;
    }

    @SuppressWarnings("unchecked")
    public T setPayments(final Set<Payment> _payments)
    {
        this.payments = _payments;
        return (T) this;
    }

    public String getBalanceOid()
    {
        return this.balanceOid;
    }

    @SuppressWarnings("unchecked")
    public T setBalanceOid(final String _balanceOid)
    {
        this.balanceOid = _balanceOid;
        return (T) this;
    }
}
