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

import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.efaps.pos.dto.DocType;
import org.efaps.pos.dto.PosLayout;
import org.efaps.pos.dto.PrintTarget;
import org.efaps.pos.dto.SpotConfig;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "workspaces")
public class Workspace
{

    @Id
    private String id;

    private String oid;

    private String name;

    private String posOid;

    private Set<DocType> docTypes;

    private SpotConfig spotConfig;

    private String warehouseOid;

    private Set<PrintCmd> printCmds;

    private Integer spotCount;

    private PosLayout posLayout;

    private Set<Discount> discounts;

    public String getOid()
    {
        return oid;
    }

    public Workspace setOid(final String _oid)
    {
        oid = _oid;
        id = _oid;
        return this;
    }

    public String getName()
    {
        return name;
    }

    public Workspace setName(final String _name)
    {
        name = _name;
        return this;
    }

    public String getPosOid()
    {
        return posOid;
    }

    public Workspace setPosOid(final String _posOid)
    {
        posOid = _posOid;
        return this;
    }

    public Set<DocType> getDocTypes()
    {
        return docTypes;
    }

    public Workspace setDocTypes(final Set<DocType> _docTypes)
    {
        docTypes = _docTypes;
        return this;
    }

    public SpotConfig getSpotConfig()
    {
        return spotConfig;
    }

    public Workspace setSpotConfig(final SpotConfig _spotConfig)
    {
        spotConfig = _spotConfig;
        return this;
    }

    public Integer getSpotCount()
    {
        return spotCount;
    }

    public Workspace setSpotCount(final Integer _spotCount)
    {
        spotCount = _spotCount;
        return this;
    }

    public String getWarehouseOid()
    {
        return warehouseOid;
    }

    public Workspace setWarehouseOid(final String _warehouseOid)
    {
        warehouseOid = _warehouseOid;
        return this;
    }

    public Set<PrintCmd> getPrintCmds()
    {
        return printCmds == null ? Collections.emptySet() : printCmds;
    }

    public Workspace setPrintCmds(final Set<PrintCmd> _printCmds)
    {
        printCmds = _printCmds;
        return this;
    }

    public PosLayout getPosLayout()
    {
        return posLayout;
    }

    public Workspace setPosLayout(final PosLayout _posLayout)
    {
        posLayout = _posLayout;
        return this;
    }

    public Set<Discount> getDiscounts()
    {
        return discounts;
    }

    public Workspace setDiscounts(final Set<Discount> discounts)
    {
        this.discounts = discounts;
        return this;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this);
    }

    public static class PrintCmd
    {

        private String printerOid;
        private PrintTarget target;
        private String targetOid;
        private String reportOid;

        public String getPrinterOid()
        {
            return printerOid;
        }

        public PrintCmd setPrinterOid(final String _printerOid)
        {
            printerOid = _printerOid;
            return this;
        }

        public PrintTarget getTarget()
        {
            return target;
        }

        public PrintCmd setTarget(final PrintTarget _target)
        {
            target = _target;
            return this;
        }

        public String getTargetOid()
        {
            return targetOid;
        }

        public PrintCmd setTargetOid(final String _targetOid)
        {
            targetOid = _targetOid;
            return this;
        }

        public String getReportOid()
        {
            return reportOid;
        }

        public PrintCmd setReportOid(final String _reportOid)
        {
            reportOid = _reportOid;
            return this;
        }
    }
}
