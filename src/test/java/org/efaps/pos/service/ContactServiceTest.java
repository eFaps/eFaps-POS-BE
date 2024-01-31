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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.efaps.pos.entity.Contact;
import org.efaps.pos.entity.Visibility;
import org.efaps.pos.error.PreconditionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@AutoConfigureMockRestServiceServer
@ActiveProfiles(profiles = "test")
@AutoConfigureDataMongo
@SpringBootTest
public class ContactServiceTest
{

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ContactService contactService;

    @BeforeEach
    public void setup()
    {
        mongoTemplate.remove(new Query(), Contact.class);
    }

    @Test
    public void testGetContacts()
    {
        mongoTemplate.save(new Contact().setOid("1.1").setVisibility(Visibility.VISIBLE));
        mongoTemplate.save(new Contact().setOid("1.2").setVisibility(Visibility.VISIBLE));
        mongoTemplate.save(new Contact().setOid("1.3").setVisibility(Visibility.VISIBLE));
        mongoTemplate.save(new Contact().setOid("1.4").setVisibility(Visibility.HIDDEN));

        final List<Contact> contacts = contactService.getContacts(PageRequest.of(0, 10)).getContent();
        assertEquals(3, contacts.size());
    }

    @Test
    public void testGetContact()
    {
        mongoTemplate.save(new Contact().setOid("1.1"));
        mongoTemplate.save(new Contact().setOid("1.2"));
        mongoTemplate.save(new Contact().setOid("1.3"));

        final Contact contact = contactService.getContact("1.3");
        assertEquals("1.3", contact.getOid());
    }

    @Test
    public void testFindContactByOid()
    {
        mongoTemplate.save(new Contact().setOid("1.1"));
        mongoTemplate.save(new Contact().setOid("1.2"));
        mongoTemplate.save(new Contact().setOid("1.3"));

        final Contact contact = contactService.findContact("1.3");
        assertEquals("1.3", contact.getOid());
    }

    @Test
    public void testFindContactById()
    {
        mongoTemplate.save(new Contact().setOid("1.1").setId("FirstId"));
        mongoTemplate.save(new Contact().setOid("1.2").setId("SecondId"));
        mongoTemplate.save(new Contact().setOid("1.3").setId("ThirdId"));

        final Contact contact = contactService.findContact("ThirdId");
        assertEquals("1.3", contact.getOid());
    }

    @Test
    public void testFindContactReturnNull()
    {
        mongoTemplate.save(new Contact().setOid("1.1").setId("FirstId"));
        mongoTemplate.save(new Contact().setOid("1.2").setId("SecondId"));
        mongoTemplate.save(new Contact().setOid("1.3").setId("ThirdId"));

        final Contact contact = contactService.findContact("Cannot be found");
        assertNull(contact);
    }

    @Test
    public void testCreateContact()
        throws PreconditionException
    {
        final Contact contact = new Contact().setOid("11.23").setName("A name");
        contactService.createContact(contact);
        final List<Contact> contacts = mongoTemplate.findAll(Contact.class);
        assertEquals(1, contacts.size());
        assertEquals("11.23", contacts.get(0).getOid());
    }

    @Test
    public void testFindContactByName()
    {
        mongoTemplate.save(new Contact().setOid("1.1").setName("ABC").setVisibility(Visibility.VISIBLE));
        mongoTemplate.save(new Contact().setOid("1.2").setName("ABCD").setVisibility(Visibility.VISIBLE));
        mongoTemplate.save(new Contact().setOid("1.3").setName("DEFG").setVisibility(Visibility.VISIBLE));
        mongoTemplate.save(new Contact().setOid("1.4").setName("ZYX").setVisibility(Visibility.VISIBLE));
        mongoTemplate.save(new Contact().setOid("1.5").setName("ZYXAB").setVisibility(Visibility.VISIBLE));
        mongoTemplate.save(new Contact().setOid("1.6").setName("ZABYX").setVisibility(Visibility.VISIBLE));
        mongoTemplate.save(new Contact().setOid("1.7").setName("ZAVYX").setVisibility(Visibility.VISIBLE));
        mongoTemplate.save(new Contact().setOid("1.8").setName("zAbyX").setVisibility(Visibility.VISIBLE));
        mongoTemplate.save(new Contact().setOid("1.9").setName("abZAVYX").setVisibility(Visibility.VISIBLE));
        mongoTemplate.save(new Contact().setOid("1.9").setName("abAB").setVisibility(Visibility.HIDDEN));

        final List<Contact> contacts = contactService.findContacts("AB", true);
        assertEquals(6, contacts.size());
        final List<Contact> contacts2 = contactService.findContacts("ab", true);
        assertEquals(6, contacts2.size());
    }

    @Test
    public void testFindContactByTaxnumber()
    {
        mongoTemplate.save(new Contact().setOid("1.1").setIdNumber("1234").setVisibility(Visibility.VISIBLE));
        mongoTemplate.save(new Contact().setOid("1.2").setIdNumber("123456").setVisibility(Visibility.VISIBLE));
        mongoTemplate.save(new Contact().setOid("1.3").setIdNumber("345").setVisibility(Visibility.VISIBLE));
        mongoTemplate.save(new Contact().setOid("1.4").setIdNumber("124").setVisibility(Visibility.VISIBLE));

        final List<Contact> contacts = contactService.findContacts("1234", false);
        assertEquals(2, contacts.size());
    }

}
