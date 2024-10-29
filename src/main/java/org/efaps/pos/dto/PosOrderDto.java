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

import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = PosOrderDto.Builder.class)
public class PosOrderDto
    extends AbstractDocumentDto
{

    private final PosSpotDto spot;

    private final DiscountDto discount;

    private final String payableOid;

    private final String shoutout;

    private final String orderOptionKey;

    public PosOrderDto(final Builder builder)
    {
        super(builder);
        this.spot = builder.spot;
        this.discount = builder.discount;
        this.payableOid = builder.payableOid;
        this.shoutout = builder.shoutout;
        this.orderOptionKey = builder.orderOptionKey;
    }

    public PosSpotDto getSpot()
    {
        return spot;
    }

    public DiscountDto getDiscount()
    {
        return discount;
    }

    public String getPayableOid()
    {
        return payableOid;
    }

    public String getShoutout()
    {
        return shoutout;
    }

    public String getOrderOptionKey()
    {
        return orderOptionKey;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractDocumentDto.Builder<Builder, PosOrderDto>
    {

        private PosSpotDto spot;
        private DiscountDto discount;
        private String payableOid;
        private String shoutout;
        private String orderOptionKey;

        public Builder withSpot(final PosSpotDto _spot)
        {
            spot = _spot;
            return this;
        }

        public Builder withDiscount(final DiscountDto _discount)
        {
            discount = _discount;
            return this;
        }

        public Builder withPayableOid(final String _payableOid)
        {
            payableOid = _payableOid;
            return this;
        }

        public Builder withItems(final Set<PosDocItemDto> _items)
        {
            setItems(_items);
            return this;
        }

        public Builder withShoutout(final String _shoutout)
        {
            shoutout = _shoutout;
            return this;
        }

        public Builder withOrderOptionKey(final String orderOptionKey)
        {
            this.orderOptionKey = orderOptionKey;
            return this;
        }

        @Override
        public PosOrderDto build()
        {
            return new PosOrderDto(this);
        }
    }
}
