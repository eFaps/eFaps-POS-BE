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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.efaps.pos.dto.Permission;
import org.efaps.pos.dto.Roles;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Document(collection = "users")
public class User
    implements UserDetails
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String oid;

    private String username;

    private String password;

    private String employeeOid;

    private String firstName;

    private String surName;

    private boolean visible;

    private Collection<Roles> roles;

    private Collection<String> workspaceOids;

    private Collection<Permission> permissions;

    public String getOid()
    {
        return this.oid;
    }

    public User setOid(final String _oid)
    {
        this.oid = _oid;
        return this;
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        if (CollectionUtils.isEmpty(getPermissions()) && CollectionUtils.isNotEmpty(getRoles())) {
            return Arrays.asList(new SimpleGrantedAuthority(Permission.ADMIN.name()),
                            new SimpleGrantedAuthority(Permission.COLLECT.name()),
                            new SimpleGrantedAuthority(Permission.ORDER.name()),
                            new SimpleGrantedAuthority(Permission.REDEEM_CREDITNOTE.name()));
        }
        return getPermissions() == null ? Collections.emptyList()
                        : getPermissions().stream().map(permission -> new SimpleGrantedAuthority(permission.name()))
                                        .collect(Collectors.toSet());
    }

    @Override
    public String getPassword()
    {
        return this.password;
    }

    public User setPassword(final String _password)
    {
        this.password = _password;
        return this;
    }

    @Override
    public String getUsername()
    {
        return this.username;
    }

    public User setUsername(final String _username)
    {
        this.username = _username;
        this.id = this.username;
        return this;
    }

    @Override
    public boolean isAccountNonExpired()
    {
        return true;
    }

    @Override
    public boolean isAccountNonLocked()
    {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired()
    {
        return true;
    }

    @Override
    public boolean isEnabled()
    {
        return true;
    }

    public String getFirstName()
    {
        return this.firstName;
    }

    public String getEmployeeOid()
    {
        return employeeOid;
    }

    public User setEmployeeOid(String employeeOid)
    {
        this.employeeOid = employeeOid;
        return this;
    }

    public User setFirstName(final String _firstName)
    {
        this.firstName = _firstName;
        return this;
    }

    public String getSurName()
    {
        return this.surName;
    }

    public User setSurName(final String _surName)
    {
        this.surName = _surName;
        return this;
    }

    public boolean isVisible()
    {
        return this.visible;
    }

    public User setVisible(final boolean _visible)
    {
        this.visible = _visible;
        return this;
    }
    @Deprecated
    public Collection<Roles> getRoles()
    {
        return this.roles;
    }

    @Deprecated
    public User setRoles(final Collection<Roles> _roles)
    {
        this.roles = _roles;
        return this;
    }

    public Collection<String> getWorkspaceOids()
    {
        return this.workspaceOids;
    }

    public User setWorkspaceOids(final Collection<String> _workspaceOids)
    {
        this.workspaceOids = _workspaceOids;
        return this;
    }

    public Collection<Permission> getPermissions()
    {
        return this.permissions;
    }

    public User setPermissions(final Collection<Permission> permissions)
    {
        this.permissions = permissions;
        return this;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this);
    }
}
