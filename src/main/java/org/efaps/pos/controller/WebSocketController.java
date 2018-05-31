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

package org.efaps.pos.controller;

import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController
{

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketController(final SimpMessagingTemplate _messagingTemplate) {
        this.messagingTemplate = _messagingTemplate;
    }

    @MessageMapping("/orders/start.edit")
    public String processOrderStartEdit(@Payload final String _message)
        throws Exception
    {
        System.out.println(_message);
        return "Hallo";
    }

    @MessageMapping("/orders/finish.edit")
    public String processOrderFinishEdit(@Payload final String _message)
        throws Exception
    {
        System.out.println(_message);
        return "Finish";
    }

    @MessageExceptionHandler
    public String handleException(final Throwable _exception)
    {
        this.messagingTemplate.convertAndSend("/errors", _exception.getMessage());
        return _exception.getMessage();
    }
}
