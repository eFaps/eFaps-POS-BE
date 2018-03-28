package org.efaps.pos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
public class ConfigPropertiesTest
{
    @Autowired
    private ConfigProperties config;

    @Test
    public void testProperties() {
       assertEquals("POS-Backend-Test", this.config.getName());
       assertEquals("http://my.sso.com/pe", this.config.getSso().getUrl());
       assertEquals(4, this.config.getSso().getPostValues().size());
    }
}
