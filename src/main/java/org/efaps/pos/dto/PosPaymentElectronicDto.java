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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = PosPaymentElectronicDto.Builder.class)
@JsonInclude(Include.NON_NULL)
public class PosPaymentElectronicDto
    extends PaymentElectronicAbstractDto
    implements IPosPaymentDto
{

    private final PaymentType type;
    private final String collectOrderId;

    protected PosPaymentElectronicDto(final Builder builder)
    {
        super(builder);
        this.type = builder.type;
        this.collectOrderId = builder.collectOrderId;
    }

    @Override
    public PaymentType getType()
    {
        return type;
    }

    @Override
    public String getCollectOrderId()
    {
        return collectOrderId;
    }

    @Override
    public String toString()
    {
        return new StringBuilder()
                        .append(super.toString())
                        .append(", type=").append(type)
                        .append("]").toString();
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
        extends PaymentElectronicAbstractDto.Builder<PosPaymentElectronicDto.Builder>
    {

        private final PaymentType type = PaymentType.ELECTRONIC;

        private String collectOrderId;

        public Builder withCollectOrderId(final String collectOrderId)
        {
            this.collectOrderId = collectOrderId;
            return this;
        }

        public Builder withType(PaymentType type)
        {
            return this;
        }

        public PosPaymentElectronicDto build()
        {
            return new PosPaymentElectronicDto(this);
        }
    }
}
