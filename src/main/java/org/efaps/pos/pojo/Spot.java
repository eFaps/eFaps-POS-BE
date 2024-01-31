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

public class Spot
{

    private String id;

    private String label;

    public String getId()
    {
        return this.id;
    }

    public Spot setId(final String _id)
    {
        this.id = _id;
        return this;
    }

    public String getLabel()
    {
        return this.label;
    }

    public Spot setLabel(final String _label)
    {
        this.label = _label;
        return this;
    }
}
