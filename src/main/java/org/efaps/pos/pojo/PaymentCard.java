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
package org.efaps.pos.pojo;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.efaps.pos.dto.PaymentType;

public class PaymentCard
    extends AbstractPayment
{

    private String cardTypeId;
    private String cardLabel;
    private String cardNumber;
    private String serviceProvider;
    private String authorization;
    private String operationId;

    public PaymentCard()
    {
        setType(PaymentType.CARD);
    }

    @Override
    public String getLabel()
    {
        return cardLabel;
    }

    public String getCardTypeId()
    {
        return cardTypeId;
    }

    public PaymentCard setCardTypeId(String cardTypeId)
    {
        this.cardTypeId = cardTypeId;
        return this;
    }

    public String getCardLabel()
    {
        return cardLabel;
    }

    public PaymentCard setCardLabel(String cardLabel)
    {
        this.cardLabel = cardLabel;
        return this;
    }

    public String getCardNumber()
    {
        return cardNumber;
    }

    public PaymentCard setCardNumber(String cardNumber)
    {
        this.cardNumber = cardNumber;
        return this;
    }

    public String getServiceProvider()
    {
        return serviceProvider;
    }

    public PaymentCard setServiceProvider(String serviceProvider)
    {
        this.serviceProvider = serviceProvider;
        return this;
    }

    public String getAuthorization()
    {
        return authorization;
    }

    public PaymentCard setAuthorization(String authorization)
    {
        this.authorization = authorization;
        return this;
    }

    public String getOperationId()
    {
        return operationId;
    }

    public PaymentCard setOperationId(String operationId)
    {
        this.operationId = operationId;
        return this;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this);
    }
}
