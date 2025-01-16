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
package org.efaps.pos.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Collections;

import org.efaps.pos.dto.BalanceDto;
import org.efaps.pos.dto.BalanceStatus;
import org.efaps.pos.dto.CategoryDto;
import org.efaps.pos.dto.ContactDto;
import org.efaps.pos.dto.Currency;
import org.efaps.pos.dto.DocItemDto;
import org.efaps.pos.dto.DocStatus;
import org.efaps.pos.dto.DocType;
import org.efaps.pos.dto.IdentificationType;
import org.efaps.pos.dto.InventoryEntryDto;
import org.efaps.pos.dto.InvoiceDto;
import org.efaps.pos.dto.JobDto;
import org.efaps.pos.dto.PosDocItemDto;
import org.efaps.pos.dto.PosDto;
import org.efaps.pos.dto.PosInventoryEntryDto;
import org.efaps.pos.dto.PosInvoiceDto;
import org.efaps.pos.dto.PosLayout;
import org.efaps.pos.dto.PosOrderDto;
import org.efaps.pos.dto.PosPaymentCardDto;
import org.efaps.pos.dto.PosPaymentCashDto;
import org.efaps.pos.dto.PosPaymentChangeDto;
import org.efaps.pos.dto.PosReceiptDto;
import org.efaps.pos.dto.PosTicketDto;
import org.efaps.pos.dto.PosUserDto;
import org.efaps.pos.dto.PrintCmdDto;
import org.efaps.pos.dto.PrintTarget;
import org.efaps.pos.dto.PrinterDto;
import org.efaps.pos.dto.PrinterType;
import org.efaps.pos.dto.Product2CategoryDto;
import org.efaps.pos.dto.ProductDto;
import org.efaps.pos.dto.ProductRelationDto;
import org.efaps.pos.dto.ReceiptDto;
import org.efaps.pos.dto.SequenceDto;
import org.efaps.pos.dto.SpotConfig;
import org.efaps.pos.dto.SpotDto;
import org.efaps.pos.dto.TaxDto;
import org.efaps.pos.dto.TaxEntryDto;
import org.efaps.pos.dto.TicketDto;
import org.efaps.pos.dto.UserDto;
import org.efaps.pos.dto.WarehouseDto;
import org.efaps.pos.dto.WorkspaceDto;
import org.efaps.pos.entity.AbstractDocument.Item;
import org.efaps.pos.entity.Balance;
import org.efaps.pos.entity.Category;
import org.efaps.pos.entity.Contact;
import org.efaps.pos.entity.InventoryEntry;
import org.efaps.pos.entity.Invoice;
import org.efaps.pos.entity.Job;
import org.efaps.pos.entity.Order;
import org.efaps.pos.entity.Pos;
import org.efaps.pos.entity.Printer;
import org.efaps.pos.entity.Product;
import org.efaps.pos.entity.Receipt;
import org.efaps.pos.entity.Sequence;
import org.efaps.pos.entity.Ticket;
import org.efaps.pos.entity.User;
import org.efaps.pos.entity.Warehouse;
import org.efaps.pos.entity.Workspace;
import org.efaps.pos.entity.Workspace.PrintCmd;
import org.efaps.pos.pojo.Product2Category;
import org.efaps.pos.pojo.ProductRelation;
import org.efaps.pos.pojo.Spot;
import org.efaps.pos.pojo.Tax;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles(profiles = "test")
@AutoConfigureDataMongo
@SpringBootTest
public class ConverterTest
{

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    public void setup()
    {
        mongoTemplate.remove(new Query(), Product.class);
        mongoTemplate.remove(new Query(), Order.class);
        mongoTemplate.remove(new Query(), Warehouse.class);
    }

    @Test
    public void testProductToEntity()
    {
        final ProductDto dto = ProductDto.builder()
                        .withOID("Asda")
                        .withSKU("100612.001")
                        .withDescription("This is the product Description")
                        .withImageOid("1234.1")
                        .withNetPrice(new BigDecimal("12.50"))
                        .withCrossPrice(new BigDecimal("14.40"))
                        .withCategories(Collections
                                        .singleton(Product2CategoryDto.builder().withCategoryOid("555.1651").build()))
                        .withTaxes(Collections.singleton(TaxDto.builder().build()))
                        .build();

        final Product product = Converter.toEntity(dto);
        assertEquals(dto.getOid(), product.getOid());
        assertEquals(dto.getSku(), product.getSKU());
        assertEquals(dto.getDescription(), product.getDescription());
        assertEquals(dto.getImageOid(), product.getImageOid());
        assertEquals(dto.getNetPrice(), product.getNetPrice());
        assertEquals(dto.getCrossPrice(), product.getCrossPrice());
        assertEquals(dto.getCategories().iterator().next().getCategoryOid(),
                        product.getCategories().iterator().next().getCategoryOid());
        assertEquals(dto.getTaxes().size(), product.getTaxes().size());
    }

    @Test
    public void testProductToDto()
    {
        final Product entity = new Product()
                        .setSKU("100612.001")
                        .setDescription("This is the product Description")
                        .setImageOid("1234.1")
                        .setNetPrice(new BigDecimal("12.50"))
                        .setCrossPrice(new BigDecimal("14.40"))
                        .setOid("165165.14651")
                        .setCategories(Collections.singleton(new Product2Category().setCategoryOid("5515.1651")));

        final ProductDto dto = Converter.toDto(entity);
        assertEquals(entity.getOid(), dto.getOid());
        assertEquals(entity.getSKU(), dto.getSku());
        assertEquals(entity.getDescription(), dto.getDescription());
        assertEquals(entity.getImageOid(), dto.getImageOid());
        assertEquals(entity.getNetPrice(), dto.getNetPrice());
        assertEquals(entity.getCrossPrice(), dto.getCrossPrice());
        assertEquals(entity.getCategories().iterator().next().getCategoryOid(),
                        dto.getCategories().iterator().next().getCategoryOid());
    }

    @Test
    public void testUserToDto()
    {
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
    public void testWorkspaceToDto()
    {
        final Workspace entity = new Workspace()
                        .setOid("165165.14651")
                        .setName("Caja 1")
                        .setPosOid("9999.514651")
                        .setWarehouseOid("6542.36")
                        .setPosLayout(PosLayout.GRID);

        final WorkspaceDto dto = Converter.toDto(entity);
        assertEquals(entity.getOid(), dto.getOid());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getPosOid(), dto.getPosOid());
        assertEquals(entity.getWarehouseOid(), dto.getWarehouseOid());
        assertEquals(entity.getPosLayout(), dto.getPosLayout());
    }

    @Test
    public void testPosToDto()
    {
        final Pos entity = new Pos()
                        .setName("Caja 1")
                        .setOid("165165.14651");

        final PosDto dto = Converter.toDto(entity);
        assertEquals(entity.getOid(), dto.getOid());
        assertEquals(entity.getName(), dto.getName());
    }

    @Test
    public void testCategoryToDto()
    {
        final Category entity = new Category()
                        .setName("Caja 1")
                        .setOid("165165.14651")
                        .setImageOid("image.OID");

        final CategoryDto dto = Converter.toDto(entity);
        assertEquals(entity.getOid(), dto.getOid());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getImageOid(), dto.getImageOid());
    }

    @Test
    public void testCategoryToEntity()
    {
        final CategoryDto dto = CategoryDto.builder()
                        .withOID("165165.14651")
                        .withName("Caja 1")
                        .withImageOid("image.OId")
                        .build();

        final Category entity = Converter.toEntity(dto);
        assertEquals(entity.getOid(), dto.getOid());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getImageOid(), dto.getImageOid());
    }

    @Test
    public void testContactToDto()
    {
        final Contact entity = new Contact()
                        .setId("the mongo id")
                        .setName("Contact 1")
                        .setOid("165165.14651")
                        .setIdType(IdentificationType.PASSPORT)
                        .setIdNumber("ABCTHDD");

        final ContactDto dto = Converter.toDto(entity);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getOid(), dto.getOid());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getIdType(), dto.getIdType());
        assertEquals(entity.getIdNumber(), dto.getIdNumber());
    }

    @Test
    public void testContactToEntity()
    {
        final ContactDto dto = ContactDto.builder()
                        .withId("this is the id")
                        .withOID("165165.14651")
                        .withName("Caja 1")
                        .withIdType(IdentificationType.PASSPORT)
                        .withIdNumber("ABCTHDD")
                        .build();

        final Contact entity = Converter.toEntity(dto);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getOid(), entity.getOid());
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getIdType(), entity.getIdType());
        assertEquals(dto.getIdNumber(), entity.getIdNumber());
    }

    @Test
    public void testReceiptToEntity()
    {
        final PosReceiptDto dto = PosReceiptDto.builder()
                        .withOID("16515.5165")
                        .withNumber("B001-1651651")
                        .withStatus(DocStatus.CLOSED)
                        .withItems(Collections.singleton(PosDocItemDto.builder().build()))
                        .withTaxes(Collections.singleton(TaxEntryDto.builder().build()))
                        .withPayments(Collections.singleton(PosPaymentCardDto.builder().build()))
                        .build();

        final Receipt entity = Converter.toEntity(dto);
        assertEquals(dto.getOid(), entity.getOid());
        assertEquals(dto.getNumber(), entity.getNumber());
        assertEquals(dto.getStatus(), entity.getStatus());
    }

    @Test
    public void testInvoiceToEntity()
    {
        final PosInvoiceDto dto = PosInvoiceDto.builder()
                        .withOID("16515.5165")
                        .withNumber("B001-1651651")
                        .withStatus(DocStatus.CLOSED)
                        .withItems(Collections.singleton(PosDocItemDto.builder().build()))
                        .withTaxes(Collections.singleton(TaxEntryDto.builder().build()))
                        .withPayments(Collections.singleton(PosPaymentCashDto.builder().build()))
                        .build();

        final Invoice entity = Converter.toEntity(dto);
        assertEquals(dto.getOid(), entity.getOid());
        assertEquals(dto.getNumber(), entity.getNumber());
        assertEquals(dto.getStatus(), entity.getStatus());
    }

    @Test
    public void testTicketToEntity()
    {
        final PosTicketDto dto = PosTicketDto.builder()
                        .withOID("16515.5165")
                        .withNumber("B001-1651651")
                        .withStatus(DocStatus.CLOSED)
                        .withItems(Collections.singleton(PosDocItemDto.builder().build()))
                        .withTaxes(Collections.singleton(TaxEntryDto.builder().build()))
                        .withPayments(Collections.singleton(PosPaymentChangeDto.builder().build()))
                        .build();

        final Ticket entity = Converter.toEntity(dto);
        assertEquals(dto.getOid(), entity.getOid());
        assertEquals(dto.getNumber(), entity.getNumber());
        assertEquals(dto.getStatus(), entity.getStatus());
    }

    @Test
    public void testOrderToEntity()
    {
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
    public void testItemToEntity()
    {
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
    public void testItemToEntityWithProduct()
    {
        final PosDocItemDto dto = PosDocItemDto.builder()
                        .withProductOid("ProductOid")
                        .withProduct(ProductDto.builder().withOID("ThisOid").build())
                        .build();

        final Item entity = Converter.toEntity(dto);
        assertEquals("ThisOid", entity.getProductOid());
    }

    @Test
    public void testTaxToEntity()
    {
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
    public void testTaxToDto()
    {
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
    public void testOrderToDto()
    {
        final Order entity = new Order()
                        .setOid("165165.14651")
                        .setNumber("B001-165165")
                        .setItems(Collections.singletonList(new Item()))
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
    public void testItemToDto()
    {
        final Product product = new Product()
                        .setOid("1234.652")
                        .setDescription("This is a description");
        mongoTemplate.save(product);

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
    public void testItemToItemDto()
    {
        final Item entity = new Item()
                        .setIndex(1)
                        .setOid("5555.622")
                        .setCrossPrice(new BigDecimal("1.11"))
                        .setCrossUnitPrice(new BigDecimal("1.12"))
                        .setNetPrice(new BigDecimal("1.13"))
                        .setNetUnitPrice(new BigDecimal("1.14"))
                        .setQuantity(new BigDecimal("1.15"))
                        .setProductOid("1234.652");

        final DocItemDto dto = Converter.toItemDto(entity);
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
    public void testSpotToEntity()
    {
        final SpotDto dto = SpotDto.builder()
                        .withLabel("Label")
                        .withOID("123.45")
                        .build();
        final Spot entity = Converter.toEntity(dto);
        assertEquals(dto.getOid(), entity.getId());
        assertEquals(dto.getLabel(), entity.getLabel());
    }

    @Test
    public void testUserToEntity()
    {
        final UserDto dto = UserDto.builder()
                        .withOID("id 1")
                        .withFirstName("First Name")
                        .withSurName("Last Name")
                        .withPassword("thats secret")
                        .withWorkspaceOids(Collections.singleton("123.4"))
                        .build();
        final User entity = Converter.toEntity(dto);
        assertEquals(dto.getOid(), entity.getOid());
        assertEquals(dto.getFirstName(), entity.getFirstName());
        assertEquals(dto.getSurName(), entity.getSurName());
        assertEquals(dto.getPassword(), entity.getPassword());
        assertEquals(dto.getPermissions(), entity.getPermissions());
        assertEquals(dto.getWorkspaceOids(), entity.getWorkspaceOids());
    }

    @Test
    public void testSequenceToEntity()
    {
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
    public void testWorkspaceToEntity()
    {
        final WorkspaceDto dto = WorkspaceDto.builder()
                        .withOID("id 1")
                        .withName("Name")
                        .withDocTypes(Collections.singleton(DocType.INVOICE))
                        .withPosOid("123.4")
                        .withSpotConfig(SpotConfig.BASIC)
                        .withWarehouseOid("1235.6")
                        .withPosLayout(PosLayout.BOTH)
                        .build();
        final Workspace entity = Converter.toEntity(dto);
        assertEquals(dto.getOid(), entity.getOid());
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getDocTypes(), entity.getDocTypes());
        assertEquals(dto.getPosOid(), entity.getPosOid());
        assertEquals(dto.getSpotConfig(), entity.getSpotConfig());
        assertEquals(dto.getWarehouseOid(), entity.getWarehouseOid());
        assertEquals(dto.getPosLayout(), entity.getPosLayout());
    }

    @Test
    public void testWarehouseToEntity()
    {
        final WarehouseDto dto = WarehouseDto.builder()
                        .withOID("id 1")
                        .withName("Name")
                        .build();
        final Warehouse entity = Converter.toEntity(dto);
        assertEquals(dto.getOid(), entity.getOid());
        assertEquals(dto.getName(), entity.getName());
    }

    @Test
    public void testWarehouseToDto()
    {
        final Warehouse entity = new Warehouse()
                        .setOid("id 1")
                        .setName("Name");
        final WarehouseDto dto = Converter.toDto(entity);
        assertEquals(entity.getOid(), dto.getOid());
        assertEquals(entity.getName(), dto.getName());
    }

    @Test
    public void testInventoryToEntity()
    {
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
    public void testInventoryToDto()
    {
        final Product product = new Product().setOid("productOid");
        mongoTemplate.save(product);
        final Warehouse warehouse = new Warehouse().setOid("warehouseOid");
        mongoTemplate.save(warehouse);

        final InventoryEntry entity = new InventoryEntry()
                        .setOid("id 1")
                        .setProductOid(product.getOid())
                        .setWarehouseOid(warehouse.getOid())
                        .setQuantity(BigDecimal.TEN);
        final PosInventoryEntryDto dto = Converter.toDto(entity);
        assertEquals(entity.getOid(), dto.getOid());
        assertEquals(entity.getProductOid(), dto.getProduct().getOid());
        assertEquals(entity.getWarehouseOid(), dto.getWarehouse().getOid());
        assertEquals(entity.getQuantity(), entity.getQuantity());
    }

    @Test
    public void testJobToDto()
    {
        final Order order = new Order();
        mongoTemplate.save(order);
        final Job entity = new Job()
                        .setId("someId")
                        .setDocumentId(order.getId());
        final JobDto dto = Converter.toDto(entity);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getDocumentId(), dto.getDocumentId());
    }

    @Test
    public void testPrinterToEntity()
    {
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

    @Test
    public void testPrinterToDto()
    {
        final Printer entity = new Printer()
                        .setOid("someId")
                        .setName("name")
                        .setType(PrinterType.PHYSICAL);
        final PrinterDto dto = Converter.toDto(entity);
        assertEquals(entity.getOid(), dto.getOid());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getType(), dto.getType());
    }

    @Test
    public void testPrintCmdToEntity()
    {
        final PrintCmdDto dto = PrintCmdDto.builder()
                        .withPrinterOid("printerOid")
                        .withReportOid("reportOid")
                        .withTargetOid("targetOid")
                        .withTarget(PrintTarget.JOB)
                        .build();
        final PrintCmd entity = Converter.toEntity(dto);
        assertEquals(dto.getPrinterOid(), entity.getPrinterOid());
        assertEquals(dto.getReportOid(), entity.getReportOid());
        assertEquals(dto.getTargetOid(), entity.getTargetOid());
        assertEquals(dto.getTarget(), entity.getTarget());
    }

    @Test
    public void testPrintCmdToDto()
    {
        final PrintCmd entity = new PrintCmd()
                        .setPrinterOid("printerOid")
                        .setReportOid("reportOid")
                        .setTargetOid("targetOid");
        final PrintCmdDto dto = Converter.toDto(entity);
        assertEquals(entity.getPrinterOid(), dto.getPrinterOid());
        assertEquals(entity.getReportOid(), dto.getReportOid());
        assertEquals(entity.getTargetOid(), dto.getTargetOid());
        assertEquals(entity.getTarget(), dto.getTarget());
    }

    @Test
    public void testInvoiceToInvoiceDto()
    {
        final Invoice entity = new Invoice()
                        .setId("an id")
                        .setOid("OID")
                        .setNumber("Number")
                        .setDate(LocalDate.now())
                        .setCurrency(Currency.PEN)
                        .setStatus(DocStatus.OPEN)
                        .setCrossTotal(BigDecimal.TEN)
                        .setNetTotal(BigDecimal.ONE)
                        .setContactOid("ContactOid")
                        .setWorkspaceOid("workspaceOid");

        final InvoiceDto dto = Converter.toInvoiceDto(entity);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getOid(), dto.getOid());
        assertEquals(entity.getNumber(), dto.getNumber());
        assertEquals(entity.getDate(), dto.getDate());
        assertEquals(entity.getCurrency(), dto.getCurrency());
        assertEquals(entity.getCrossTotal(), dto.getCrossTotal());
        assertEquals(entity.getNetTotal(), dto.getNetTotal());
        assertEquals(entity.getContactOid(), dto.getContactOid());
        assertEquals(entity.getWorkspaceOid(), dto.getWorkspaceOid());
    }

    @Test
    public void testInvoiceToDto()
    {
        final Invoice entity = new Invoice()
                        .setId("an id")
                        .setOid("OID")
                        .setNumber("Number")
                        .setDate(LocalDate.now())
                        .setCurrency(Currency.PEN)
                        .setStatus(DocStatus.OPEN)
                        .setCrossTotal(BigDecimal.TEN)
                        .setNetTotal(BigDecimal.ONE)
                        .setContactOid("ContactOid")
                        .setWorkspaceOid("workspaceOid");

        final PosInvoiceDto dto = Converter.toDto(entity);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getOid(), dto.getOid());
        assertEquals(entity.getNumber(), dto.getNumber());
        assertEquals(entity.getDate(), dto.getDate());
        assertEquals(entity.getCurrency(), dto.getCurrency());
        assertEquals(entity.getCrossTotal(), dto.getCrossTotal());
        assertEquals(entity.getNetTotal(), dto.getNetTotal());
        assertEquals(entity.getContactOid(), dto.getContactOid());
        assertEquals(entity.getWorkspaceOid(), dto.getWorkspaceOid());
    }

    @Test
    public void testReceiptToReceiptDto()
    {
        final Receipt entity = new Receipt()
                        .setId("an id")
                        .setOid("OID")
                        .setNumber("Number")
                        .setDate(LocalDate.now())
                        .setCurrency(Currency.PEN)
                        .setStatus(DocStatus.OPEN)
                        .setCrossTotal(BigDecimal.TEN)
                        .setNetTotal(BigDecimal.ONE)
                        .setContactOid("ContactOid")
                        .setWorkspaceOid("workspaceOid");

        final ReceiptDto dto = Converter.toReceiptDto(entity);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getOid(), dto.getOid());
        assertEquals(entity.getNumber(), dto.getNumber());
        assertEquals(entity.getDate(), dto.getDate());
        assertEquals(entity.getCurrency(), dto.getCurrency());
        assertEquals(entity.getCrossTotal(), dto.getCrossTotal());
        assertEquals(entity.getNetTotal(), dto.getNetTotal());
        assertEquals(entity.getContactOid(), dto.getContactOid());
        assertEquals(entity.getWorkspaceOid(), dto.getWorkspaceOid());
    }

    @Test
    public void testReceiptToDto()
    {
        final Receipt entity = new Receipt()
                        .setId("an id")
                        .setOid("OID")
                        .setNumber("Number")
                        .setDate(LocalDate.now())
                        .setCurrency(Currency.PEN)
                        .setStatus(DocStatus.OPEN)
                        .setCrossTotal(BigDecimal.TEN)
                        .setNetTotal(BigDecimal.ONE)
                        .setContactOid("ContactOid")
                        .setWorkspaceOid("workspaceOid");

        final PosReceiptDto dto = Converter.toDto(entity);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getOid(), dto.getOid());
        assertEquals(entity.getNumber(), dto.getNumber());
        assertEquals(entity.getDate(), dto.getDate());
        assertEquals(entity.getCurrency(), dto.getCurrency());
        assertEquals(entity.getCrossTotal(), dto.getCrossTotal());
        assertEquals(entity.getNetTotal(), dto.getNetTotal());
        assertEquals(entity.getContactOid(), dto.getContactOid());
        assertEquals(entity.getWorkspaceOid(), dto.getWorkspaceOid());
    }

    @Test
    public void testTicketToTicketDto()
    {
        final Ticket entity = new Ticket()
                        .setId("an id")
                        .setOid("OID")
                        .setNumber("Number")
                        .setDate(LocalDate.now())
                        .setCurrency(Currency.PEN)
                        .setStatus(DocStatus.OPEN)
                        .setCrossTotal(BigDecimal.TEN)
                        .setNetTotal(BigDecimal.ONE)
                        .setContactOid("ContactOid")
                        .setWorkspaceOid("workspaceOid");

        final TicketDto dto = Converter.toTicketDto(entity);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getOid(), dto.getOid());
        assertEquals(entity.getNumber(), dto.getNumber());
        assertEquals(entity.getDate(), dto.getDate());
        assertEquals(entity.getCurrency(), dto.getCurrency());
        assertEquals(entity.getCrossTotal(), dto.getCrossTotal());
        assertEquals(entity.getNetTotal(), dto.getNetTotal());
        assertEquals(entity.getContactOid(), dto.getContactOid());
        assertEquals(entity.getWorkspaceOid(), dto.getWorkspaceOid());
    }

    @Test
    public void testTicketToDto()
    {
        final Ticket entity = new Ticket()
                        .setId("an id")
                        .setOid("OID")
                        .setNumber("Number")
                        .setDate(LocalDate.now())
                        .setCurrency(Currency.PEN)
                        .setStatus(DocStatus.OPEN)
                        .setCrossTotal(BigDecimal.TEN)
                        .setNetTotal(BigDecimal.ONE)
                        .setContactOid("ContactOid")
                        .setWorkspaceOid("workspaceOid");

        final PosTicketDto dto = Converter.toDto(entity);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getOid(), dto.getOid());
        assertEquals(entity.getNumber(), dto.getNumber());
        assertEquals(entity.getDate(), dto.getDate());
        assertEquals(entity.getCurrency(), dto.getCurrency());
        assertEquals(entity.getCrossTotal(), dto.getCrossTotal());
        assertEquals(entity.getNetTotal(), dto.getNetTotal());
        assertEquals(entity.getContactOid(), dto.getContactOid());
        assertEquals(entity.getWorkspaceOid(), dto.getWorkspaceOid());
    }

    @Test
    public void testPosToEntity()
    {
        final PosDto dto = PosDto.builder()
                        .withOID("1651.1651")
                        .withName("A name")
                        .withCurrency(Currency.PEN)
                        .withDefaultContactOid("998.15")
                        .withReceiptSeqOid("3998.15")
                        .withInvoiceSeqOid("29d98.15")
                        .withTicketSeqOid("1998.15")
                        .build();
        final Pos entity = Converter.toEntity(dto);
        assertEquals(entity.getOid(), dto.getOid());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getCurrency(), dto.getCurrency());
        assertEquals(entity.getDefaultContactOid(), dto.getDefaultContactOid());
        assertEquals(entity.getReceiptSeqOid(), dto.getReceiptSeqOid());
        assertEquals(entity.getInvoiceSeqOid(), dto.getInvoiceSeqOid());
        assertEquals(entity.getTicketSeqOid(), dto.getTicketSeqOid());
    }

    @Test
    public void testBalanceToEntity()
    {
        final BalanceDto dto = BalanceDto.builder()
                        .withOID("5556.26")
                        .withId("asimsddfsdfs")
                        .withNumber("a number")
                        .withUserOid("12369.99")
                        .withEndAt(OffsetDateTime.now())
                        .withStartAt(OffsetDateTime.now())
                        .withStatus(BalanceStatus.OPEN)
                        .build();
        final Balance entity = Converter.toEntity(dto);
        assertEquals(dto.getOid(), entity.getOid());
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getUserOid(), entity.getUserOid());
        assertEquals(dto.getId(), entity.getId());
        assertNotNull(entity.getEndAt());
        assertNotNull(entity.getStartAt());
        assertEquals(dto.getStatus(), entity.getStatus());
    }

    @Test
    public void testBalanceToDto()
    {
        final Balance entity = new Balance()
                        .setOid("5556.26")
                        .setId("asimsddfsdfs")
                        .setNumber("a number")
                        .setUserOid("1984.5161")
                        .setEndAt(LocalDateTime.now())
                        .setStartAt(LocalDateTime.now())
                        .setStatus(BalanceStatus.CLOSED);
        final BalanceDto dto = Converter.toBalanceDto(entity);
        assertEquals(entity.getOid(), dto.getOid());
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getNumber(), dto.getNumber());
        assertEquals(entity.getUserOid(), dto.getUserOid());
        assertNotNull(dto.getEndAt());
        assertNotNull(dto.getStartAt());
        assertEquals(entity.getStatus(), dto.getStatus());
    }

    @Test
    public void testProductRelationToEntity()
    {
        final ProductRelationDto dto = ProductRelationDto.builder()
                        .withLabel("a label")
                        .withProductOid("986.253")
                        .build();
        final ProductRelation entity = Converter.toEntity(dto);
        assertEquals(dto.getLabel(), entity.getLabel());
        assertEquals(dto.getProductOid(), entity.getProductOid());
    }

    @Test
    public void testProductRelationToDto()
    {
        final ProductRelation entity = new ProductRelation()
                        .setLabel("asimsddfsdfs")
                        .setProductOid("1984.5161");
        final ProductRelationDto dto = Converter.toDto(entity);
        assertEquals(entity.getLabel(), dto.getLabel());
        assertEquals(entity.getProductOid(), dto.getProductOid());
    }
}
