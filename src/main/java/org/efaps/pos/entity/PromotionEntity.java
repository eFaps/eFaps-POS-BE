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
