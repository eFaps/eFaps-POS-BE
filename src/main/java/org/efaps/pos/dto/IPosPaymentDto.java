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
package org.efaps.pos.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
                @JsonSubTypes.Type(value = PosPaymentCardDto.class, name = "CARD"),
                @JsonSubTypes.Type(value = PosPaymentCashDto.class, name = "CASH"),
                @JsonSubTypes.Type(value = PosPaymentChangeDto.class, name = "CHANGE"),
                @JsonSubTypes.Type(value = PosPaymentElectronicDto.class, name = "ELECTRONIC"),
                @JsonSubTypes.Type(value = PosPaymentFreeDto.class, name = "FREE"),
                @JsonSubTypes.Type(value = PosPaymentLoyaltyPointsDto.class, name = "LOYALTY_POINTS"),
                @JsonSubTypes.Type(value = PosPaymentRedeemCreditNoteDto.class, name = "REDEEM_CREDITNOTE"),
})
public interface IPosPaymentDto
    extends IPaymentDto
{
    String getCollectOrderId();

    BigDecimal getAmount();
}
