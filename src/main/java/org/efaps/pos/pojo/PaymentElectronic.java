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

public class PaymentElectronic
    extends AbstractPayment
{

    private String mappingKey;
    private String cardLabel;
    private String serviceProvider;
    private String authorization;
    private String operationId;
    private String cardNumber;
    private String equipmentIdent;

    public PaymentElectronic()
    {
        setType(PaymentType.ELECTRONIC);
    }

    public String getMappingKey()
    {
        return mappingKey;
    }

    public PaymentElectronic setMappingKey(String mappingKey)
    {
        this.mappingKey = mappingKey;
        return this;
    }

    public String getCardLabel()
    {
        return cardLabel;
    }

    public PaymentElectronic setCardLabel(String cardLabel)
    {
        this.cardLabel = cardLabel;
        return this;
    }

    public String getServiceProvider()
    {
        return serviceProvider;
    }

    public PaymentElectronic setServiceProvider(String serviceProvider)
    {
        this.serviceProvider = serviceProvider;
        return this;
    }

    public String getAuthorization()
    {
        return authorization;
    }

    public PaymentElectronic setAuthorization(String authorization)
    {
        this.authorization = authorization;
        return this;
    }

    public String getOperationId()
    {
        return operationId;
    }

    public PaymentElectronic setOperationId(String operationId)
    {
        this.operationId = operationId;
        return this;
    }

    public String getCardNumber()
    {
        return cardNumber;
    }

    public PaymentElectronic setCardNumber(String cardNumber)
    {
        this.cardNumber = cardNumber;
        return this;
    }

    public String getEquipmentIdent()
    {
        return equipmentIdent;
    }

    public PaymentElectronic setEquipmentIdent(String equipmentIdent)
    {
        this.equipmentIdent = equipmentIdent;
        return this;
    }

    @Override
    public String getLabel()
    {
        return cardLabel;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this);
    }
}
