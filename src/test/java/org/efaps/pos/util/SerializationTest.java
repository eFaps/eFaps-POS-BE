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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Set;

import org.efaps.pos.dto.Currency;
import org.efaps.pos.dto.DocStatus;
import org.efaps.pos.dto.IPosPaymentDto;
import org.efaps.pos.dto.PaymentType;
import org.efaps.pos.dto.PosDocItemDto;
import org.efaps.pos.dto.PosPaymentCashDto;
import org.efaps.pos.dto.PosPaymentElectronicDto;
import org.efaps.pos.dto.PosPaymentLoyaltyPointsDto;
import org.efaps.pos.dto.PosReceiptDto;
import org.efaps.pos.dto.TaxEntryDto;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class SerializationTest
{

    private ObjectMapper getObjectMapper()
    {
        final var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }

    private PosPaymentCashDto getPaymentCashDto()
    {
        return PosPaymentCashDto.builder()
                        .withCollectOrderId("collectOrderId")
                        .withOID("123.456")
                        .withAmount(new BigDecimal("12.56"))
                        .withCurrency(Currency.PEN)
                        .withExchangeRate(BigDecimal.ONE)
                        .build();
    }

    private PosPaymentLoyaltyPointsDto getPaymentLoyaltyPointsDto()
    {
        return PosPaymentLoyaltyPointsDto.builder()
                        .withCollectOrderId("collectOrderId")
                        .withOID("123.456")
                        .withAmount(new BigDecimal("12.56"))
                        .withCurrency(Currency.PEN)
                        .withExchangeRate(BigDecimal.ONE)
                        .build();
    }

    private PosPaymentElectronicDto getPaymentElectronicDto()
    {
        return PosPaymentElectronicDto.builder()
                        .withCollectOrderId("collectOrderId")
                        .withOID("123.456")
                        .withAmount(new BigDecimal("12.56"))
                        .withCurrency(Currency.PEN)
                        .withExchangeRate(BigDecimal.ONE)
                        .withMappingKey("MAPPINGKEY")
                        .withServiceProvider("IZIPP")
                        .withEquipmentIdent("08-15")
                        .withAuthorization("888888")
                        .withOperationId("12365465")
                        .withOperationDateTime(OffsetDateTime.now())
                        .withCardLabel("VISA")
                        .withCardNumber("1234657898987")
                        .withInfo("the info field")
                        .build();
    }

    @Test
    public void testPaymentCash()
        throws JsonProcessingException
    {

        final var dto = getPaymentCashDto();
        final var objectMapper = getObjectMapper();
        final var json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(dto);
        System.out.println(json);
        final var payment = objectMapper.readValue(json, IPosPaymentDto.class);
        System.out.println(payment);
        assertTrue(payment instanceof PosPaymentCashDto);
        assertEquals(payment.getType(), PaymentType.CASH);

        final var paymentDto = (PosPaymentCashDto) payment;
        assertEquals(paymentDto.getType(), paymentDto.getType());
        assertEquals(paymentDto.getAmount(), paymentDto.getAmount());
        assertEquals(paymentDto.getCurrency(), paymentDto.getCurrency());
        assertEquals(paymentDto.getExchangeRate(), paymentDto.getExchangeRate());
    }

    @Test
    public void testPaymentLoyaltyPoints()
        throws JsonProcessingException
    {

        final var dto = getPaymentLoyaltyPointsDto();
        final var objectMapper = getObjectMapper();
        final var json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(dto);
        System.out.println(json);
        final var payment = objectMapper.readValue(json, IPosPaymentDto.class);
        System.out.println(payment);
        assertTrue(payment instanceof PosPaymentLoyaltyPointsDto);
        assertEquals(payment.getType(), PaymentType.LOYALTY_POINTS);

        final var paymentDto = (PosPaymentLoyaltyPointsDto) payment;
        assertEquals(paymentDto.getType(), paymentDto.getType());
        assertEquals(paymentDto.getAmount(), paymentDto.getAmount());
        assertEquals(paymentDto.getCurrency(), paymentDto.getCurrency());
        assertEquals(paymentDto.getExchangeRate(), paymentDto.getExchangeRate());
    }

    @Test
    public void testPaymentElectronics()
        throws JsonProcessingException
    {

        final var dto = getPaymentElectronicDto();

        final var objectMapper = getObjectMapper();
        final var json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(dto);
        System.out.println(json);
        final var payment = objectMapper.readValue(json, IPosPaymentDto.class);
        System.out.println(payment);
        assertTrue(payment instanceof PosPaymentElectronicDto);
        assertEquals(payment.getType(), PaymentType.ELECTRONIC);

        final var paymentDto = (PosPaymentElectronicDto) payment;
        assertEquals(paymentDto.getType(), paymentDto.getType());
        assertEquals(paymentDto.getAmount(), paymentDto.getAmount());
        assertEquals(paymentDto.getCurrency(), paymentDto.getCurrency());
        assertEquals(paymentDto.getExchangeRate(), paymentDto.getExchangeRate());
    }

    @Test
    public void testDocument()
        throws JsonProcessingException
    {

        final PosReceiptDto dto = PosReceiptDto.builder()
                        .withId("absgctagd")
                        .withOID("123.15")
                        .withNumber("001-001651")
                        .withStatus(DocStatus.OPEN)
                        .withItems(Collections.singleton(PosDocItemDto.builder().build()))
                        .withPayments(Set.of(getPaymentElectronicDto(), getPaymentCashDto(),
                                        getPaymentLoyaltyPointsDto()))
                        .withNetTotal(new BigDecimal("11.2"))
                        .withCrossTotal(new BigDecimal("13.41"))
                        .withTaxes(Collections.singleton(TaxEntryDto.builder().build()))
                        .build();
        final var objectMapper = getObjectMapper();
        final var json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(dto);
        System.out.println(json);
        final var receiptDto = objectMapper.readValue(json, PosReceiptDto.class);
        System.out.println(receiptDto);
    }
}
