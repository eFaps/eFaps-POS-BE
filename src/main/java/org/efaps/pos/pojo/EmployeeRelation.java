package org.efaps.pos.pojo;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.efaps.pos.dto.EmployeeRelationType;

public class EmployeeRelation
{

    private EmployeeRelationType type;

    private String employeeOid;

    public EmployeeRelationType getType()
    {
        return type;
    }

    public EmployeeRelation setType(EmployeeRelationType type)
    {
        this.type = type;
        return this;
    }

    public String getEmployeeOid()
    {
        return employeeOid;
    }

    public EmployeeRelation setEmployeeOid(String employeeOid)
    {
        this.employeeOid = employeeOid;
        return this;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this);
    }
}
