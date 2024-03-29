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
package org.efaps.pos.service;

import java.util.HashSet;
import java.util.Set;

import org.efaps.pos.pojo.CollectorState;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService
{
    private final SimpMessagingTemplate template;

    public WebSocketService(final SimpMessagingTemplate _template) {
        template = _template;
    }

    private final Set<String> ordersEdited = new HashSet<>();

    public void addOrder(final String _orderId)
    {
        ordersEdited.add(_orderId);
    }

    public void removeOrder(final String _orderId)
    {
        ordersEdited.remove(_orderId);
    }

    public Set<String> getOrdersEdited()
    {
        return ordersEdited;
    }

    public void notifyCollectOrderState(final CollectorState _collectorState)
    {
        template.convertAndSend("/topic/collectOrder/" + _collectorState.getCollectOrderId(),
                        _collectorState.getState().name());
    }

}
