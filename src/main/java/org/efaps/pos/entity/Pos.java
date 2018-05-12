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

package org.efaps.pos.entity;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "poss")
public class Pos
{

    @Id
    private String id;

    private String oid;

    private String name;

    private String currency;

    private Company company;

    private String defaultContactOid;

    private String receiptSeqOid;
    private String invoiceSeqOid;
    private String ticketSeqOid;

    public String getOid()
    {
        return this.oid;
    }

    public Pos setOid(final String _oid)
    {
        this.oid = _oid;
        this.id = _oid;
        return this;
    }

    public String getName()
    {
        return this.name;
    }

    public Pos setName(final String _name)
    {
        this.name = _name;
        return this;
    }

    public String getCurrency()
    {
        return this.currency;
    }

    public Pos setCurrency(final String _currency)
    {
        this.currency = _currency;
        return this;
    }

    public Company getCompany()
    {
        return this.company;
    }

    public Pos setCompany(final Company _company)
    {
        this.company = _company;
        return this;
    }

    public String getDefaultContactOid()
    {
        return this.defaultContactOid;
    }

    public Pos setDefaultContactOid(final String _defaultContactOid)
    {
        this.defaultContactOid = _defaultContactOid;
        return this;
    }

    public String getReceiptSeqOid()
    {
        return this.receiptSeqOid;
    }

    public void setReceiptSeqOid(final String _receiptSeqOid)
    {
        this.receiptSeqOid = _receiptSeqOid;
    }

    public String getInvoiceSeqOid()
    {
        return this.invoiceSeqOid;
    }

    public void setInvoiceSeqOid(final String _invoiceSeqOid)
    {
        this.invoiceSeqOid = _invoiceSeqOid;
    }

    public String getTicketSeqOid()
    {
        return this.ticketSeqOid;
    }

    public void setTicketSeqOid(final String _ticketSeqOid)
    {
        this.ticketSeqOid = _ticketSeqOid;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this);
    }

    public static class Company
    {

        private String name;
        private String taxNumber;

        public String getName()
        {
            return this.name;
        }

        public Company setName(final String _name)
        {
            this.name = _name;
            return this;
        }

        public String getTaxNumber()
        {
            return this.taxNumber;
        }

        public Company setTaxNumber(final String _taxNumber)
        {
            this.taxNumber = _taxNumber;
            return this;
        }
    }
}
