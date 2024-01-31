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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.efaps.pos.entity.CollectOrder;

public class CollectorState
{

    private final String collectOrderId;
    private CollectOrder.State state;

    public CollectorState(final String _collectOrderId)
    {
        collectOrderId = _collectOrderId;
    }

    public String getCollectOrderId()
    {
        return collectOrderId;
    }

    public synchronized void setState(final CollectOrder.State _state)
    {
        state = _state;
    }

    public synchronized CollectOrder.State getState()
    {
        return state;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
