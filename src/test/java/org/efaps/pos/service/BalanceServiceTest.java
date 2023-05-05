/*
 * Copyright 2003 - 2022 The eFaps Team
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.efaps.pos.dto.BalanceStatus;
import org.efaps.pos.entity.Balance;
import org.efaps.pos.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@AutoConfigureMockRestServiceServer
@ActiveProfiles(profiles = "test")
@AutoConfigureDataMongo
@SpringBootTest
public class BalanceServiceTest
{

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private BalanceService balanceService;

    @BeforeEach
    public void setup()
    {
        mongoTemplate.remove(new Query(), Balance.class);
    }

    @Test
    public void testGetCurrentNone()
    {
        final User user = new User();
        final Optional<Balance> balanceOpt = balanceService.getCurrent(user, false);
        assertFalse(balanceOpt.isPresent());
    }

    @Test
    public void testGetCurrentCreateNew()
    {
        final User user = new User();
        user.setOid("123.45");
        final Optional<Balance> balanceOpt = balanceService.getCurrent(user, true);
        assertTrue(balanceOpt.isPresent());
        final Balance balance = balanceOpt.get();
        assertEquals(BalanceStatus.OPEN, balance.getStatus());
        assertEquals(user.getOid(), balance.getUserOid());
        assertNotNull(balance.getNumber());
    }

    @Test
    public void testUpdateNotFound()
    {
        final Balance balance = new Balance();
        balance.setId("1234");
        final Balance ret = balanceService.update(balance);
        assertEquals(balance, ret);
    }

    @Test
    public void testUpdate()
    {
        final Balance balance = new Balance();
        mongoTemplate.save(balance);

        final Balance ret = balanceService.update(balance);
        assertEquals(balance.getId(), ret.getId());
        assertEquals(BalanceStatus.CLOSED, ret.getStatus());
        assertFalse(ret.isSynced());
        assertNotNull(ret.getEndAt());
    }
}
