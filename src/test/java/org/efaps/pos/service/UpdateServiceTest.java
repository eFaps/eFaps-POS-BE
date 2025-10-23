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

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.io.IOUtils;
import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.dto.UpdateDto;
import org.efaps.pos.dto.UpdateInstructionDto;
import org.efaps.pos.entity.Identifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;

@ExtendWith(SpringExtension.class)
@ActiveProfiles(profiles = { "test" })
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureDataMongo
public class UpdateServiceTest
{

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UpdateService updateService;
    @Autowired
    private EFapsClient eFapsClient;

    private MockRestServiceServer server;


    @BeforeEach
    void init()
    {
        server = MockRestServiceServer.createServer(eFapsClient.getRestTemplate());
        mongoTemplate.save(new Identifier()
                        .setId(Identifier.KEY)
                        .setCreated(LocalDateTime.now())
                        .setIdentifier("FAKEIDENT"));
    }

    @Test
    public void testCreateStructure()
        throws IOException, ArchiveException
    {

        final var apple = IOUtils.toByteArray(getClass().getClassLoader().getResourceAsStream("apple.jpeg"));
        final var orange = IOUtils.toByteArray(getClass().getClassLoader().getResourceAsStream("orange.jpeg"));
        final var spa = IOUtils.toByteArray(getClass().getClassLoader().getResourceAsStream("spa.zip"));

        server.expect(requestTo("http://localhost:8888/eFaps/api/checkout?oid=1.1&ident=FAKEIDENT"))
                        .andRespond(MockRestResponseCreators
                                        .withSuccess(apple, MediaType.IMAGE_JPEG)
                                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                                        ContentDisposition.attachment().filename("apple.jpeg").build()
                                                                        .toString()));

        server.expect(requestTo("http://localhost:8888/eFaps/api/checkout?oid=1.2&ident=FAKEIDENT"))
                        .andRespond(MockRestResponseCreators
                                        .withSuccess(orange, MediaType.IMAGE_JPEG)
                                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                                        ContentDisposition.attachment().filename("orange.jpeg").build()
                                                                        .toString()));

        server.expect(requestTo("http://localhost:8888/eFaps/api/checkout?oid=1.3&ident=FAKEIDENT"))
        .andRespond(MockRestResponseCreators
                        .withSuccess(spa, MediaType.valueOf("application/zip"))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                        ContentDisposition.attachment().filename("spa.zip").build()
                                                        .toString()));

        updateService.adhereInstructions(UpdateDto.builder()
                        .withVersion("26.3.5")
                        .withInstructions(Arrays.asList(
                                        UpdateInstructionDto.builder()
                                                        .withFileOid("1.1")
                                                        .withTargetPath(".")
                                                        .build(),
                                        UpdateInstructionDto.builder()
                                                        .withFileOid("1.2")
                                                        .withTargetPath("./extension")
                                                        .build(),
                                        UpdateInstructionDto.builder()
                                                        .withFileOid("1.3")
                                                        .withTargetPath("./static")
                                                        .withExpand(true)
                                                        .build()))
                        .build());
    }
}
