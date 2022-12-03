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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.efaps.pos.dto.CategoryDto;
import org.efaps.pos.dto.InvoiceDto;
import org.efaps.pos.dto.PosDto;
import org.efaps.pos.dto.PrinterDto;
import org.efaps.pos.dto.ProductDto;
import org.efaps.pos.dto.ReceiptDto;
import org.efaps.pos.dto.TicketDto;
import org.efaps.pos.dto.UserDto;
import org.efaps.pos.dto.WarehouseDto;
import org.efaps.pos.dto.WorkspaceDto;
import org.efaps.pos.entity.Category;
import org.efaps.pos.entity.Contact;
import org.efaps.pos.entity.Identifier;
import org.efaps.pos.entity.Invoice;
import org.efaps.pos.entity.Pos;
import org.efaps.pos.entity.Printer;
import org.efaps.pos.entity.Product;
import org.efaps.pos.entity.Receipt;
import org.efaps.pos.entity.Ticket;
import org.efaps.pos.entity.User;
import org.efaps.pos.entity.Warehouse;
import org.efaps.pos.entity.Workspace;
import org.efaps.pos.util.SyncServiceDeactivatedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@AutoConfigureMockRestServiceServer
@ActiveProfiles(profiles = "test")
@SpringBootTest
@AutoConfigureDataMongo
public class SyncServiceTest
{
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SyncService syncService;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    public void setup()
    {
        mongoTemplate.remove(new Query(), Product.class);
        mongoTemplate.remove(new Query(), Category.class);
        mongoTemplate.remove(new Query(), Workspace.class);
        mongoTemplate.remove(new Query(), Warehouse.class);
        mongoTemplate.remove(new Query(), Printer.class);
        mongoTemplate.remove(new Query(), Pos.class);
        mongoTemplate.remove(new Query(), User.class);
        mongoTemplate.remove(new Query(), Receipt.class);
        mongoTemplate.remove(new Query(), Contact.class);
        mongoTemplate.remove(new Query(), Invoice.class);
        mongoTemplate.remove(new Query(), Ticket.class);
        mongoTemplate.save(new Identifier()
                        .setId(Identifier.KEY)
                        .setCreated(LocalDateTime.now())
                        .setIdentifier("TESTIDENT"));
    }

    //@Test
    public void testSyncProductsFirstTime()
        throws JsonProcessingException, SyncServiceDeactivatedException
    {
        assertTrue(mongoTemplate.findAll(Product.class).isEmpty());

        final ProductDto product1 = ProductDto.builder()
                        .withDescription("A product description")
                        .withOID("5586.1651")
                        .build();

        final List<ProductDto> productDtos = Arrays.asList(product1);

        server.expect(requestTo("http://localhost:8888/eFaps/servlet/rest/pos/TESTIDENT/products"))
            .andRespond(withSuccess(mapper.writeValueAsString(productDtos), MediaType.APPLICATION_JSON));

        syncService.syncProducts();

        final List<Product> products = mongoTemplate.findAll(Product.class);
        assertEquals(1, products.size());
    }

    //@Test
    public void testSyncProductsUpdate()
        throws JsonProcessingException, SyncServiceDeactivatedException
    {
        assertTrue(mongoTemplate.findAll(Product.class).isEmpty());

        final Product product = new Product()
                        .setOid("5586.1651")
                        .setDescription("Some old description");
        mongoTemplate.save(product);

        final ProductDto product1 = ProductDto.builder()
                        .withDescription("An updated product description")
                        .withOID("5586.1651")
                        .build();

        final List<ProductDto> productDtos = Arrays.asList(product1);

        server.expect(requestTo("http://localhost:8888/eFaps/servlet/rest/pos/TESTIDENT/products"))
            .andRespond(withSuccess(mapper.writeValueAsString(productDtos), MediaType.APPLICATION_JSON));

        syncService.syncProducts();

        final List<Product> products = mongoTemplate.findAll(Product.class);
        assertEquals(1, products.size());
        assertEquals("5586.1651", products.get(0).getOid());
        assertEquals("An updated product description", products.get(0).getDescription());
    }

    //@Test
    public void testSyncProductsRemoveObsolete()
        throws JsonProcessingException, SyncServiceDeactivatedException
    {
        assertTrue(mongoTemplate.findAll(Product.class).isEmpty());

        final Product product = new Product()
                        .setOid("5586.1650")
                        .setDescription("This product should be removed");
        mongoTemplate.save(product);

        final ProductDto product1 = ProductDto.builder()
                        .withDescription("An updated product description")
                        .withOID("5586.1651")
                        .build();

        final List<ProductDto> productDtos = Arrays.asList(product1);

        server.expect(requestTo("http://localhost:8888/eFaps/servlet/rest/pos/TESTIDENT/products"))
            .andRespond(withSuccess(mapper.writeValueAsString(productDtos), MediaType.APPLICATION_JSON));

        syncService.syncProducts();

        final List<Product> products = mongoTemplate.findAll(Product.class);
        assertEquals(1, products.size());
        assertEquals("5586.1651", products.get(0).getOid());
        assertEquals("An updated product description", products.get(0).getDescription());
    }

    //@Test
    public void testSyncCategoriesFirstTime()
        throws JsonProcessingException, SyncServiceDeactivatedException
    {
        assertTrue(mongoTemplate.findAll(Category.class).isEmpty());

        final CategoryDto product1 = CategoryDto.builder()
                        .withOID("5586.1651")
                        .withName("Cat Name")
                        .build();

        final List<CategoryDto> categoryDtos = Arrays.asList(product1);

        server.expect(requestTo("http://localhost:8888/eFaps/servlet/rest/pos/TESTIDENT/categories"))
            .andRespond(withSuccess(mapper.writeValueAsString(categoryDtos), MediaType.APPLICATION_JSON));

        syncService.syncCategories();

        final List<Category> categories = mongoTemplate.findAll(Category.class);
        assertEquals(1, categories.size());
    }

    //@Test
    public void testSyncCategoriesRemoveObsolete()
        throws JsonProcessingException, SyncServiceDeactivatedException
    {
        assertTrue(mongoTemplate.findAll(Category.class).isEmpty());

        final Category category = new Category()
                        .setOid("5586.1650")
                        .setName("This category should be removed");
        mongoTemplate.save(category);

        final CategoryDto category1 = CategoryDto.builder()
                        .withName("An updated category description")
                        .withOID("5586.1651")
                        .build();

        final List<CategoryDto> categoryDtos = Arrays.asList(category1);

        server.expect(requestTo("http://localhost:8888/eFaps/servlet/rest/pos/TESTIDENT/categories"))
            .andRespond(withSuccess(mapper.writeValueAsString(categoryDtos), MediaType.APPLICATION_JSON));

        syncService.syncCategories();

        final List<Category> categories = mongoTemplate.findAll(Category.class);
        assertEquals(1, categories.size());
        assertEquals("5586.1651", categories.get(0).getOid());
        assertEquals("An updated category description", categories.get(0).getName());
    }

    //@Test
    public void testSyncWorkspacesFirstTime()
        throws JsonProcessingException, SyncServiceDeactivatedException
    {
        assertTrue(mongoTemplate.findAll(Workspace.class).isEmpty());

        final WorkspaceDto product1 = WorkspaceDto.builder()
                        .withOID("5586.1651")
                        .withName("Cat Name")
                        .build();

        final List<WorkspaceDto> workspaceDtos = Arrays.asList(product1);

        server.expect(requestTo("http://localhost:8888/eFaps/servlet/rest/pos/TESTIDENT/workspaces"))
            .andRespond(withSuccess(mapper.writeValueAsString(workspaceDtos), MediaType.APPLICATION_JSON));

        syncService.syncWorkspaces();

        final List<Workspace> categories = mongoTemplate.findAll(Workspace.class);
        assertEquals(1, categories.size());
    }

    //@Test
    public void testSyncWorkspacesRemoveObsolete()
        throws JsonProcessingException, SyncServiceDeactivatedException
    {
        assertTrue(mongoTemplate.findAll(Workspace.class).isEmpty());

        final Workspace workspace = new Workspace()
                        .setOid("5586.1650")
                        .setName("This workspace should be removed");
        mongoTemplate.save(workspace);

        final WorkspaceDto workspace1 = WorkspaceDto.builder()
                        .withName("A workspace description")
                        .withOID("5586.1651")
                        .build();

        final List<WorkspaceDto> workspaceDtos = Arrays.asList(workspace1);

        server.expect(requestTo("http://localhost:8888/eFaps/servlet/rest/pos/TESTIDENT/workspaces"))
            .andRespond(withSuccess(mapper.writeValueAsString(workspaceDtos), MediaType.APPLICATION_JSON));

        syncService.syncWorkspaces();

        final List<Workspace> workspaces = mongoTemplate.findAll(Workspace.class);
        assertEquals(1, workspaces.size());
        assertEquals("5586.1651", workspaces.get(0).getOid());
        assertEquals("A workspace description", workspaces.get(0).getName());
    }

    //@Test
    public void testSyncWarehousesFirstTime()
        throws JsonProcessingException, SyncServiceDeactivatedException
    {
        assertTrue(mongoTemplate.findAll(Warehouse.class).isEmpty());

        final WarehouseDto product1 = WarehouseDto.builder()
                        .withOID("5586.1651")
                        .withName("Cat Name")
                        .build();

        final List<WarehouseDto> warehouseDtos = Arrays.asList(product1);

        server.expect(requestTo("http://localhost:8888/eFaps/servlet/rest/pos/warehouses"))
            .andRespond(withSuccess(mapper.writeValueAsString(warehouseDtos), MediaType.APPLICATION_JSON));

        syncService.syncWarehouses();

        final List<Warehouse> warehouses = mongoTemplate.findAll(Warehouse.class);
        assertEquals(1, warehouses.size());
    }

    //@Test
    public void testSyncWarehousesRemoveObsolete()
        throws JsonProcessingException, SyncServiceDeactivatedException
    {
        assertTrue(mongoTemplate.findAll(Warehouse.class).isEmpty());

        final Warehouse warehouse = new Warehouse()
                        .setOid("5586.1650")
                        .setName("This warehouse should be removed");
        mongoTemplate.save(warehouse);

        final WarehouseDto warehouse1 = WarehouseDto.builder()
                        .withName("A warehouse description")
                        .withOID("5586.1651")
                        .build();

        final List<WarehouseDto> warehouseDtos = Arrays.asList(warehouse1);

        server.expect(requestTo("http://localhost:8888/eFaps/servlet/rest/pos/warehouses"))
            .andRespond(withSuccess(mapper.writeValueAsString(warehouseDtos), MediaType.APPLICATION_JSON));

        syncService.syncWarehouses();

        final List<Warehouse> warehouses = mongoTemplate.findAll(Warehouse.class);
        assertEquals(1, warehouses.size());
        assertEquals("5586.1651", warehouses.get(0).getOid());
        assertEquals("A warehouse description", warehouses.get(0).getName());
    }

    //@Test
    public void testSyncPrintersFirstTime()
        throws JsonProcessingException, SyncServiceDeactivatedException
    {
        assertTrue(mongoTemplate.findAll(Printer.class).isEmpty());

        final PrinterDto product1 = PrinterDto.builder()
                        .withOID("5586.1651")
                        .withName("Cat Name")
                        .build();

        final List<PrinterDto> printerDtos = Arrays.asList(product1);

        server.expect(requestTo("http://localhost:8888/eFaps/servlet/rest/pos/printers"))
            .andRespond(withSuccess(mapper.writeValueAsString(printerDtos), MediaType.APPLICATION_JSON));

        syncService.syncPrinters();

        final List<Printer> printers = mongoTemplate.findAll(Printer.class);
        assertEquals(1, printers.size());
    }

    //@Test
    public void testSyncPrintersRemoveObsolete()
        throws JsonProcessingException, SyncServiceDeactivatedException
    {
        assertTrue(mongoTemplate.findAll(Printer.class).isEmpty());

        final Printer printer = new Printer()
                        .setOid("5586.1650")
                        .setName("This printer should be removed");
        mongoTemplate.save(printer);

        final PrinterDto printer1 = PrinterDto.builder()
                        .withName("A printer description")
                        .withOID("5586.1651")
                        .build();

        final List<PrinterDto> printerDtos = Arrays.asList(printer1);

        server.expect(requestTo("http://localhost:8888/eFaps/servlet/rest/pos/printers"))
            .andRespond(withSuccess(mapper.writeValueAsString(printerDtos), MediaType.APPLICATION_JSON));

        syncService.syncPrinters();

        final List<Printer> printers = mongoTemplate.findAll(Printer.class);
        assertEquals(1, printers.size());
        assertEquals("5586.1651", printers.get(0).getOid());
        assertEquals("A printer description", printers.get(0).getName());
    }

    //@Test
    public void testSyncPossFirstTime()
        throws JsonProcessingException, SyncServiceDeactivatedException
    {
        assertTrue(mongoTemplate.findAll(Pos.class).isEmpty());

        final PosDto product1 = PosDto.builder()
                        .withOID("5586.1651")
                        .withName("Cat Name")
                        .build();

        final List<PosDto> posDtos = Arrays.asList(product1);

        server.expect(requestTo("http://localhost:8888/eFaps/servlet/rest/pos/TESTIDENT/poss"))
            .andRespond(withSuccess(mapper.writeValueAsString(posDtos), MediaType.APPLICATION_JSON));

        syncService.syncPOSs();

        final List<Pos> poss = mongoTemplate.findAll(Pos.class);
        assertEquals(1, poss.size());
    }

    //@Test
    public void testSyncPossRemoveObsolete()
        throws JsonProcessingException, SyncServiceDeactivatedException
    {
        assertTrue(mongoTemplate.findAll(Pos.class).isEmpty());

        final Pos pos = new Pos()
                        .setOid("5586.1650")
                        .setName("This pos should be removed");
        mongoTemplate.save(pos);

        final PosDto pos1 = PosDto.builder()
                        .withName("A pos description")
                        .withOID("5586.1651")
                        .build();

        final List<PosDto> posDtos = Arrays.asList(pos1);

        server.expect(requestTo("http://localhost:8888/eFaps/servlet/rest/pos/TESTIDENT/poss"))
            .andRespond(withSuccess(mapper.writeValueAsString(posDtos), MediaType.APPLICATION_JSON));

        syncService.syncPOSs();

        final List<Pos> poss = mongoTemplate.findAll(Pos.class);
        assertEquals(1, poss.size());
        assertEquals("5586.1651", poss.get(0).getOid());
        assertEquals("A pos description", poss.get(0).getName());
    }

    //@Test
    public void testSyncUsersFirstTime()
        throws JsonProcessingException, SyncServiceDeactivatedException
    {
        assertTrue(mongoTemplate.findAll(User.class).isEmpty());

        final UserDto user1 = UserDto.builder()
                        .withOID("5586.1651")
                        .withFirstName("Cat Name")
                        .build();

        final List<UserDto> userDtos = Arrays.asList(user1);

        server.expect(requestTo("http://localhost:8888/eFaps/servlet/rest/pos/users"))
            .andRespond(withSuccess(mapper.writeValueAsString(userDtos), MediaType.APPLICATION_JSON));

        syncService.syncUsers();

        final List<User> users = mongoTemplate.findAll(User.class);
        assertEquals(1, users.size());
    }

    //@Test
    public void testSyncUsersRemoveObsolete()
        throws JsonProcessingException, SyncServiceDeactivatedException
    {
        assertTrue(mongoTemplate.findAll(User.class).isEmpty());

        final User user = new User()
                        .setOid("5586.1650")
                        .setFirstName("This user should be removed");
        mongoTemplate.save(user);

        final UserDto user1 = UserDto.builder()
                        .withOID("5586.1651")
                        .withFirstName("A user description")
                        .build();

        final List<UserDto> userDtos = Arrays.asList(user1);

        server.expect(requestTo("http://localhost:8888/eFaps/servlet/rest/pos/users"))
            .andRespond(withSuccess(mapper.writeValueAsString(userDtos), MediaType.APPLICATION_JSON));

        syncService.syncUsers();

        final List<User> users = mongoTemplate.findAll(User.class);
        assertEquals(1, users.size());
        assertEquals("5586.1651", users.get(0).getOid());
        assertEquals("A user description", users.get(0).getFirstName());
    }

    ////@Test
    public void testSyncReceiptsNoContact() throws SyncServiceDeactivatedException
    {
        assertTrue(mongoTemplate.findAll(Receipt.class).isEmpty());
        final Receipt receipt1 = new Receipt()
                        .setOid("5586.1650");
        mongoTemplate.save(receipt1);
        final Receipt receipt2 = new Receipt();
        mongoTemplate.save(receipt2);

        syncService.syncReceipts();

        final Receipt checkReceipt2 = mongoTemplate.findById(receipt2.getId(), Receipt.class);
        assertNull(checkReceipt2.getOid());
    }

    //@Test
    public void testSyncReceipts()
        throws JsonProcessingException, SyncServiceDeactivatedException
    {
        assertTrue(mongoTemplate.findAll(Receipt.class).isEmpty());
        final Contact contact = new Contact().setOid("123.456");
        mongoTemplate.save(contact);
        final Receipt receipt1 = new Receipt()
                        .setOid("5586.1650");
        mongoTemplate.save(receipt1);
        final Receipt receipt2 = new Receipt()
                        .setContactOid(contact.getOid())
                        .setBalanceOid("123.456");
        mongoTemplate.save(receipt2);

        final ReceiptDto responseDto = ReceiptDto.builder()
                        .withId(receipt2.getId())
                        .withOID("5555.6666")
                        .build();

        server.expect(requestTo("http://localhost:8888/eFaps/servlet/rest/pos/receipts"))
            .andRespond(withSuccess(mapper.writeValueAsString(responseDto), MediaType.APPLICATION_JSON));

        syncService.syncReceipts();

        final Receipt checkReceipt2 = mongoTemplate.findById(receipt2.getId(), Receipt.class);
        assertEquals(responseDto.getOid(), checkReceipt2.getOid());
    }

    //@Test
    public void testSyncInvoicesNoContact() throws SyncServiceDeactivatedException
    {
        assertTrue(mongoTemplate.findAll(Invoice.class).isEmpty());
        final Invoice invoice1 = new Invoice()
                        .setOid("5586.1650")
                        .setContactOid("123.456");
        mongoTemplate.save(invoice1);
        final Invoice invoice2 = new Invoice();
        mongoTemplate.save(invoice2).setContactOid("116516515");

        syncService.syncInvoices();

        final Invoice checkInvoice2 = mongoTemplate.findById(invoice2.getId(), Invoice.class);
        assertNull(checkInvoice2.getOid());
    }

    //@Test
    public void testSyncInvoices()
        throws JsonProcessingException, SyncServiceDeactivatedException
    {
        assertTrue(mongoTemplate.findAll(Invoice.class).isEmpty());
        final Contact contact = new Contact().setOid("123.456");
        mongoTemplate.save(contact);
        final Invoice invoice1 = new Invoice()
                        .setOid("5586.1650");
        mongoTemplate.save(invoice1);
        final Invoice invoice2 = new Invoice()
                        .setContactOid(contact.getOid())
                        .setBalanceOid("123.456");
        mongoTemplate.save(invoice2);

        final InvoiceDto responseDto = InvoiceDto.builder()
                        .withId(invoice2.getId())
                        .withOID("5555.6666")
                        .build();

        server.expect(requestTo("http://localhost:8888/eFaps/servlet/rest/pos/invoices"))
            .andRespond(withSuccess(mapper.writeValueAsString(responseDto), MediaType.APPLICATION_JSON));

        syncService.syncInvoices();

        final Invoice checkInvoice2 = mongoTemplate.findById(invoice2.getId(), Invoice.class);
        assertEquals(responseDto.getOid(), checkInvoice2.getOid());
    }

    //@Test
    public void testSyncTicketsNoContact() throws SyncServiceDeactivatedException
    {
        assertTrue(mongoTemplate.findAll(Ticket.class).isEmpty());
        final Ticket ticket1 = new Ticket()
                        .setOid("5586.1650");
        mongoTemplate.save(ticket1);
        final Ticket ticket2 = new Ticket();
        mongoTemplate.save(ticket2);

        syncService.syncTickets();

        final Ticket checkTicket2 = mongoTemplate.findById(ticket2.getId(), Ticket.class);
        assertNull(checkTicket2.getOid());
    }

    //@Test
    public void testSyncTickets()
        throws JsonProcessingException, SyncServiceDeactivatedException
    {
        assertTrue(mongoTemplate.findAll(Ticket.class).isEmpty());
        final Contact contact = new Contact().setOid("123.456");
        mongoTemplate.save(contact);
        final Ticket ticket1 = new Ticket()
                        .setOid("5586.1650")
                        .setBalanceOid("123.456");
        mongoTemplate.save(ticket1);
        final Ticket ticket2 = new Ticket()
                        .setContactOid(contact.getOid())
                        .setBalanceOid("123.456");
        mongoTemplate.save(ticket2);

        final TicketDto responseDto = TicketDto.builder()
                        .withId(ticket2.getId())
                        .withOID("5555.6666")
                        .withBalanceOid("123.456")
                        .build();

        server.expect(requestTo("http://localhost:8888/eFaps/servlet/rest/pos/tickets"))
            .andRespond(withSuccess(mapper.writeValueAsString(responseDto), MediaType.APPLICATION_JSON));

        syncService.syncTickets();

        final Ticket checkTicket2 = mongoTemplate.findById(ticket2.getId(), Ticket.class);
        assertEquals(responseDto.getOid(), checkTicket2.getOid());
    }
}
