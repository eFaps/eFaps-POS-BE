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

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class ConfigurationBOM
{

    private String oid;
    private String toProductOid;
    private String bomGroupOid;
    private int position;
    private BigDecimal quantity;
    private String uoM;
    private int flags;
    private List<BOMAction> actions;

    public String getOid()
    {
        return oid;
    }

    public ConfigurationBOM setOid(String oid)
    {
        this.oid = oid;
        return this;
    }

    public String getToProductOid()
    {
        return toProductOid;
    }

    public ConfigurationBOM setToProductOid(String toProductOid)
    {
        this.toProductOid = toProductOid;
        return this;
    }

    public String getBomGroupOid()
    {
        return bomGroupOid;
    }

    public ConfigurationBOM setBomGroupOid(String bomGroupOid)
    {
        this.bomGroupOid = bomGroupOid;
        return this;
    }

    public int getPosition()
    {
        return position;
    }

    public ConfigurationBOM setPosition(int position)
    {
        this.position = position;
        return this;
    }

    public BigDecimal getQuantity()
    {
        return quantity;
    }

    public ConfigurationBOM setQuantity(BigDecimal quantity)
    {
        this.quantity = quantity;
        return this;
    }

    public String getUoM()
    {
        return uoM;
    }

    public ConfigurationBOM setUoM(String uoM)
    {
        this.uoM = uoM;
        return this;
    }

    public int getFlags()
    {
        return flags;
    }

    public ConfigurationBOM setFlags(int flags)
    {
        this.flags = flags;
        return this;
    }

    public List<BOMAction> getActions()
    {
        return actions;
    }

    public ConfigurationBOM setActions(List<BOMAction> actions)
    {
        this.actions = actions;
        return this;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this);
    }
}
