package org.efaps.pos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.efaps.pos.entity.AbstractDocument.Item;
import org.efaps.pos.entity.Category;
import org.efaps.pos.entity.Job;
import org.efaps.pos.entity.Order;
import org.efaps.pos.entity.Printer;
import org.efaps.pos.entity.Product;
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
public class JobServiceTest
{

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private JobService jobService;

    @BeforeEach
    public void setup()
    {
        this.mongoTemplate.remove(new Query(), Job.class);
        this.mongoTemplate.remove(new Query(), Category.class);
        this.mongoTemplate.remove(new Query(), Printer.class);
        this.mongoTemplate.remove(new Query(), Product.class);
    }

    @Test
    public void testCreateJobsNoItems()
    {
        final Collection<Job> jobs = this.jobService.createJobs(new Order().setItems(Collections.emptySet()));
        assertTrue(jobs.isEmpty());
    }

    @Test
    public void testCreateJobsProductDoesNotExist()
    {
        final Set<Item> items = new HashSet<>();
        items.add(new Item().setProductOid("123.456"));
        final Order order = new Order().setItems(items);

        final Collection<Job> jobs = this.jobService.createJobs(order);
        assertTrue(jobs.isEmpty());
    }

    @Test
    public void testCreateJobsProductNoCategories()
    {
        this.mongoTemplate.save(new Product().setOid("productOid"));

        final Set<Item> items = new HashSet<>();
        items.add(new Item().setProductOid("productOid"));
        final Order order = new Order().setItems(items);

        final Collection<Job> jobs = this.jobService.createJobs(order);
        assertTrue(jobs.isEmpty());
    }

    @Test
    public void testCreateJobsCategoryDoesNotExist()
    {
        this.mongoTemplate.save(new Product().setOid("productOid").setCategoryOids(Collections.singleton(
                        "categoryOid")));

        final Set<Item> items = new HashSet<>();
        items.add(new Item().setProductOid("productOid"));
        final Order order = new Order().setItems(items);

        final Collection<Job> jobs = this.jobService.createJobs(order);
        assertTrue(jobs.isEmpty());
    }

    @Test
    public void testCreateJobsCategoryNoPrinter()
    {
        this.mongoTemplate.save(new Category().setOid("categoryOid"));
        this.mongoTemplate.save(new Product().setOid("productOid").setCategoryOids(Collections.singleton(
                        "categoryOid")));

        final Set<Item> items = new HashSet<>();
        items.add(new Item().setProductOid("productOid"));
        final Order order = new Order().setItems(items);

        final Collection<Job> jobs = this.jobService.createJobs(order);
        assertTrue(jobs.isEmpty());
    }

    @Test
    public void testCreateJobsPrinterDoesNotExist()
    {
        this.mongoTemplate.save(new Category().setOid("categoryOid").setJobPrinterOid("printerOid"));
        this.mongoTemplate.save(new Product().setOid("productOid").setCategoryOids(Collections.singleton(
                        "categoryOid")));

        final Set<Item> items = new HashSet<>();
        items.add(new Item().setProductOid("productOid"));
        final Order order = new Order().setItems(items);

        final Collection<Job> jobs = this.jobService.createJobs(order);
        assertTrue(jobs.isEmpty());
    }

    @Test
    public void testCreateJob()
    {
        this.mongoTemplate.save(new Printer().setOid("printerOid"));
        this.mongoTemplate.save(new Category().setOid("categoryOid").setJobPrinterOid("printerOid"));
        this.mongoTemplate.save(new Product().setOid("productOid").setCategoryOids(Collections.singleton(
                        "categoryOid")));

        final Set<Item> items = new HashSet<>();
        items.add(new Item().setProductOid("productOid"));
        final Order order = new Order().setItems(items);

        final Collection<Job> jobs = this.jobService.createJobs(order);
        assertEquals(1, jobs.size());
        assertNotNull(jobs.iterator().next().getId());
        assertEquals(1, this.mongoTemplate.findAll(Job.class).size());
    }

    @Test
    public void testCreateJobs()
    {
        this.mongoTemplate.save(new Printer().setOid("printerOid1"));
        this.mongoTemplate.save(new Printer().setOid("printerOid2"));
        this.mongoTemplate.save(new Category().setOid("categoryOid1").setJobPrinterOid("printerOid1"));
        this.mongoTemplate.save(new Category().setOid("categoryOid2").setJobPrinterOid("printerOid1"));
        this.mongoTemplate.save(new Category().setOid("categoryOid3").setJobPrinterOid("printerOid2"));

        this.mongoTemplate.save(new Product().setOid("productOid1").setCategoryOids(Collections.singleton(
                        "categoryOid1")));
        this.mongoTemplate.save(new Product().setOid("productOid2").setCategoryOids(Collections.singleton(
                        "categoryOid2")));
        this.mongoTemplate.save(new Product().setOid("productOid3").setCategoryOids(Collections.singleton(
                        "categoryOid3")));
        this.mongoTemplate.save(new Product().setOid("productOid4").setCategoryOids(Collections.singleton(
                        "categoryOid1")));
        this.mongoTemplate.save(new Product().setOid("productOid5").setCategoryOids(Collections.singleton(
                        "categoryOid2")));

        final Set<Item> items = new HashSet<>();
        items.add(new Item().setProductOid("productOid1"));
        items.add(new Item().setProductOid("productOid2"));
        items.add(new Item().setProductOid("productOid3"));
        items.add(new Item().setProductOid("productOid4"));
        items.add(new Item().setProductOid("productOid5"));
        final Order order = new Order().setItems(items);

        final Collection<Job> jobs = this.jobService.createJobs(order);
        assertEquals(2, jobs.size());
        final Job job1 = jobs.stream().filter(job -> job.getPrinterOid().equals("printerOid1")).findFirst().get();
        final Job job2 = jobs.stream().filter(job -> job.getPrinterOid().equals("printerOid2")).findFirst().get();
        assertEquals(4, job1.getItems().size());
        assertEquals(1, job2.getItems().size());
        assertEquals(2, this.mongoTemplate.findAll(Job.class).size());
    }

    @Test
    public void testGetItems() {
        final Set<Item> items = new HashSet<>();
        items.add(new Item().setIndex(1));
        items.add(new Item().setIndex(2));
        items.add(new Item().setIndex(3));
        items.add(new Item().setIndex(4));
        this.mongoTemplate.save(new Job().setItems(items));

        final Set<Item> orderItems = new HashSet<>();
        orderItems.add(new Item().setIndex(1));
        orderItems.add(new Item().setIndex(2));
        orderItems.add(new Item().setIndex(3));
        orderItems.add(new Item().setIndex(4));
        orderItems.add(new Item().setIndex(5));
        orderItems.add(new Item().setIndex(6));
        final Order order = new Order().setItems(orderItems);
        final Collection<Item> itemsToPrint = this.jobService.getNewItems(order);
        assertEquals(2, itemsToPrint.size());
        assertTrue(itemsToPrint.stream().allMatch(item -> item.getIndex() > 4));
    }
}
