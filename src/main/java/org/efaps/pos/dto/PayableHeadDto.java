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
