package org.efaps.pos.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = PosStocktakingDto.Builder.class)
public class PosStocktakingDto
{

    private final String id;
    private final String number;
    private final String userOid;
    private final LocalDateTime startAt;
    private final LocalDateTime endAt;
    private final StocktakingStatus status;
    private final String warehouseOid;
    private final WarehouseDto warehouse;
    private final PosUserDto user;

    private PosStocktakingDto(Builder builder)
    {
        this.id = builder.id;
        this.number = builder.number;
        this.userOid = builder.userOid;
        this.startAt = builder.startAt;
        this.endAt = builder.endAt;
        this.status = builder.status;
        this.warehouseOid = builder.warehouseOid;
        this.warehouse = builder.warehouse;
        this.user = builder.user;
    }

    public String getId()
    {
        return id;
    }

    public String getNumber()
    {
        return number;
    }

    public String getUserOid()
    {
        return userOid;
    }

    public LocalDateTime getStartAt()
    {
        return startAt;
    }

    public LocalDateTime getEndAt()
    {
        return endAt;
    }

    public StocktakingStatus getStatus()
    {
        return status;
    }

    public String getWarehouseOid()
    {
        return warehouseOid;
    }

    public WarehouseDto getWarehouse()
    {
        return warehouse;
    }

    public PosUserDto getUser()
    {
        return user;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private String id;
        private String number;
        private String userOid;
        private LocalDateTime startAt;
        private LocalDateTime endAt;
        private StocktakingStatus status;
        private String warehouseOid;
        private WarehouseDto warehouse;
        private PosUserDto user;

        private Builder()
        {
        }

        public Builder withId(String id)
        {
            this.id = id;
            return this;
        }

        public Builder withNumber(String number)
        {
            this.number = number;
            return this;
        }

        public Builder withUserOid(String userOid)
        {
            this.userOid = userOid;
            return this;
        }

        public Builder withStartAt(LocalDateTime startAt)
        {
            this.startAt = startAt;
            return this;
        }

        public Builder withEndAt(LocalDateTime endAt)
        {
            this.endAt = endAt;
            return this;
        }

        public Builder withStatus(StocktakingStatus status)
        {
            this.status = status;
            return this;
        }

        public Builder withWarehouseOid(String warehouseOid)
        {
            this.warehouseOid = warehouseOid;
            return this;
        }

        public Builder withWarehouse(WarehouseDto warehouse)
        {
            this.warehouse = warehouse;
            return this;
        }

        public Builder withUser(PosUserDto user)
        {
            this.user = user;
            return this;
        }

        public PosStocktakingDto build()
        {
            return new PosStocktakingDto(this);
        }
    }

}
