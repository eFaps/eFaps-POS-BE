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
package org.efaps.pos.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Collections;

import org.efaps.pos.dto.CategoryDto;
import org.efaps.pos.dto.DocStatus;
import org.efaps.pos.dto.DocType;
import org.efaps.pos.dto.InventoryEntryDto;
import org.efaps.pos.dto.JobDto;
import org.efaps.pos.dto.PaymentDto;
import org.efaps.pos.dto.PosDocItemDto;
import org.efaps.pos.dto.PosDto;
import org.efaps.pos.dto.PosInvoiceDto;
import org.efaps.pos.dto.PosOrderDto;
import org.efaps.pos.dto.PosReceiptDto;
import org.efaps.pos.dto.PosTicketDto;
import org.efaps.pos.dto.PosUserDto;
import org.efaps.pos.dto.PrinterDto;
import org.efaps.pos.dto.PrinterType;
import org.efaps.pos.dto.ProductDto;
import org.efaps.pos.dto.Roles;
import org.efaps.pos.dto.SequenceDto;
import org.efaps.pos.dto.SpotConfig;
import org.efaps.pos.dto.SpotDto;
import org.efaps.pos.dto.TaxDto;
import org.efaps.pos.dto.TaxEntryDto;
import org.efaps.pos.dto.UserDto;
import org.efaps.pos.dto.WarehouseDto;
import org.efaps.pos.dto.WorkspaceDto;
import org.efaps.pos.entity.AbstractDocument.Item;
import org.efaps.pos.entity.Category;
import org.efaps.pos.entity.InventoryEntry;
import org.efaps.pos.entity.Invoice;
import org.efaps.pos.entity.Job;
import org.efaps.pos.entity.Order;
import org.efaps.pos.entity.Pos;
import org.efaps.pos.entity.Printer;
import org.efaps.pos.entity.Product;
import org.efaps.pos.entity.Receipt;
import org.efaps.pos.entity.Sequence;
import org.efaps.pos.entity.Spot;
import org.efaps.pos.entity.Tax;
import org.efaps.pos.entity.Ticket;
import org.efaps.pos.entity.User;
import org.efaps.pos.entity.Warehouse;
import org.efaps.pos.entity.Workspace;
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
public class ConverterTest
{
    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    public void setup() {
        this.mongoTemplate.remove(new Query(), Product.class);
    }

    @Test
    public void testProductToEntity() {
        final ProductDto dto = ProductDto.builder()
                        .withOID("Asda")
                        .withSKU("100612.001")
                        .withDescription("This is the product Description")
                        .withImageOid("1234.1")
                        .withNetPrice(new BigDecimal("12.50"))
                        .withCrossPrice(new BigDecimal("14.40"))
                        .withCategoryOids(Collections.singleton("555.1651"))
                        .withTaxes(Collections.singleton(TaxDto.builder().build()))
                        .build();

        final Product product = Converter.toEntity(dto);
        assertEquals(dto.getOid(), product.getOid());
        assertEquals(dto.getSku(), product.getSKU());
        assertEquals(dto.getDescription(), product.getDescription());
        assertEquals(dto.getImageOid(), product.getImageOid());
        assertEquals(dto.getNetPrice(), product.getNetPrice());
        assertEquals(dto.getCrossPrice(), product.getCrossPrice());
        assertEquals(dto.getCategoryOids(), product.getCategoryOids());
        assertEquals(dto.getTaxes().size(), product.getTaxes().size());
    }

    @Test
    public void testProductToDto() {
        final Product entity = new Product()
                        .setSKU("100612.001")
                        .setDescription("This is the product Description")
                        .setImageOid("1234.1")
                        .setNetPrice(new BigDecimal("12.50"))
                        .setCrossPrice(new BigDecimal("14.40"))
                        .setOid("165165.14651")
                        .setCategoryOids(Collections.singleton("5515.1651"));

        final ProductDto dto = Converter.toDto(entity);
        assertEquals(entity.getOid(), dto.getOid());
        assertEquals(entity.getSKU(), dto.getSku());
        assertEquals(entity.getDescription(), dto.getDescription());
        assertEquals(entity.getImageOid(), dto.getImageOid());
        assertEquals(entity.getNetPrice(), dto.getNetPrice());
        assertEquals(entity.getCrossPrice(), dto.getCrossPrice());
        assertEquals(entity.getCategoryOids(), dto.getCategoryOids());
    }

    @Test
    public void testUserToDto() {
        final User entity = new User()
                        .setUsername("a username")
                        .setFirstName("Juan")
                        .setSurName("Perez")
                        .setOid("165165.14651");

        final PosUserDto dto = Converter.toDto(entity);
        assertEquals(entity.getUsername(), dto.getUsername());
        assertEquals(entity.getFirstName(), dto.getFirstName());
        assertEquals(entity.getSurName(), dto.getSurName());
    }

    @Test
    public void testWorkspaceToDto() {
        final Workspace entity = new Workspace()
                        .setOid("165165.14651")
                        .setName("Caja 1")
                        .setPosOid("9999.514651")
                        .setWarehouseOid("6542.36");

        final WorkspaceDto dto = Converter.toDto(entity);
        assertEquals(entity.getOid(), dto.getOid());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getPosOid(), dto.getPosOid());
        assertEquals(entity.getWarehouseOid(), dto.getWarehouseOid());
    }

    @Test
    public void testPosToDto() {
        final Pos entity = new Pos()
                        .setName("Caja 1")
                        .setOid("165165.14651");

        final PosDto dto = Converter.toDto(entity);
        assertEquals(entity.getOid(), dto.getOid());
        assertEquals(entity.getName(), dto.getName());
    }

    @Test
    public void testCategoryToDto() {
        final Category entity = new Category()
                        .setName("Caja 1")
                        .setOid("165165.14651");

        final CategoryDto dto = Converter.toDto(entity);
        assertEquals(entity.getOid(), dto.getOid());
        assertEquals(entity.getName(), dto.getName());
    }

    @Test
    public void testReceiptToEntity() {
        final PosReceiptDto dto = PosReceiptDto.builder()
                        .withOID("16515.5165")
                        .withNumber("B001-1651651")
                        .withStatus(DocStatus.CLOSED)
                        .withItems(Collections.singleton(PosDocItemDto.builder().build()))
                        .withTaxes(Collections.singleton(TaxEntryDto.builder().build()))
                        .withPayments(Collections.singleton(PaymentDto.builder().build()))
                        .build();

        final Receipt entity = Converter.toEntity(dto);
        assertEquals(dto.getOid(), entity.getOid());
        assertEquals(dto.getNumber(), entity.getNumber());
        assertEquals(dto.getStatus(), entity.getStatus());
    }

    @Test
    public void testInvoiceToEntity() {
        final PosInvoiceDto dto = PosInvoiceDto.builder()
                        .withOID("16515.5165")
                        .withNumber("B001-1651651")
                        .withStatus(DocStatus.CLOSED)
                        .withItems(Collections.singleton(PosDocItemDto.builder().build()))
                        .withTaxes(Collections.singleton(TaxEntryDto.builder().build()))
                        .withPayments(Collections.singleton(PaymentDto.builder().build()))
                        .build();

        final Invoice entity = Converter.toEntity(dto);
        assertEquals(dto.getOid(), entity.getOid());
        assertEquals(dto.getNumber(), entity.getNumber());
        assertEquals(dto.getStatus(), entity.getStatus());
    }

    @Test
    public void testTicketToEntity() {
        final PosTicketDto dto = PosTicketDto.builder()
                        .withOID("16515.5165")
                        .withNumber("B001-1651651")
                        .withStatus(DocStatus.CLOSED)
                        .withItems(Collections.singleton(PosDocItemDto.builder().build()))
                        .withTaxes(Collections.singleton(TaxEntryDto.builder().build()))
                        .withPayments(Collections.singleton(PaymentDto.builder().build()))
                        .build();

        final Ticket entity = Converter.toEntity(dto);
        assertEquals(dto.getOid(), entity.getOid());
        assertEquals(dto.getNumber(), entity.getNumber());
        assertEquals(dto.getStatus(), entity.getStatus());
    }

    @Test
    public void testOrderToEntity() {
        final PosOrderDto dto = PosOrderDto.builder()
                        .withOID("16515.5165")
                        .withNumber("B001-1651651")
                        .withItems(Collections.singleton(PosDocItemDto.builder().build()))
                        .withStatus(DocStatus.CLOSED)
                        .withTaxes(Collections.singleton(TaxEntryDto.builder().build()))
                        .withNetTotal(new BigDecimal("12.44"))
                        .withCrossTotal(new BigDecimal("32.44"))
                        .build();

        final Order entity = Converter.toEntity(dto);
        assertEquals(dto.getOid(), entity.getOid());
        assertEquals(dto.getNumber(), entity.getNumber());
        assertEquals(1, entity.getItems().size());
        assertEquals(dto.getStatus(), entity.getStatus());
        assertEquals(dto.getNetTotal(), entity.getNetTotal());
        assertEquals(dto.getCrossTotal(), entity.getCrossTotal());
        assertEquals(1, entity.getTaxes().size());
    }

    @Test
    public void testItemToEntity() {
        final PosDocItemDto dto = PosDocItemDto.builder()
                        .withOID("16515.5165")
                        .withIndex(2)
                        .withCrossPrice(new BigDecimal("1.11"))
                        .withCrossUnitPrice(new BigDecimal("1.12"))
                        .withNetPrice(new BigDecimal("1.13"))
                        .withNetUnitPrice(new BigDecimal("1.14"))
                        .withQuantity(new BigDecimal("1.15"))
                        .withProductOid("ProductOid")
                        .withTaxes(Collections.singleton(TaxEntryDto.builder().build()))
                        .build();

        final Item entity = Converter.toEntity(dto);
        assertEquals(dto.getOid(), entity.getOid());
        assertEquals(dto.getIndex(), entity.getIndex());
        assertEquals(dto.getCrossPrice(), entity.getCrossPrice());
        assertEquals(dto.getCrossUnitPrice(), entity.getCrossUnitPrice());
        assertEquals(dto.getNetPrice(), entity.getNetPrice());
        assertEquals(dto.getNetUnitPrice(), entity.getNetUnitPrice());
        assertEquals(dto.getQuantity(), entity.getQuantity());
        assertEquals(dto.getProductOid(), entity.getProductOid());
        assertEquals(1, entity.getTaxes().size());
    }

    @Test
    public void testItemToEntityWithProduct() {
        final PosDocItemDto dto = PosDocItemDto.builder()
                        .withProductOid("ProductOid")
                        .withProduct(ProductDto.builder().withOID("ThisOid").build())
                        .build();

        final Item entity = Converter.toEntity(dto);
        assertEquals("ThisOid", entity.getProductOid());
    }

    @Test
    public void testTaxToEntity() {
        final TaxDto dto = TaxDto.builder()
                        .withOID("653.25")
                        .withName("VAT")
                        .withPercent(new BigDecimal("18"))
                        .build();

        final Tax entity = Converter.toEntity(dto);
        assertEquals(dto.getOid(), entity.getOid());
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getPercent(), entity.getPercent());
    }

    @Test
    public void testTaxToDto() {
        final Tax entity = new Tax()
                        .setOid("123.44")
                        .setName("VAT")
                        .setPercent(new BigDecimal("18"));

        final TaxDto dto = Converter.toDto(entity);
        assertEquals(entity.getOid(), dto.getOid());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getPercent(), dto.getPercent());
    }

    @Test
    public void testOrderToDto() {
        final Order entity = new Order()
                        .setOid("165165.14651")
                        .setNumber("B001-165165")
                        .setItems(Collections.singleton(new Item()))
                        .setStatus(DocStatus.CLOSED)
                        .setTaxes(Collections.emptySet())
                        .setNetTotal(new BigDecimal("12.44"))
                        .setCrossTotal(new BigDecimal("32.44"));

        final PosOrderDto dto = Converter.toDto(entity);
        assertEquals(entity.getOid(), dto.getOid());
        assertEquals(entity.getNumber(), dto.getNumber());
        assertEquals(1, dto.getItems().size());
        assertEquals(dto.getStatus(), entity.getStatus());
        assertEquals(0, dto.getTaxes().size());
        assertEquals(entity.getNetTotal(), dto.getNetTotal());
        assertEquals(entity.getCrossTotal(), dto.getCrossTotal());
    }

    @Test
    public void testItemToDto() {
        final Product product = new Product()
                        .setOid("1234.652")
                        .setDescription("This is a description");
        this.mongoTemplate.save(product);

        final Item entity = new Item()
                        .setIndex(1)
                        .setOid("5555.622")
                        .setCrossPrice(new BigDecimal("1.11"))
                        .setCrossUnitPrice(new BigDecimal("1.12"))
                        .setNetPrice(new BigDecimal("1.13"))
                        .setNetUnitPrice(new BigDecimal("1.14"))
                        .setQuantity(new BigDecimal("1.15"))
                        .setProductOid("1234.652");

        final PosDocItemDto dto = Converter.toDto(entity);
        assertEquals(entity.getOid(), dto.getOid());
        assertEquals(entity.getIndex(), dto.getIndex());
        assertEquals(entity.getCrossPrice(), dto.getCrossPrice());
        assertEquals(entity.getCrossUnitPrice(), dto.getCrossUnitPrice());
        assertEquals(entity.getNetPrice(), dto.getNetPrice());
        assertEquals(entity.getNetUnitPrice(), dto.getNetUnitPrice());
        assertEquals(entity.getQuantity(), dto.getQuantity());
        assertEquals(entity.getProductOid(), dto.getProductOid());
    }

    @Test
    public void testReceiptToDto() {
        final Receipt entity = new Receipt()
                        .setOid("165165.14651")
                        .setNumber("B001-165165")
                        .setStatus(DocStatus.CLOSED);

        final PosReceiptDto dto = Converter.toDto(entity);
        assertEquals(entity.getOid(), dto.getOid());
        assertEquals(entity.getNumber(), dto.getNumber());
        assertEquals(entity.getStatus(), dto.getStatus());
    }

    @Test
    public void testSpotToEntity() {
        final SpotDto dto = SpotDto.builder()
                        .withId("id 1")
                        .withLabel("Label")
                        .build();
        final Spot entity = Converter.toEntity(dto);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getLabel(), entity.getLabel());
    }

    @Test
    public void testUserToEntity() {
        final UserDto dto = UserDto.builder()
                        .withOID("id 1")
                        .withFirstName("First Name")
                        .withSurName("Last Name")
                        .withPassword("thats secret")
                        .withRoles(Collections.singleton(Roles.ADMIN))
                        .withWorkspaceOids(Collections.singleton("123.4"))
                        .build();
        final User entity = Converter.toEntity(dto);
        assertEquals(dto.getOid(), entity.getOid());
        assertEquals(dto.getFirstName(), entity.getFirstName());
        assertEquals(dto.getSurName(), entity.getSurName());
        assertEquals(dto.getPassword(), entity.getPassword());
        assertEquals(dto.getRoles(), entity.getRoles());
        assertEquals(dto.getWorkspaceOids(), entity.getWorkspaceOids());
    }

    @Test
    public void testSequenceToEntity() {
        final SequenceDto dto = SequenceDto.builder()
                        .withOID("id 1")
                        .withFormat("Format")
                        .withSeq(22)
                        .build();
        final Sequence entity = Converter.toEntity(dto);
        assertEquals(dto.getOid(), entity.getOid());
        assertEquals(dto.getFormat(), entity.getFormat());
        assertEquals(dto.getSeq(), entity.getSeq());
    }

    @Test
    public void testWorkspaceToEntity() {
        final WorkspaceDto dto = WorkspaceDto.builder()
                        .withOID("id 1")
                        .withName("Name")
                        .withDocTypes(Collections.singleton(DocType.INVOICE))
                        .withPosOid("123.4")
                        .withSpotConfig(SpotConfig.BASIC)
                        .withWarehouseOid("1235.6")
                        .build();
        final Workspace entity = Converter.toEntity(dto);
        assertEquals(dto.getOid(), entity.getOid());
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getDocTypes(), entity.getDocTypes());
        assertEquals(dto.getPosOid(), entity.getPosOid());
        assertEquals(dto.getSpotConfig(), entity.getSpotConfig());
        assertEquals(dto.getWarehouseOid(), entity.getWarehouseOid());
    }

    @Test
    public void testWarehouseToEntity() {
        final WarehouseDto dto = WarehouseDto.builder()
                        .withOID("id 1")
                        .withName("Name")
                        .build();
        final Warehouse entity = Converter.toEntity(dto);
        assertEquals(dto.getOid(), entity.getOid());
        assertEquals(dto.getName(), entity.getName());
    }

    @Test
    public void testWarehouseToDto() {
        final Warehouse entity = new Warehouse()
                        .setOid("id 1")
                        .setName("Name");
        final WarehouseDto dto = Converter.toDto(entity);
        assertEquals(entity.getOid(), dto.getOid());
        assertEquals(entity.getName(), dto.getName());
    }

    @Test
    public void testInventoryToEntity() {
        final InventoryEntryDto dto = InventoryEntryDto.builder()
                        .withOID("id 1")
                        .withProductOid("Some oid")
                        .withWarehouseOid("some warehouse")
                        .withQuantity(BigDecimal.TEN)
                        .build();
        final InventoryEntry entity = Converter.toEntity(dto);
        assertEquals(dto.getOid(), entity.getOid());
        assertEquals(dto.getProductOid(), entity.getProductOid());
        assertEquals(dto.getWarehouseOid(), entity.getWarehouseOid());
        assertEquals(dto.getQuantity(), entity.getQuantity());
    }

    @Test
    public void testJobToDto() {
        final Job entity = new Job()
                        .setId("someId")
                        .setDocumentId("documentId");
        final JobDto dto = Converter.toDto(entity);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getDocumentId(), dto.getDocumentId());
    }

    @Test
    public void testPrinterToEntity() {
        final PrinterDto dto = PrinterDto.builder()
                        .withOID("id 1")
                        .withName("Name")
                        .withType(PrinterType.PHYSICAL)
                        .build();
        final Printer entity = Converter.toEntity(dto);
        assertEquals(dto.getOid(), entity.getOid());
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getType(), entity.getType());
    }
}
