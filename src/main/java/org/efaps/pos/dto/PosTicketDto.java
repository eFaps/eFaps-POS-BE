/*
 * Copyright 2003 - 2018 The eFaps Team
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
package org.efaps.pos.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Set;
import java.util.stream.Collectors;

import org.efaps.pos.interfaces.ITicket;
import org.efaps.pos.interfaces.ITicketItem;

@JsonDeserialize(builder = PosTicketDto.Builder.class)
public class PosTicketDto
    extends AbstractPayableDocumentDto
    implements ITicket
{

    public PosTicketDto(final Builder _builder)
    {
        super(_builder);
    }

    @Override
    public Set<ITicketItem> getTicketItems()
    {
        return getItems().stream().map(item -> (ITicketItem) item).collect(Collectors.toSet());
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractPayableDocumentDto.Builder<Builder, PosTicketDto>
    {

        public Builder withItems(final Set<PosDocItemDto> _items)
        {
            setItems(_items);
            return this;
        }

        public Builder withPayments(final Set<PaymentDto> _payments)
        {
            setPayments(_payments);
            return this;
        }

        @Override
        public PosTicketDto build()
        {
            return new PosTicketDto(this);
        }
    }
}