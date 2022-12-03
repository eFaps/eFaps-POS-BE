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
package org.efaps.pos;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.MongoClients;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import jakarta.annotation.PreDestroy;

@Configuration
public class EmbeddedMongoTest {

    private MongodExecutable mongodExe;
    private MongodProcess mongod;

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        final MongodStarter starter = MongodStarter.getDefaultInstance();
        final String bindIp = "localhost";
        final int port = RandomUtils.nextInt(27020, 28000);
        final MongodConfig mongodConfig = MongodConfig.builder()
            .version(Version.Main.V4_4)
            .net(new Net(bindIp, port, Network.localhostIsIPv6()))
            .build();
        this.mongodExe = starter.prepare(mongodConfig);
        this.mongod = mongodExe.start();
        return new MongoTemplate(MongoClients.create("mongodb://localhost:" + port), "test");
    }

    @PreDestroy
    public void destroyMongo() throws Exception {
        if (this.mongod != null) {
            this.mongod.stop();
            this.mongodExe.stop();
        }
    }
}

