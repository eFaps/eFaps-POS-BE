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
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Collections;
import java.util.List;

import org.efaps.pos.entity.User;
import org.efaps.pos.entity.Workspace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@AutoConfigureMockRestServiceServer
@ActiveProfiles(profiles = "test")
@DataMongoTest
public class WorkspaceServiceTest
{
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private WorkspaceService workspaceService;

    @BeforeEach
    public void setup()
    {
        mongoTemplate.remove(new Query(), Workspace.class);
    }

    @Test
    public void testGetWorkspaces()
    {
        mongoTemplate.save(new Workspace().setOid("1.1"));
        mongoTemplate.save(new Workspace().setOid("1.2"));
        mongoTemplate.save(new Workspace().setOid("1.3"));
        final User user = new User().setWorkspaceOids(Collections.singleton("1.1"));
        final List<Workspace> workspaces = workspaceService.getWorkspaces(user);
        assertEquals(1, workspaces.size());
        assertEquals("1.1", workspaces.get(0).getOid());
    }

    @Test
    public void testGetWorkspace4User()
    {
        mongoTemplate.save(new Workspace().setOid("1.1"));
        mongoTemplate.save(new Workspace().setOid("1.2"));
        mongoTemplate.save(new Workspace().setOid("1.3"));
        final User user = new User().setWorkspaceOids(Collections.singleton("1.1"));
        final Workspace workspaces = workspaceService.getWorkspace(user, "1.1");
        assertEquals("1.1", workspaces.getOid());
    }

    @Test
    public void testGetWorkspace4UserNoAccess()
    {
        mongoTemplate.save(new Workspace().setOid("1.1"));
        mongoTemplate.save(new Workspace().setOid("1.2"));
        mongoTemplate.save(new Workspace().setOid("1.3"));
        final User user = new User().setWorkspaceOids(Collections.singleton("1.4"));
        final Workspace workspaces = workspaceService.getWorkspace(user, "1.1");
        assertNull(workspaces);
    }

    @Test
    public void testGetWorkspace()
    {
        mongoTemplate.save(new Workspace().setOid("1.1"));
        mongoTemplate.save(new Workspace().setOid("1.2"));
        mongoTemplate.save(new Workspace().setOid("1.3"));
        final Workspace workspaces = workspaceService.getWorkspace("1.2");
        assertEquals("1.2", workspaces.getOid());
    }
}
