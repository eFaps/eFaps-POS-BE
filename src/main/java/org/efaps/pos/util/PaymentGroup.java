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
package org.efaps.pos.util;

import java.math.BigDecimal;

import org.efaps.pos.dto.PaymentInfoDto;
import org.efaps.pos.dto.PaymentType;

public class PaymentGroup
{

    private final PaymentType type;
    private String label;

    private PaymentGroup(final Builder _builder)
    {
        type = _builder.type;
        label = _builder.label == null ? "" : _builder.label;
    }

    public PaymentType getType()
    {
        return type;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(final String label)
    {
        this.label = label;
    }

    public PaymentInfoDto getInfo(final int _count, final BigDecimal _amount) {
        return PaymentInfoDto.builder()
                        .withCount(_count)
                        .withAmount(_amount)
                        .withLabel(label)
                        .withType(type)
                        .build();
    }

    @Override
    public boolean equals(final Object _obj)
    {
        boolean ret = false;
        if (_obj instanceof PaymentGroup) {
            final PaymentGroup other = (PaymentGroup) _obj;
            ret = other.type.equals(type) && other.label.equals(label);
        } else {
            ret = super.equals(_obj);
        }
        return ret;
    }

    @Override
    public int hashCode()
    {
        return type.hashCode() + label.hashCode();
    }


    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {

        private PaymentType type;
        private String label;

        public Builder withType(final PaymentType _type)
        {
            type = _type;
            return this;
        }

        public Builder withLabel(final String label)
        {
            this.label = label;
            return this;
        }

        public PaymentGroup build()
        {
            return new PaymentGroup(this);
        }
    }
}
