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
package org.efaps.pos.service;

import java.util.List;
import java.util.stream.Collectors;

import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.entity.SyncInfo;
import org.efaps.pos.entity.User;
import org.efaps.pos.repository.UserRepository;
import org.efaps.pos.util.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService
    implements UserDetailsService
{

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final EFapsClient eFapsClient;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(final EFapsClient eFapsClient,
                       final UserRepository userRepository,
                       final PasswordEncoder passwordEncoder)
    {
        this.eFapsClient = eFapsClient;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(final String _username)
        throws UsernameNotFoundException
    {
        return userRepository.findById(_username).orElseThrow(() -> new UsernameNotFoundException(_username));
    }

    public PasswordEncoder getPasswordEncoder()
    {
        return passwordEncoder;
    }

    public List<User> getUsers()
    {
        return userRepository.findUserByVisibleIsTrue();
    }

    public User getUserByOid(final String userOid)
    {
        return userRepository.findOneByOid(userOid).orElse(null);
    }

    public boolean syncUsers(final SyncInfo syncInfo)
    {
        LOG.info("Syncing Users");
        final var response = eFapsClient.getUsers();
        boolean ret;
        if (response == null) {
            ret = false;
            LOG.warn("Syncing of Users failed!");
        } else {
            final List<User> users = response.stream()
                            .map(Converter::toEntity)
                            .collect(Collectors.toList());
            users.forEach(user -> userRepository.save(user));

            final var allUsers = userRepository.findAll();
            allUsers.forEach(existingUser -> {
                if (users.stream().noneMatch(au -> au.getOid().equals(existingUser.getOid()))) {
                    userRepository.deleteById(existingUser.getId());
                }
            });
            ret = true;
        }
        return ret;
    }
}
