package org.efaps.pos.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Collections;

import org.efaps.pos.dto.DocStatus;
import org.efaps.pos.dto.PosDocItemDto;
import org.efaps.pos.dto.PosOrderDto;
import org.efaps.pos.dto.PosReceiptDto;
import org.efaps.pos.entity.AbstractDocument.Item;
import org.efaps.pos.entity.Order;
import org.efaps.pos.entity.Product;
import org.efaps.pos.entity.Receipt;
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
public class DocumentControllerTest
{
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DocumentController documentController;

    @BeforeEach
    public void setup() {
        this.mongoTemplate.remove(new Query(), Product.class);
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

        final PosOrderDto dto = this.documentController.toDto(entity);
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

        final PosDocItemDto dto = this.documentController.toDto(entity);
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

        final PosReceiptDto dto = this.documentController.toDto(entity);
        assertEquals(entity.getOid(), dto.getOid());
        assertEquals(entity.getNumber(), dto.getNumber());
        assertEquals(entity.getStatus(), dto.getStatus());
    }
}
