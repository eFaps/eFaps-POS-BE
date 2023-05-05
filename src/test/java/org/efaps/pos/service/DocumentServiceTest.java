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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willReturn;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.efaps.pos.dto.DocStatus;
import org.efaps.pos.entity.AbstractDocument.TaxEntry;
import org.efaps.pos.entity.Balance;
import org.efaps.pos.entity.Contact;
import org.efaps.pos.entity.Invoice;
import org.efaps.pos.entity.Order;
import org.efaps.pos.entity.Pos;
import org.efaps.pos.entity.Pos.Company;
import org.efaps.pos.entity.Receipt;
import org.efaps.pos.entity.Ticket;
import org.efaps.pos.entity.Workspace;
import org.efaps.pos.pojo.Spot;
import org.efaps.pos.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@AutoConfigureMockRestServiceServer
@ActiveProfiles(profiles = "test")
@AutoConfigureDataMongo
@SpringBootTest
public class DocumentServiceTest
{
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private OrderRepository orderRepository;

    @Mock
    private OrderRepository mockOrderRepository;


    @BeforeEach
    public void setup()
    {
        ReflectionTestUtils.setField(documentService, "orderRepository", orderRepository);
        mongoTemplate.remove(new Query(), Order.class);
        mongoTemplate.remove(new Query(), Receipt.class);
        mongoTemplate.remove(new Query(), Ticket.class);
        mongoTemplate.remove(new Query(), Invoice.class);
        mongoTemplate.remove(new Query(), Workspace.class);
        mongoTemplate.remove(new Query(), Pos.class);
        mongoTemplate.remove(new Query(), Contact.class);
        mongoTemplate.remove(new Query(), Balance.class);
    }

    @Test
    public void testGetOrder()
    {
        final Order order = new Order().setOid("1.2");
        mongoTemplate.save(new Order().setOid("1.1"));
        mongoTemplate.save(order);
        mongoTemplate.save(new Order().setOid("1.3"));

        final Order recievedOrder = documentService.getOrder(order.getId());
        assertEquals("1.2", recievedOrder.getOid());
    }

    @Test
    public void testGetOrders()
    {
        mongoTemplate.save(new Order().setOid("1.1"));
        mongoTemplate.save(new Order().setOid("1.2"));
        mongoTemplate.save(new Order().setOid("1.3"));

        final List<Order> orders = documentService.getOrders();
        assertEquals(3, orders.size());
    }

    @Test
    public void testGetOrders4Spots()
    {
        mongoTemplate.save(new Order().setOid("1.1").setStatus(DocStatus.OPEN));
        mongoTemplate.save(new Order().setOid("1.2").setStatus(DocStatus.OPEN).setSpot(new Spot()));
        mongoTemplate.save(new Order().setOid("1.3").setStatus(DocStatus.CLOSED));
        mongoTemplate.save(new Order().setOid("1.4").setStatus(DocStatus.CLOSED).setSpot(new Spot()));
        mongoTemplate.save(new Order().setOid("1.5").setStatus(DocStatus.CANCELED));
        mongoTemplate.save(new Order().setOid("1.6").setStatus(DocStatus.CANCELED).setSpot(new Spot()));

        final Collection<Order> orders = documentService.getOrders4Spots();
        assertEquals(1, orders.size());
        assertEquals("1.2", orders.iterator().next().getOid());
    }

    @Test
    public void testCreateOrder()
    {
        final Order newOrder = new Order();
        newOrder.setNetTotal(new BigDecimal("123.45869"));
        newOrder.setTaxes(Collections.emptySet());

        final Order order = documentService.createOrder(newOrder);
        assertNotNull(order.getNumber());
        assertNotNull(order.getId());
        assertEquals(new BigDecimal("123.46"), order.getNetTotal());
        assertEquals(new BigDecimal("123.46"), order.getCrossTotal());
        assertEquals(1, mongoTemplate.findAll(Order.class).size());
    }

   // @Test
    public void testUpdateOrder()
    {
        mongoTemplate.save(new Order());
        final Order updateOrder = mongoTemplate.findAll(Order.class).get(0);
        updateOrder.setNetTotal(new BigDecimal("123.45869"));
        updateOrder.setTaxes(Collections.singleton(new TaxEntry().setAmount(new BigDecimal(100))));

        final Order order = documentService.updateOrder("asda", null);
        assertNotNull(order.getId());
        assertEquals(new BigDecimal("123.46"), order.getNetTotal());
        assertEquals(new BigDecimal("223.46"), order.getCrossTotal());
        assertEquals(1, mongoTemplate.findAll(Order.class).size());
    }

    @Test
    public void testDeleteOrderSetCanceled()
    {
        mongoTemplate.save(new Order().setStatus(DocStatus.OPEN));
        final Order order = mongoTemplate.findAll(Order.class).get(0);

        documentService.deleteOrder(order.getId());
        assertEquals(1, mongoTemplate.findAll(Order.class).size());
        final Order deleted = mongoTemplate.findById(order.getId(), Order.class);
        assertEquals(DocStatus.CANCELED, deleted.getStatus());
    }

    @Test
    public void testDeleteOrderWrongStatus()
    {
        mongoTemplate.save(new Order().setStatus(DocStatus.CLOSED));
        final Order order = mongoTemplate.findAll(Order.class).get(0);

        documentService.deleteOrder(order.getId());
        assertEquals(1, mongoTemplate.findAll(Order.class).size());
    }

    @Test
    public void testDeleteOrderNotFound()
    {
        mongoTemplate.save(new Order().setStatus(DocStatus.CLOSED));
        documentService.deleteOrder("a different id");
        assertEquals(1, mongoTemplate.findAll(Order.class).size());
    }

    @Test
    public void testCreateReceipt()
    {
        ReflectionTestUtils.setField(documentService, "orderRepository", mockOrderRepository);
        final String wsOid = "123.4";
        final String posOid = "223.4";
        final String contactOid = "323.4";
        mongoTemplate.save(new Workspace().setOid(wsOid).setPosOid(posOid));
        mongoTemplate.save(new Pos().setOid(posOid)
                        .setDefaultContactOid(contactOid)
                        .setCompany(new Company()
                                        .setName("company")
                                        .setTaxNumber("12345678911")));
        mongoTemplate.save(new Contact().setOid(contactOid));

        final Order order = new Order().setStatus(DocStatus.OPEN);
        willReturn(Optional.of(order)).given(mockOrderRepository).findById(any());

        final Receipt newReceipt = new Receipt()
                        .setContactOid("1123.1")
                        .setNetTotal(new BigDecimal("123.45869"))
                        .setTaxes(Collections.emptySet())
                        .setDate(LocalDate.now());

        final Receipt receipt = documentService.createReceipt(wsOid, "orderid", newReceipt);
        assertNotNull(receipt.getNumber());
        assertNotNull(receipt.getId());
    }

    @Test
    public void testCreateReceiptCatchesError()
    {
        ReflectionTestUtils.setField(documentService, "orderRepository", mockOrderRepository);
        final String wsOid = "123.4";
        final String posOid = "223.4";
        final String contactOid = "323.4";
        mongoTemplate.save(new Workspace().setOid(wsOid).setPosOid(posOid));
        mongoTemplate.save(new Pos().setOid(posOid)
                        .setDefaultContactOid(contactOid)
                        .setCompany(new Company()
                                        .setName("company")
                                        .setTaxNumber("12345678911")));
        mongoTemplate.save(new Contact().setOid(contactOid));

        final Order order = new Order().setStatus(DocStatus.OPEN);
        willReturn(Optional.of(order)).given(mockOrderRepository).findById(any());

        final Receipt newReceipt = new Receipt()
                        .setContactOid("1123.1")
                        .setNetTotal(new BigDecimal("123.45869"))
                        .setTaxes(Collections.emptySet());

        final Receipt receipt = documentService.createReceipt(wsOid, "orderid", newReceipt);
        assertNotNull(receipt.getNumber());
        assertNotNull(receipt.getId());
    }

    @Test
    public void testCreateInvoice()
    {
        ReflectionTestUtils.setField(documentService, "orderRepository", mockOrderRepository);
        final String wsOid = "123.4";
        final String posOid = "223.4";
        final String contactOid = "323.4";
        mongoTemplate.save(new Workspace().setOid(wsOid).setPosOid(posOid));
        mongoTemplate.save(new Pos().setOid(posOid)
                        .setDefaultContactOid(contactOid)
                        .setCompany(new Company()
                                        .setName("company")
                                        .setTaxNumber("12345678911")));
        mongoTemplate.save(new Contact().setOid(contactOid));

        final Order order = new Order().setStatus(DocStatus.OPEN);
        willReturn(Optional.of(order)).given(mockOrderRepository).findById(any());

        final Invoice invoice = new Invoice()
                        .setContactOid("1123.1")
                        .setNetTotal(new BigDecimal("123.45869"))
                        .setTaxes(Collections.emptySet())
                        .setDate(LocalDate.now());

        final Invoice createdInvoice = documentService.createInvoice(wsOid, "orderid", invoice);
        assertNotNull(createdInvoice.getNumber());
        assertNotNull(createdInvoice.getId());
    }

    @Test
    public void testCreateInvoiceCatchesError()
    {
        ReflectionTestUtils.setField(documentService, "orderRepository", mockOrderRepository);
        final String wsOid = "123.4";
        final String posOid = "223.4";
        final String contactOid = "323.4";
        mongoTemplate.save(new Workspace().setOid(wsOid).setPosOid(posOid));
        mongoTemplate.save(new Pos().setOid(posOid)
                        .setDefaultContactOid(contactOid)
                        .setCompany(new Company()
                                        .setName("company")
                                        .setTaxNumber("12345678911")));
        mongoTemplate.save(new Contact().setOid(contactOid));

        final Order order = new Order().setStatus(DocStatus.OPEN);
        willReturn(Optional.of(order)).given(mockOrderRepository).findById(any());

        final Invoice invoice = new Invoice()
                        .setContactOid("1123.1")
                        .setNetTotal(new BigDecimal("123.45869"))
                        .setTaxes(Collections.emptySet());

        final Invoice createdInvoice = documentService.createInvoice(wsOid, "orderid", invoice);
        assertNotNull(createdInvoice.getNumber());
        assertNotNull(createdInvoice.getId());
    }

    @Test
    public void testCreateTicket()
    {
        ReflectionTestUtils.setField(documentService, "orderRepository", mockOrderRepository);
        final String wsOid = "123.4";
        final String posOid = "223.4";
        final String contactOid = "323.4";
        mongoTemplate.save(new Workspace().setOid(wsOid).setPosOid(posOid));
        mongoTemplate.save(new Pos().setOid(posOid)
                        .setDefaultContactOid(contactOid)
                        .setCompany(new Company()
                                        .setName("company")
                                        .setTaxNumber("12345678911")));
        mongoTemplate.save(new Contact().setOid(contactOid));

        final Order order = new Order().setStatus(DocStatus.OPEN);
        willReturn(Optional.of(order)).given(mockOrderRepository).findById(any());

        final Ticket ticket = new Ticket()
                        .setContactOid("1123.1")
                        .setNetTotal(new BigDecimal("123.45869"))
                        .setTaxes(Collections.emptySet())
                        .setDate(LocalDate.now());

        final Ticket createdTicket = documentService.createTicket(wsOid, "orderid", ticket);
        assertNotNull(createdTicket.getNumber());
        assertNotNull(createdTicket.getId());
    }

    @Test
    public void testCreateTicketCatchesError()
    {
        ReflectionTestUtils.setField(documentService, "orderRepository", mockOrderRepository);
        final String wsOid = "123.4";
        final String posOid = "223.4";
        final String contactOid = "323.4";
        mongoTemplate.save(new Workspace().setOid(wsOid).setPosOid(posOid));
        mongoTemplate.save(new Pos().setOid(posOid)
                        .setDefaultContactOid(contactOid)
                        .setCompany(new Company()
                                        .setName("company")
                                        .setTaxNumber("12345678911")));
        mongoTemplate.save(new Contact().setOid(contactOid));

        final Order order = new Order().setStatus(DocStatus.OPEN);
        willReturn(Optional.of(order)).given(mockOrderRepository).findById(any());

        final Ticket ticket = new Ticket()
                        .setContactOid("1123.1")
                        .setNetTotal(new BigDecimal("123.45869"))
                        .setTaxes(Collections.emptySet());

        final Ticket createdTicket = documentService.createTicket(wsOid, "orderid", ticket);
        assertNotNull(createdTicket.getNumber());
        assertNotNull(createdTicket.getId());
    }

    @Test
    public void testEvalBalanceOidIsValidOid() {
        final String key = "123.45";
        final String oid = documentService.evalBalanceOid(key);
        assertEquals(key, oid);
    }

    @Test
    public void testEvalBalanceOidNoBalanceFound() {
        final String key = "AnId";
        final String oid = documentService.evalBalanceOid(key);
        assertEquals(key, oid);
    }

    @Test
    public void testEvalBalanceOidBalanceNoOIDYet() {
        final Balance balance = new Balance();
        mongoTemplate.save(balance);
        final String key = balance.getId();
        final String oid = documentService.evalBalanceOid(key);
        assertEquals(key, oid);
    }

    @Test
    public void testEvalBalanceOidBalanceHasOID() {
        final Balance balance = new Balance().setOid("123.456");
        mongoTemplate.save(balance);
        final String key = balance.getId();
        final String oid = documentService.evalBalanceOid(key);
        assertEquals(balance.getOid(), oid);
    }

}
