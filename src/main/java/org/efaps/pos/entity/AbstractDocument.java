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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.efaps.pos.dto.DocStatus;
import org.springframework.data.annotation.Id;

public abstract class AbstractDocument<T>
{

    @Id
    private String id;
    private String oid;
    private String number;
    private Set<Item> items;
    private DocStatus status;
    private LocalDate date;
    private String currency;
    private BigDecimal netTotal;
    private BigDecimal crossTotal;
    private Set<TaxEntry> taxes;
    private String contactOid;
    private String workspaceOid;

    public String getId()
    {
        return this.id;
    }

    @SuppressWarnings("unchecked")
    public T setId(final String _id)
    {
        this.id = _id;
        return (T) this;
    }

    public String getOid()
    {
        return this.oid;
    }

    @SuppressWarnings("unchecked")
    public T setOid(final String _oid)
    {
        this.oid = _oid;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setNumber(final String _number)
    {
        this.number = _number;
        return (T) this;
    }

    public String getNumber()
    {
        return this.number;
    }

    public LocalDate getDate()
    {
        return this.date;
    }

    @SuppressWarnings("unchecked")
    public T setDate(final LocalDate _date)
    {
        this.date = _date;
        return (T) this;
    }

    public Set<Item> getItems()
    {
        return this.items == null ? Collections.emptySet() : this.items;
    }

    @SuppressWarnings("unchecked")
    public T setItems(final Set<Item> _items)
    {
        this.items = _items;
        return (T) this;
    }

    public DocStatus getStatus()
    {
        return this.status;
    }

    @SuppressWarnings("unchecked")
    public T setStatus(final DocStatus _status)
    {
        this.status = _status;
        return (T) this;
    }

    public String getCurrency()
    {
        return this.currency;
    }

    @SuppressWarnings("unchecked")
    public T setCurrency(final String _currency)
    {
        this.currency = _currency;
        return (T) this;
    }

    public BigDecimal getNetTotal()
    {
        return this.netTotal;
    }

    @SuppressWarnings("unchecked")
    public T setNetTotal(final BigDecimal _netTotal)
    {
        this.netTotal = _netTotal;
        return (T) this;
    }

    public BigDecimal getCrossTotal()
    {
        return this.crossTotal;
    }

    @SuppressWarnings("unchecked")
    public T setCrossTotal(final BigDecimal _crossTotal)
    {
        this.crossTotal = _crossTotal;
        return (T) this;
    }

    public Set<TaxEntry> getTaxes()
    {
        return this.taxes;
    }

    @SuppressWarnings("unchecked")
    public T setTaxes(final Set<TaxEntry> _taxes)
    {
        this.taxes = _taxes;
        return (T) this;
    }

    public String getContactOid()
    {
        return this.contactOid;
    }

    @SuppressWarnings("unchecked")
    public T setContactOid(final String _contactOid)
    {
        this.contactOid = _contactOid;
        return (T) this;
    }

    public String getWorkspaceOid()
    {
        return this.workspaceOid;
    }

    @SuppressWarnings("unchecked")
    public T setWorkspaceOid(final String _workspaceOid)
    {
        this.workspaceOid = _workspaceOid;
        return (T) this;
    }

    @Override
    public String toString() {
       return ToStringBuilder.reflectionToString(this);
    }

    public static class Item
    {

        private String oid;
        private Integer index;
        private String productOid;
        private BigDecimal quantity;
        private BigDecimal netUnitPrice;
        private BigDecimal crossUnitPrice;
        private BigDecimal netPrice;
        private BigDecimal crossPrice;
        private Set<TaxEntry> taxes;

        public Integer getIndex()
        {
            return this.index;
        }

        public Item setIndex(final Integer _index)
        {
            this.index = _index;
            return this;
        }

        public String getOid()
        {
            return this.oid;
        }

        public Item setOid(final String _oid)
        {
            this.oid = _oid;
            return this;
        }

        public String getProductOid()
        {
            return this.productOid;
        }

        public Item setProductOid(final String _productOid)
        {
            this.productOid = _productOid;
            return this;
        }

        public BigDecimal getQuantity()
        {
            return this.quantity;
        }

        public Item setQuantity(final BigDecimal _quantity)
        {
            this.quantity = _quantity;
            return this;
        }

        public BigDecimal getNetUnitPrice()
        {
            return this.netUnitPrice;
        }

        public Item setNetUnitPrice(final BigDecimal _netUnitPrice)
        {
            this.netUnitPrice = _netUnitPrice;
            return this;
        }

        public BigDecimal getCrossUnitPrice()
        {
            return this.crossUnitPrice;
        }

        public Item setCrossUnitPrice(final BigDecimal _crossUnitPrice)
        {
            this.crossUnitPrice = _crossUnitPrice;
            return this;
        }

        public BigDecimal getNetPrice()
        {
            return this.netPrice;
        }

        public Item setNetPrice(final BigDecimal _netPrice)
        {
            this.netPrice = _netPrice;
            return this;
        }

        public BigDecimal getCrossPrice()
        {
            return this.crossPrice;
        }

        public Item setCrossPrice(final BigDecimal _crossPrice)
        {
            this.crossPrice = _crossPrice;
            return this;
        }

        public Set<TaxEntry> getTaxes()
        {
            return this.taxes;
        }

        public Item setTaxes(final Set<TaxEntry> _taxes)
        {
            this.taxes = _taxes;
            return this;
        }
    }

    public static class TaxEntry
    {

        private Tax tax;
        private BigDecimal base;
        private BigDecimal amount;

        public Tax getTax()
        {
            return this.tax;
        }

        public TaxEntry setTax(final Tax _tax)
        {
            this.tax = _tax;
            return this;
        }

        public BigDecimal getBase()
        {
            return this.base;
        }

        public TaxEntry setBase(final BigDecimal _base)
        {
            this.base = _base;
            return this;
        }

        public BigDecimal getAmount()
        {
            return this.amount;
        }

        public TaxEntry setAmount(final BigDecimal _amount)
        {
            this.amount = _amount;
            return this;
        }
    }
}
