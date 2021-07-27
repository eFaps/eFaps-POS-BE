/*
 * Copyright 2003 - 2021 The eFaps Team
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

import org.efaps.pos.dto.ProductRelationType;

public class ProductRelation
{

    private String label;
    private String productOid;
    private ProductRelationType type;

    public String getLabel()
    {
        return label;
    }

    public ProductRelation setLabel(final String _label)
    {
        label = _label;
        return this;
    }

    public String getProductOid()
    {
        return productOid;
    }

    public ProductRelation setProductOid(final String _productOid)
    {
        productOid = _productOid;
        return this;
    }

    public ProductRelationType getType()
    {
        return type;
    }

    public ProductRelation setType(final ProductRelationType type)
    {
        this.type = type;
        return this;
    }
}
