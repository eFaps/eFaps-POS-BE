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
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureMockRestServiceServer
@ActiveProfiles(profiles = "test")
public class WorkspaceServiceTest
{
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private WorkspaceService workspaceService;

    @BeforeEach
    public void setup()
    {
        this.mongoTemplate.remove(new Query(), Workspace.class);
    }

    @Test
    public void testGetWorkspaces()
    {
        this.mongoTemplate.save(new Workspace().setOid("1.1"));
        this.mongoTemplate.save(new Workspace().setOid("1.2"));
        this.mongoTemplate.save(new Workspace().setOid("1.3"));
        final User user = new User().setWorkspaceOids(Collections.singleton("1.1"));
        final List<Workspace> workspaces = this.workspaceService.getWorkspaces(user);
        assertEquals(1, workspaces.size());
        assertEquals("1.1", workspaces.get(0).getOid());
    }

    @Test
    public void testGetWorkspace4User()
    {
        this.mongoTemplate.save(new Workspace().setOid("1.1"));
        this.mongoTemplate.save(new Workspace().setOid("1.2"));
        this.mongoTemplate.save(new Workspace().setOid("1.3"));
        final User user = new User().setWorkspaceOids(Collections.singleton("1.1"));
        final Workspace workspaces = this.workspaceService.getWorkspace(user, "1.1");
        assertEquals("1.1", workspaces.getOid());
    }

    @Test
    public void testGetWorkspace4UserNoAccess()
    {
        this.mongoTemplate.save(new Workspace().setOid("1.1"));
        this.mongoTemplate.save(new Workspace().setOid("1.2"));
        this.mongoTemplate.save(new Workspace().setOid("1.3"));
        final User user = new User().setWorkspaceOids(Collections.singleton("1.4"));
        final Workspace workspaces = this.workspaceService.getWorkspace(user, "1.1");
        assertNull(workspaces);
    }

    @Test
    public void testGetWorkspace()
    {
        this.mongoTemplate.save(new Workspace().setOid("1.1"));
        this.mongoTemplate.save(new Workspace().setOid("1.2"));
        this.mongoTemplate.save(new Workspace().setOid("1.3"));
        final Workspace workspaces = this.workspaceService.getWorkspace("1.2");
        assertEquals("1.2", workspaces.getOid());
    }
}
