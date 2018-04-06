/*
 * Copyright 2003 - 2018 The eFaps Team
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
package org.efaps.pos.service;

import java.util.List;

import org.efaps.pos.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService
    implements UserDetailsService
{
    private final MongoTemplate mongoTemplate;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(final MongoTemplate _mongoTemplate,
                       final PasswordEncoder _passwordEncoder)
    {
        this.mongoTemplate = _mongoTemplate;
        this.passwordEncoder = _passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(final String _username)
        throws UsernameNotFoundException
    {
        final UserDetails user = this.mongoTemplate.findById(_username, User.class);
        if (user == null) {
            throw new UsernameNotFoundException(_username);
        }
        return user;
    }

    public PasswordEncoder getPasswordEncoder()
    {
        return this.passwordEncoder;
    }

    public List<User> getUsers() {
        return this.mongoTemplate.find(new Query(), User.class);
    }
}
