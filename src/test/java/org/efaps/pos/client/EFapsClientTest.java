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
package org.efaps.pos.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.efaps.pos.dto.ProductDto;
import org.efaps.pos.entity.Identifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

//@ExtendWith(SpringExtension.class)
//@SpringBootTest
//@AutoConfigureMockMvc
//@AutoConfigureMockRestServiceServer
@ActiveProfiles(profiles = "test")
public class EFapsClientTest
{
    //@Autowired
    private EFapsClient client;

    //@Autowired
    private ObjectMapper mapper;

    //@Autowired
    private MongoTemplate mongoTemplate;

    MockRestServiceServer server;

    //@BeforeEach
    public void setup() {
        final RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(client, "restTemplate");

        server = MockRestServiceServer.bindTo(restTemplate).build();

        mongoTemplate.save(new Identifier()
                        .setId(Identifier.KEY)
                        .setCreated(LocalDateTime.now())
                        .setIdentifier("TESTIDENT"));
    }

    //@Test
    public void testGetProducts() throws JsonProcessingException {
        mongoTemplate.save(new Identifier()
                                        .setId(Identifier.KEY)
                                        .setCreated(LocalDateTime.now())
                                        .setIdentifier("TESTIDENT"));

        final List<ProductDto> products = Collections.singletonList(ProductDto.builder().build());

        server.expect(requestTo("http://localhost:8888/eFaps/servlet/rest/pos/TESTIDENT/products"))
            .andRespond(withSuccess(mapper.writeValueAsString(products), MediaType.APPLICATION_JSON));

        final List<ProductDto> response = client.getProducts();

        assertEquals(1, response.size());
        server.verify();
    }
}
