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
package org.efaps.pos.sso;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.efaps.pos.ConfigProperties;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@ExtendWith(SpringExtension.class)
//@SpringBootTest(properties = { "sso.url=http://my.sso.com/pe",
//                "sso.client_id: ${random.value}",
//                "sso.client_secret: ${random.value}",
//                "sso.username: ${random.value}",
//                "sso.password: ${random.value}"})
//@AutoConfigureMockMvc
//@AutoConfigureMockRestServiceServer
//@RestClientTest
@ActiveProfiles(profiles = "test")
public class SSOClientTest
{
    //@Autowired
    private ConfigProperties config;

    //@Autowired
    private SSOClient client;

    //@Autowired
    private MockRestServiceServer server;

    //@Test
    public void testClient() {
        final MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.set("client_id", config.getSso().getClientId());
        map.set("client_secret", config.getSso().getClientSecret());
        map.set("grant_type", "password");
        map.set("username", config.getSso().getUsername());
        map.set("password", config.getSso().getPassword());

        server.expect(requestTo("http://my.sso.com/pe"))
            .andExpect(content().formData(map))
            .andRespond(withSuccess("{ \"access_token\": \"sometokenstuff\", \"expires_in\": 100 , "
                            + "\"refresh_expires_in\": 1800 }",
                            MediaType.APPLICATION_JSON));
        client.login();
        server.verify();
    }
}
