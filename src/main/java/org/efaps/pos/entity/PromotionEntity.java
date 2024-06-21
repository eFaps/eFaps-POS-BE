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
package org.efaps.pos.entity;

import java.time.OffsetDateTime;
import java.util.List;

import org.efaps.promotionengine.action.IAction;
import org.efaps.promotionengine.condition.ICondition;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "promotions")
public class PromotionEntity
{

    @Id
    private String id;

    private String oid;

    private String name;

    private String description;

    private String label;


    private int priority;

    private OffsetDateTime startDateTime;

    private OffsetDateTime endDateTime;

    private List<ICondition> sourceConditions;

    private List<ICondition> targetConditions;

    private List<IAction> actions;

    public String getId()
    {
        return id;
    }

    public String getOid()
    {
        return oid;
    }

    public PromotionEntity setOid(String oid)
    {
        this.oid = oid;
        this.id = oid;
        return this;
    }

    public String getName()
    {
        return name;
    }

    public PromotionEntity setName(String name)
    {
        this.name = name;
        return this;
    }

    public String getDescription()
    {
        return description;
    }

    public PromotionEntity setDescription(String description)
    {
        this.description = description;
        return this;
    }

    public String getLabel()
    {
        return label;
    }

    public PromotionEntity setLabel(String label)
    {
        this.label = label;
        return this;
    }

    public int getPriority()
    {
        return priority;
    }

    public PromotionEntity setPriority(int priority)
    {
        this.priority = priority;
        return this;
    }

    public OffsetDateTime getStartDateTime()
    {
        return startDateTime;
    }

    public PromotionEntity setStartDateTime(OffsetDateTime startDateTime)
    {
        this.startDateTime = startDateTime;
        return this;
    }

    public OffsetDateTime getEndDateTime()
    {
        return endDateTime;
    }

    public PromotionEntity setEndDateTime(OffsetDateTime endDateTime)
    {
        this.endDateTime = endDateTime;
        return this;
    }

    public List<ICondition> getSourceConditions()
    {
        return sourceConditions;
    }

    public PromotionEntity setSourceConditions(List<ICondition> sourceConditions)
    {
        this.sourceConditions = sourceConditions;
        return this;
    }

    public List<ICondition> getTargetConditions()
    {
        return targetConditions;
    }

    public PromotionEntity setTargetConditions(List<ICondition> targetConditions)
    {
        this.targetConditions = targetConditions;
        return this;
    }

    public List<IAction> getActions()
    {
        return actions;
    }

    public PromotionEntity setActions(List<IAction> actions)
    {
        this.actions = actions;
        return this;
    }
}
