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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.efaps.pos.dto.PrintTarget;
import org.efaps.pos.entity.AbstractDocument.Item;
import org.efaps.pos.entity.Category;
import org.efaps.pos.entity.Job;
import org.efaps.pos.entity.Order;
import org.efaps.pos.entity.Printer;
import org.efaps.pos.entity.Product;
import org.efaps.pos.entity.Workspace;
import org.efaps.pos.entity.Workspace.PrintCmd;
import org.efaps.pos.pojo.Product2Category;
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
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureMockRestServiceServer
@ActiveProfiles(profiles = "test")
@AutoConfigureDataMongo
public class JobServiceTest
{

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private JobService jobService;

    @BeforeEach
    public void setup()
    {
        mongoTemplate.remove(new Query(), Job.class);
        mongoTemplate.remove(new Query(), Category.class);
        mongoTemplate.remove(new Query(), Printer.class);
        mongoTemplate.remove(new Query(), Product.class);
    }

    @Test
    public void testCreateJobsNoItems()
    {
        final Collection<Job> jobs = jobService.createJobs(null, new Order().setItems(Collections
                        .emptySet()));
        assertTrue(jobs.isEmpty());
    }

    @Test
    public void testCreateJobsProductDoesNotExist()
    {
        final Set<Item> items = new HashSet<>();
        items.add(new Item().setProductOid("123.456"));
        final Order order = new Order().setItems(items);

        final Collection<Job> jobs = jobService.createJobs(null, order);
        assertTrue(jobs.isEmpty());
    }

    @Test
    public void testCreateJobsProductNoCategories()
    {
        mongoTemplate.save(new Product().setOid("productOid"));

        final Set<Item> items = new HashSet<>();
        items.add(new Item().setProductOid("productOid"));
        final Order order = new Order().setItems(items);

        final Collection<Job> jobs = jobService.createJobs(null, order);
        assertTrue(jobs.isEmpty());
    }

    @Test
    public void testCreateJobsCategoryDoesNotExist()
    {
        mongoTemplate.save(new Product().setOid("productOid").setCategories(Collections.singleton(
                        new Product2Category().setCategoryOid("categoryOid"))));

        final Set<Item> items = new HashSet<>();
        items.add(new Item().setProductOid("productOid"));
        final Order order = new Order().setItems(items);

        final Collection<Job> jobs = jobService.createJobs(null, order);
        assertTrue(jobs.isEmpty());
    }

    @Test
    public void testCreateJobsCategoryNoPrinter()
    {
        mongoTemplate.save(new Category().setOid("categoryOid"));
        mongoTemplate.save(new Product().setOid("productOid").setCategories(Collections.singleton(
                        new Product2Category().setCategoryOid("categoryOid"))));

        final Workspace workspace = new Workspace();
        final Set<Item> items = new HashSet<>();
        items.add(new Item().setProductOid("productOid"));
        final Order order = new Order().setItems(items);

        final Collection<Job> jobs = jobService.createJobs(workspace, order);
        assertTrue(jobs.isEmpty());
    }

    @Test
    public void testCreateJobsNpPrintCmds()
    {
        mongoTemplate.save(new Category().setOid("categoryOid"));
        mongoTemplate.save(new Product().setOid("productOid").setCategories(Collections.singleton(
                        new Product2Category().setCategoryOid("categoryOid"))));

        final Workspace workspace = new Workspace();
        final Set<Item> items = new HashSet<>();
        items.add(new Item().setProductOid("productOid"));
        final Order order = new Order().setItems(items);

        final Collection<Job> jobs = jobService.createJobs(workspace, order);
        assertTrue(jobs.isEmpty());
    }

    @Test
    public void testCreateJob()
    {
        mongoTemplate.save(new Printer().setOid("printerOid"));
        mongoTemplate.save(new Category().setOid("categoryOid"));
        mongoTemplate.save(new Product().setOid("productOid").setCategories(Collections.singleton(
                        new Product2Category().setCategoryOid("categoryOid"))));

        final Workspace workspace = new Workspace();
        final Set<PrintCmd> printCmds = new HashSet<>();
        printCmds.add(new PrintCmd()
                        .setPrinterOid("printerOid")
                        .setTarget(PrintTarget.JOB)
                        .setTargetOid("categoryOid"));
        workspace.setPrintCmds(printCmds);
        final Set<Item> items = new HashSet<>();
        items.add(new Item().setProductOid("productOid"));
        final Order order = new Order().setItems(items);

        final Collection<Job> jobs = jobService.createJobs(workspace, order);
        assertEquals(1, jobs.size());
        assertNotNull(jobs.iterator().next().getId());
        assertEquals(1, mongoTemplate.findAll(Job.class).size());
    }

    @Test
    public void testCreateJobs()
    {
        mongoTemplate.save(new Printer().setOid("printerOid1"));
        mongoTemplate.save(new Printer().setOid("printerOid2"));
        mongoTemplate.save(new Category().setOid("categoryOid1"));
        mongoTemplate.save(new Category().setOid("categoryOid2"));
        mongoTemplate.save(new Category().setOid("categoryOid3"));

        mongoTemplate.save(new Product().setOid("productOid1").setCategories(Collections.singleton(
                        new Product2Category().setCategoryOid("categoryOid1"))));
        mongoTemplate.save(new Product().setOid("productOid2").setCategories(Collections.singleton(
                        new Product2Category().setCategoryOid("categoryOid2"))));
        mongoTemplate.save(new Product().setOid("productOid3").setCategories(Collections.singleton(
                        new Product2Category().setCategoryOid("categoryOid3"))));
        mongoTemplate.save(new Product().setOid("productOid4").setCategories(Collections.singleton(
                        new Product2Category().setCategoryOid("categoryOid1"))));
        mongoTemplate.save(new Product().setOid("productOid5").setCategories(Collections.singleton(
                        new Product2Category().setCategoryOid("categoryOid2"))));

        final Workspace workspace = new Workspace();
        final Set<PrintCmd> printCmds = new HashSet<>();
        printCmds.add(new PrintCmd()
                        .setPrinterOid("printerOid1")
                        .setTarget(PrintTarget.JOB)
                        .setTargetOid("categoryOid1"));
        printCmds.add(new PrintCmd()
                        .setPrinterOid("printerOid2")
                        .setTarget(PrintTarget.JOB)
                        .setTargetOid("categoryOid2"));
        workspace.setPrintCmds(printCmds);
        final Set<Item> items = new HashSet<>();
        items.add(new Item().setProductOid("productOid1"));
        items.add(new Item().setProductOid("productOid2"));
        items.add(new Item().setProductOid("productOid3"));
        items.add(new Item().setProductOid("productOid4"));
        items.add(new Item().setProductOid("productOid5"));
        final Order order = new Order().setItems(items);

        final Collection<Job> jobs = jobService.createJobs(workspace, order);
        assertEquals(2, jobs.size());
        final Job job1 = jobs.stream().filter(job -> job.getPrinterOid().equals("printerOid1")).findFirst().get();
        final Job job2 = jobs.stream().filter(job -> job.getPrinterOid().equals("printerOid2")).findFirst().get();
        assertEquals(2, job1.getItems().size());
        assertEquals(2, job2.getItems().size());
        assertEquals(2, mongoTemplate.findAll(Job.class).size());
    }

    @Test
    public void testGetItems()
    {
        final Set<Item> items = new HashSet<>();
        items.add(new Item().setIndex(1));
        items.add(new Item().setIndex(2));
        items.add(new Item().setIndex(3));
        items.add(new Item().setIndex(4));
        mongoTemplate.save(new Job().setItems(items));

        final Set<Item> orderItems = new HashSet<>();
        orderItems.add(new Item().setIndex(1));
        orderItems.add(new Item().setIndex(2));
        orderItems.add(new Item().setIndex(3));
        orderItems.add(new Item().setIndex(4));
        orderItems.add(new Item().setIndex(5));
        orderItems.add(new Item().setIndex(6));
        final Order order = new Order().setItems(orderItems);
        final Collection<Item> itemsToPrint = jobService.getNewItems(order);
        assertEquals(2, itemsToPrint.size());
        assertTrue(itemsToPrint.stream().allMatch(item -> item.getIndex() > 4));
    }
}
