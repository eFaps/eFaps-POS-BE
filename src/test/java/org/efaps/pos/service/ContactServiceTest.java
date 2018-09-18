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
package org.efaps.pos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.efaps.pos.entity.Contact;
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
public class ContactServiceTest
{

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ContactService contactService;

    @BeforeEach
    public void setup()
    {
        this.mongoTemplate.remove(new Query(), Contact.class);
    }

    @Test
    public void testGetContacts()
    {
        this.mongoTemplate.save(new Contact().setOid("1.1"));
        this.mongoTemplate.save(new Contact().setOid("1.2"));
        this.mongoTemplate.save(new Contact().setOid("1.3"));

        final List<Contact> contacts = this.contactService.getContacts();
        assertEquals(3, contacts.size());
    }

    @Test
    public void testGetContact()
    {
        this.mongoTemplate.save(new Contact().setOid("1.1"));
        this.mongoTemplate.save(new Contact().setOid("1.2"));
        this.mongoTemplate.save(new Contact().setOid("1.3"));

        final Contact contact = this.contactService.getContact("1.3");
        assertEquals("1.3", contact.getOid());
    }

    @Test
    public void testFindContactByOid()
    {
        this.mongoTemplate.save(new Contact().setOid("1.1"));
        this.mongoTemplate.save(new Contact().setOid("1.2"));
        this.mongoTemplate.save(new Contact().setOid("1.3"));

        final Contact contact = this.contactService.findContact("1.3");
        assertEquals("1.3", contact.getOid());
    }

    @Test
    public void testFindContactById()
    {
        this.mongoTemplate.save(new Contact().setOid("1.1").setId("FirstId"));
        this.mongoTemplate.save(new Contact().setOid("1.2").setId("SecondId"));
        this.mongoTemplate.save(new Contact().setOid("1.3").setId("ThirdId"));

        final Contact contact = this.contactService.findContact("ThirdId");
        assertEquals("1.3", contact.getOid());
    }

    @Test
    public void testFindContactReturnNull()
    {
        this.mongoTemplate.save(new Contact().setOid("1.1").setId("FirstId"));
        this.mongoTemplate.save(new Contact().setOid("1.2").setId("SecondId"));
        this.mongoTemplate.save(new Contact().setOid("1.3").setId("ThirdId"));

        final Contact contact = this.contactService.findContact("Cannot be found");
        assertNull(contact);
    }

    @Test
    public void testCreateContact()
    {
        final Contact contact = new Contact().setOid("11.23").setName("A name");
        this.contactService.createContact(contact);
        final List<Contact> contacts = this.mongoTemplate.findAll(Contact.class);
        assertEquals(1, contacts.size());
        assertEquals("11.23", contacts.get(0).getOid());
    }

    @Test
    public void testFindContactByName()
    {
        this.mongoTemplate.save(new Contact().setOid("1.1").setName("ABC"));
        this.mongoTemplate.save(new Contact().setOid("1.2").setName("ABCD"));
        this.mongoTemplate.save(new Contact().setOid("1.3").setName("DEFG"));
        this.mongoTemplate.save(new Contact().setOid("1.4").setName("ZYX"));

        final List<Contact> contacts = this.contactService.findContacts("AB", true);
        assertEquals(2, contacts.size());
    }

    @Test
    public void testFindContactByTaxnumber()
    {
        this.mongoTemplate.save(new Contact().setOid("1.1").setIdNumber("1234"));
        this.mongoTemplate.save(new Contact().setOid("1.2").setIdNumber("123456"));
        this.mongoTemplate.save(new Contact().setOid("1.3").setIdNumber("345"));
        this.mongoTemplate.save(new Contact().setOid("1.4").setIdNumber("124"));

        final List<Contact> contacts = this.contactService.findContacts("1234", false);
        assertEquals(2, contacts.size());
    }

}
