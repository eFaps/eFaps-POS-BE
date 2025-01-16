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

import org.efaps.pos.dto.PaymentType;

public class PaymentLoyaltyPoints
    extends AbstractPayment
{

    private String mappingKey;
    private String authorization;
    private String operationId;
    private Integer pointsAmount;

    public PaymentLoyaltyPoints()
    {
        setType(PaymentType.LOYALTY_POINTS);
    }

    public String getMappingKey()
    {
        return mappingKey;
    }

    public PaymentLoyaltyPoints setMappingKey(String mappingKey)
    {
        this.mappingKey = mappingKey;
        return this;
    }

    public String getAuthorization()
    {
        return authorization;
    }

    public PaymentLoyaltyPoints setAuthorization(String authorization)
    {
        this.authorization = authorization;
        return this;
    }

    public String getOperationId()
    {
        return operationId;
    }

    public PaymentLoyaltyPoints setOperationId(String operationId)
    {
        this.operationId = operationId;
        return this;
    }

    public Integer getPointsAmount()
    {
        return pointsAmount;
    }

    public PaymentLoyaltyPoints setPointsAmount(Integer pointsAmount)
    {
        this.pointsAmount = pointsAmount;
        return this;
    }
}
