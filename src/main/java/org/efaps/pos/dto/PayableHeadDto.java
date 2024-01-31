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

public class PayableHeadDto
    extends DocumentHeadDto
{

    private final DocumentHeadDto order;

    public PayableHeadDto(final Builder _builder)
    {
        super(_builder);
        order = _builder.order;
    }

    public DocumentHeadDto getOrder()
    {
        return order;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
        extends DocumentHeadDto.Builder<Builder, PayableHeadDto>
    {

        private DocumentHeadDto order;

        public Builder withOrder(final DocumentHeadDto _order) {
            order = _order;
            return this;
        }

        @Override
        public PayableHeadDto build()
        {
            return new PayableHeadDto(this);
        }
    }
}
