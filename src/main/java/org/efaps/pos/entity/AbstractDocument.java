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

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.efaps.pos.dto.Currency;
import org.efaps.pos.dto.DocStatus;
import org.efaps.pos.pojo.Discount;
import org.efaps.pos.pojo.EmployeeRelation;
import org.efaps.pos.pojo.Tax;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;

public abstract class AbstractDocument<T extends AbstractDocument<T>>
{

    @Id
    private String id;
    private String oid;
    private String number;
    private List<Item> items;
    private DocStatus status;
    private LocalDate date;
    private Currency currency;
    private BigDecimal netTotal;
    private BigDecimal crossTotal;
    private BigDecimal exchangeRate;
    private BigDecimal payableAmount;
    private Set<TaxEntry> taxes;
    private String contactOid;
    private String workspaceOid;
    private Discount discount;
    private String note;
    private Set<EmployeeRelation> employeeRelations;

    @CreatedBy
    private String user;

    @CreatedDate
    private Instant createdDate;

    @LastModifiedBy
    private String lastModifiedUser;

    @LastModifiedDate
    private Instant lastModifiedDate;

    @Version
    private long version;

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

    public List<Item> getItems()
    {
        return this.items == null ? new ArrayList<>() : this.items;
    }

    @SuppressWarnings("unchecked")
    public T setItems(final List<Item> _items)
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

    public Currency getCurrency()
    {
        return this.currency;
    }

    @SuppressWarnings("unchecked")
    public T setCurrency(final Currency _currency)
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

    public BigDecimal getExchangeRate()
    {
        return exchangeRate;
    }

    @SuppressWarnings("unchecked")
    public T setExchangeRate(final BigDecimal exchangeRate)
    {
        this.exchangeRate = exchangeRate;
        return (T) this;
    }

    public BigDecimal getPayableAmount()
    {
        return payableAmount;
    }

    @SuppressWarnings("unchecked")
    public T setPayableAmount(BigDecimal payableAmount)
    {
        this.payableAmount = payableAmount;
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

    public Discount getDiscount()
    {
        return discount;
    }

    @SuppressWarnings("unchecked")
    public T setDiscount(final Discount discount)
    {
        this.discount = discount;
        return (T) this;
    }

    public String getNote()
    {
        return note;
    }

    @SuppressWarnings("unchecked")
    public T setNote(final String note)
    {
        this.note = note;
        return (T) this;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(final String user)
    {
        this.user = user;
    }

    public Instant getCreatedDate()
    {
        return createdDate;
    }

    public void setCreatedDate(final Instant createdDate)
    {
        this.createdDate = createdDate;
    }

    public String getLastModifiedUser()
    {
        return lastModifiedUser;
    }

    public void setLastModifiedUser(final String lastModifiedUser)
    {
        this.lastModifiedUser = lastModifiedUser;
    }

    public Instant getLastModifiedDate()
    {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(final Instant lastModifiedDate)
    {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Set<EmployeeRelation> getEmployeeRelations()
    {
        return employeeRelations;
    }

    @SuppressWarnings("unchecked")
    public T setEmployeeRelations(Set<EmployeeRelation> employeeRelations)
    {
        this.employeeRelations = employeeRelations;
        return (T) this;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }

    public static class Item
    {

        private String oid;
        private Integer index;
        private Integer parentIdx;
        private String productOid;
        private String standInOid;
        private BigDecimal quantity;
        private BigDecimal netUnitPrice;
        private BigDecimal crossUnitPrice;
        private BigDecimal netPrice;
        private BigDecimal crossPrice;
        private Currency currency;
        private BigDecimal exchangeRate;
        private Set<TaxEntry> taxes;
        private String remark;

        public Integer getIndex()
        {
            return index;
        }

        public Item setIndex(final Integer _index)
        {
            index = _index;
            return this;
        }

        public Integer getParentIdx()
        {
            return parentIdx;
        }

        public Item setParentIdx(final Integer parentIdx)
        {
            this.parentIdx = parentIdx;
            return this;
        }

        public String getOid()
        {
            return oid;
        }

        public Item setOid(final String _oid)
        {
            oid = _oid;
            return this;
        }

        public String getProductOid()
        {
            return productOid;
        }

        public Item setProductOid(final String _productOid)
        {
            productOid = _productOid;
            return this;
        }

        public String getStandInOid()
        {
            return standInOid;
        }

        public Item setStandInOid(final String standInOid)
        {
            this.standInOid = standInOid;
            return this;
        }

        public BigDecimal getQuantity()
        {
            return quantity;
        }

        public Item setQuantity(final BigDecimal _quantity)
        {
            quantity = _quantity;
            return this;
        }

        public BigDecimal getNetUnitPrice()
        {
            return netUnitPrice;
        }

        public Item setNetUnitPrice(final BigDecimal _netUnitPrice)
        {
            netUnitPrice = _netUnitPrice;
            return this;
        }

        public BigDecimal getCrossUnitPrice()
        {
            return crossUnitPrice;
        }

        public Item setCrossUnitPrice(final BigDecimal _crossUnitPrice)
        {
            crossUnitPrice = _crossUnitPrice;
            return this;
        }

        public BigDecimal getNetPrice()
        {
            return netPrice;
        }

        public Item setNetPrice(final BigDecimal _netPrice)
        {
            netPrice = _netPrice;
            return this;
        }

        public BigDecimal getCrossPrice()
        {
            return crossPrice;
        }

        public Item setCrossPrice(final BigDecimal _crossPrice)
        {
            crossPrice = _crossPrice;
            return this;
        }

        public Currency getCurrency()
        {
            return currency;
        }

        public Item setCurrency(final Currency currency)
        {
            this.currency = currency;
            return this;
        }

        public BigDecimal getExchangeRate()
        {
            return exchangeRate;
        }

        public Item setExchangeRate(final BigDecimal exchangeRate)
        {
            this.exchangeRate = exchangeRate;
            return this;
        }

        public Set<TaxEntry> getTaxes()
        {
            return taxes;
        }

        public Item setTaxes(final Set<TaxEntry> _taxes)
        {
            taxes = _taxes;
            return this;
        }

        public String getRemark()
        {
            return remark;
        }

        public Item setRemark(final String remark)
        {
            this.remark = remark;
            return this;
        }

        @Override
        public String toString()
        {
            return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }

    public static class TaxEntry
    {

        private Tax tax;
        private BigDecimal base;
        private BigDecimal amount;
        private Currency currency;
        private BigDecimal exchangeRate;

        public Tax getTax()
        {
            return tax;
        }

        public TaxEntry setTax(final Tax _tax)
        {
            tax = _tax;
            return this;
        }

        public BigDecimal getBase()
        {
            return base;
        }

        public TaxEntry setBase(final BigDecimal _base)
        {
            base = _base;
            return this;
        }

        public BigDecimal getAmount()
        {
            return amount;
        }

        public TaxEntry setAmount(final BigDecimal _amount)
        {
            amount = _amount;
            return this;
        }

        public Currency getCurrency()
        {
            return currency;
        }

        public TaxEntry setCurrency(final Currency currency)
        {
            this.currency = currency;
            return this;
        }

        public BigDecimal getExchangeRate()
        {
            return exchangeRate;
        }

        public TaxEntry setExchangeRate(final BigDecimal exchangeRate)
        {
            this.exchangeRate = exchangeRate;
            return this;
        }

        @Override
        public String toString()
        {
            return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }
}
