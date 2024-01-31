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

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.efaps.pos.dto.Currency;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "poss")
public class Pos
{

    @Id
    private String id;

    private String oid;

    private String name;

    private Currency currency;

    private Company company;

    private String defaultContactOid;

    private String receiptSeqOid;
    private String invoiceSeqOid;
    private String ticketSeqOid;
    private String creditNote4InvoiceSeqOid;
    private String creditNote4ReceiptSeqOid;

    public String getOid()
    {
        return oid;
    }

    public Pos setOid(final String _oid)
    {
        oid = _oid;
        id = _oid;
        return this;
    }

    public String getName()
    {
        return name;
    }

    public Pos setName(final String _name)
    {
        name = _name;
        return this;
    }

    public Currency getCurrency()
    {
        return currency;
    }

    public Pos setCurrency(final Currency _currency)
    {
        currency = _currency;
        return this;
    }

    public Company getCompany()
    {
        return company;
    }

    public Pos setCompany(final Company _company)
    {
        company = _company;
        return this;
    }

    public String getDefaultContactOid()
    {
        return defaultContactOid;
    }

    public Pos setDefaultContactOid(final String _defaultContactOid)
    {
        defaultContactOid = _defaultContactOid;
        return this;
    }

    public String getReceiptSeqOid()
    {
        return receiptSeqOid;
    }

    public Pos setReceiptSeqOid(final String _receiptSeqOid)
    {
        receiptSeqOid = _receiptSeqOid;
        return this;
    }

    public String getInvoiceSeqOid()
    {
        return invoiceSeqOid;
    }

    public Pos setInvoiceSeqOid(final String _invoiceSeqOid)
    {
        invoiceSeqOid = _invoiceSeqOid;
        return this;
    }

    public String getTicketSeqOid()
    {
        return ticketSeqOid;
    }

    public Pos setTicketSeqOid(final String _ticketSeqOid)
    {
        ticketSeqOid = _ticketSeqOid;
        return this;
    }

    public String getCreditNote4InvoiceSeqOid()
    {
        return creditNote4InvoiceSeqOid;
    }

    public Pos setCreditNote4InvoiceSeqOid(final String creditNote4InvoiceSeqOid)
    {
        this.creditNote4InvoiceSeqOid = creditNote4InvoiceSeqOid;
        return this;
    }

    public String getCreditNote4ReceiptSeqOid()
    {
        return creditNote4ReceiptSeqOid;
    }

    public Pos setCreditNote4ReceiptSeqOid(final String creditNote4ReceiptSeqOid)
    {
        this.creditNote4ReceiptSeqOid = creditNote4ReceiptSeqOid;
        return this;
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
            return name;
        }

        public Company setName(final String _name)
        {
            name = _name;
            return this;
        }

        public String getTaxNumber()
        {
            return taxNumber;
        }

        public Company setTaxNumber(final String _taxNumber)
        {
            taxNumber = _taxNumber;
            return this;
        }
    }
}
