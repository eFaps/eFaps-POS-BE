/*
 * Copyright 2003 - 2019 The eFaps Team
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

package org.efaps.pos.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "stash")
public class SyncInfo
{

    @Id
    private String id;
    private LocalDateTime lastSync;

    public String getId()
    {
        return this.id;
    }

    public void setId(final String _id)
    {
        this.id = _id;
    }

    public LocalDateTime getLastSync()
    {
        return this.lastSync;
    }

    public void setLastSync(final LocalDateTime _lastSync)
    {
        this.lastSync = _lastSync;
    }
}
