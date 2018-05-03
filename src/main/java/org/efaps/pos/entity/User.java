package org.efaps.pos.entity;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.efaps.pos.dto.Roles;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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

    private String firstName;

    private String surName;

    private Set<Roles> roles;

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
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return getRoles() == null
                        ? Collections.emptyList()
                        : getRoles().stream()
                            .map(_role -> new SimpleGrantedAuthority(_role.name()))
                            .collect(Collectors.toSet());
    }

    @Override
    public String getPassword()
    {
        return this.password;
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

    public Set<Roles> getRoles()
    {
        return this.roles;
    }

    public void setRoles(final Set<Roles> _roles)
    {
        this.roles = _roles;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this);
    }
}
