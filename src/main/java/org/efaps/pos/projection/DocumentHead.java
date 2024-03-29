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
package org.efaps.pos.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.efaps.pos.dto.Currency;
import org.efaps.pos.dto.DocStatus;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class DocumentHead
{

    private String id;
    private String number;
    private BigDecimal netTotal;
    private BigDecimal crossTotal;
    private Currency currency;
    private BigDecimal exchangeRate;
    private BigDecimal payableAmount;
    private LocalDate date;
    private DocStatus status;

    public String getId()
    {
        return id;
    }

    public void setId(final String id)
    {
        this.id = id;
    }

    public Currency getCurrency()
    {
        return currency;
    }

    public void setCurrency(final Currency currency)
    {
        this.currency = currency;
    }

    public BigDecimal getNetTotal()
    {
        return netTotal;
    }

    public void setNetTotal(final BigDecimal netTotal)
    {
        this.netTotal = netTotal;
    }

    public BigDecimal getCrossTotal()
    {
        return crossTotal;
    }

    public void setCrossTotal(final BigDecimal crossTotal)
    {
        this.crossTotal = crossTotal;
    }

    public String getNumber()
    {
        return number;
    }

    public void setNumber(final String number)
    {
        this.number = number;
    }

    public LocalDate getDate()
    {
        return date;
    }

    public void setDate(final LocalDate date)
    {
        this.date = date;
    }

    public DocStatus getStatus()
    {
        return status;
    }

    public void setStatus(final DocStatus status)
    {
        this.status = status;
    }


    public BigDecimal getExchangeRate()
    {
        return exchangeRate;
    }


    public void setExchangeRate(final BigDecimal exchangeRate)
    {
        this.exchangeRate = exchangeRate;
    }

    public BigDecimal getPayableAmount() {
      return payableAmount;
    }

    public void setPayableAmount(BigDecimal payableAmount) {
      this.payableAmount = payableAmount;
    }

}
