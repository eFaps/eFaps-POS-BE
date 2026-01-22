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

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintServiceAttributeSet;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.efaps.pos.config.ConfigProperties;
import org.efaps.pos.dto.DocType;
import org.efaps.pos.dto.PrintEmployeeRelationDto;
import org.efaps.pos.dto.PrintPayableDto;
import org.efaps.pos.dto.PrintResponseDto;
import org.efaps.pos.entity.AbstractDocument;
import org.efaps.pos.entity.AbstractPayableDocument;
import org.efaps.pos.entity.CreditNote;
import org.efaps.pos.entity.Invoice;
import org.efaps.pos.entity.Job;
import org.efaps.pos.entity.Order;
import org.efaps.pos.entity.Printer;
import org.efaps.pos.entity.Receipt;
import org.efaps.pos.entity.Ticket;
import org.efaps.pos.entity.Workspace.PrintCmd;
import org.efaps.pos.listener.IPrintListener;
import org.efaps.pos.repository.PrinterRepository;
import org.efaps.pos.util.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.data.JsonQLDataSource;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimplePrintServiceExporterConfiguration;

@Service
public class PrintService
{

    private static final Logger LOG = LoggerFactory.getLogger(PrintService.class);

    private static Cache<String, byte[]> CACHE = Caffeine.newBuilder()
                    .expireAfterWrite(10, TimeUnit.MINUTES)
                    .build();

    private final ObjectMapper jacksonObjectMapper;
    private final PrinterRepository printerRepository;
    private final ConfigProperties configProperties;
    private final GridFsService gridFsService;
    private final DocumentHelperService documentHelperService;
    private final PromotionService promotionService;
    private final EmployeeService employeeService;
    private final List<IPrintListener> printListeners;

    public PrintService(final ObjectMapper _jacksonObjectMapper,
                        final GridFsService _gridFsService,
                        final ConfigProperties _configProperties,
                        final PrinterRepository _printerRepository,
                        final DocumentHelperService documentHelperService,
                        final PromotionService promotionService,
                        final EmployeeService employeeService,
                        final Optional<List<IPrintListener>> _printListeners)
    {
        jacksonObjectMapper = _jacksonObjectMapper;
        gridFsService = _gridFsService;
        configProperties = _configProperties;
        printerRepository = _printerRepository;
        this.documentHelperService = documentHelperService;
        this.promotionService = promotionService;
        this.employeeService = employeeService;
        printListeners = _printListeners.isPresent() ? _printListeners.get() : Collections.emptyList();
        LOG.info("Discovered {} IPrintListener", printListeners.size());
    }

    public byte[] print2Image(final Object _object,
                              final String _reportOid,
                              final Map<String, Object> _parameters)
    {
        byte[] ret = null;
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Creating report for {}", jacksonObjectMapper.writerWithDefaultPrettyPrinter()
                                .writeValueAsString(_object));
            }
            ret = print2Image(new ByteArrayInputStream(jacksonObjectMapper.writeValueAsBytes(_object)),
                            gridFsService.getContent(_reportOid), _parameters);
        } catch (final IllegalStateException | IOException e) {
            LOG.error("Catched", e);
        }
        return ret;
    }

    public byte[] print2Image(final InputStream _json,
                              final InputStream _report,
                              final Map<String, Object> _parameters)
    {
        byte[] ret = null;
        try {
            final Map<String, Object> parameters = new HashMap<>();
            if (MapUtils.isNotEmpty(_parameters)) {
                parameters.putAll(_parameters);
            }
            final JsonQLDataSource datasource = new JsonQLDataSource(_json);
            final JasperPrint jasperPrint = JasperFillManager.fillReport(_report, parameters,
                            datasource);
            final Image image = JasperPrintManager.printPageToImage(jasperPrint, 0, 1);
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write((RenderedImage) image, "png", baos);
            ret = baos.toByteArray();
        } catch (JRException | IOException e) {
            LOG.error("Catched", e);
        }
        return ret;
    }

    public Optional<PrintResponseDto> queue(final Job job)
    {
        return queue(job.getPrinterOid(), job.getReportOid(), Converter.toDto(job));
    }

    public Optional<PrintResponseDto> queue(final PrintCmd printCmd,
                                            final AbstractDocument<?> document)
    {
        Object content;
        if (document instanceof Order) {
            content = Converter.toPrintDto((Order) document);
        } else if (document instanceof AbstractPayableDocument) {
            final Map<String, Object> additionalInfo = new HashMap<>();
            for (final IPrintListener listener : printListeners) {
                listener.addAdditionalInfo2Document(document, additionalInfo);
            }
            final Optional<Order> orderOpt = documentHelperService
                            .getOrder4Payable((AbstractPayableDocument<?>) document);
            final var promoInfo = promotionService.getPromotionInfoForDocument(document.getId());

            final var bldr = PrintPayableDto.builder()
                            .withTime(LocalTime.ofInstant(
                                            document.getCreatedDate() == null ? Instant.now()
                                                            : document.getCreatedDate(),
                                            ZoneId.of(configProperties.getBeInst().getTimeZone())))
                            .withOrder(orderOpt.isEmpty() ? null : Converter.toPrintDto(orderOpt.get()))
                            .withPromoInfo(promoInfo)
                            .withAmountInWords(getWordsForAmount(document.getCrossTotal()))
                            .withAdditionalInfo(additionalInfo)
                            .withEmployees(evalEmloyees(document));

            if (document instanceof Receipt) {
                bldr.withPayableType(DocType.RECEIPT)
                                .withPayable(Converter.toDto((Receipt) document));
            } else if (document instanceof Invoice) {
                bldr.withPayableType(DocType.INVOICE)
                                .withPayable(Converter.toDto((Invoice) document));
            } else if (document instanceof Ticket) {
                bldr.withPayableType(DocType.TICKET)
                                .withPayable(Converter.toDto((Ticket) document));

            } else if (document instanceof CreditNote) {
                bldr.withPayableType(DocType.CREDITNOTE)
                                .withPayable(Converter.toDto((CreditNote) document));
            }
            content = bldr.build();
        } else {
            content = document;
        }
        return queue(printCmd.getPrinterOid(), printCmd.getReportOid(), content);
    }

    protected Set<PrintEmployeeRelationDto> evalEmloyees(AbstractDocument<?> document)
    {
        Set<PrintEmployeeRelationDto> ret;
        if (document.getEmployeeRelations() != null && !document.getEmployeeRelations().isEmpty()) {
            ret = document.getEmployeeRelations().stream()
                            .map(entry -> {
                                final var employeeOpt = employeeService.getEmployee(entry.getEmployeeOid());
                                PrintEmployeeRelationDto dto;
                                if (employeeOpt.isPresent()) {
                                    dto = PrintEmployeeRelationDto.builder()
                                                    .withType(entry.getType())
                                                    .withEmployee(Converter.toDto(employeeOpt.get()))
                                                    .build();
                                } else {
                                    dto = null;
                                }
                                return dto;
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());
        } else {
            ret = Collections.emptySet();
        }
        return ret;
    }

    protected String getWordsForAmount(final BigDecimal amount)
    {
        final var num2wrdCvtr = org.efaps.number2words.Converter.getMaleConverter(new Locale("es"));
        return num2wrdCvtr.convert(amount.longValue());
    }

    public Optional<PrintResponseDto> queue(final String printerOid,
                                            final String reportOid,
                                            final Object content)
    {
        Optional<PrintResponseDto> ret;
        final Optional<Printer> printerOpt = printerRepository.findById(printerOid);
        if (printerOpt.isPresent()) {
            final Map<String, Object> parameters = new HashMap<>();
            parameters.put("PRINTER", printerOpt.get().getName());

            switch (printerOpt.get().getType()) {
                case PREVIEW: {
                    final byte[] data = print2Image(content, reportOid, parameters);
                    final String key = RandomStringUtils.insecure().nextAlphabetic(12);
                    CACHE.put(key, data);
                    ret = Optional.of(PrintResponseDto.builder()
                                    .withKey(key)
                                    .withPrinter(Converter.toDto(printerOpt.get()))
                                    .build());
                    break;
                }
                case EXTENSION:
                    printViaExtension(printerOpt.get().getName(), content);
                    ret = Optional.of(PrintResponseDto.builder()
                                    .withPrinter(Converter.toDto(printerOpt.get()))
                                    .build());
                    break;
                case PHYSICAL:
                default:
                    print(printerOpt.get().getName(), content, reportOid, parameters);
                    ret = Optional.of(PrintResponseDto.builder()
                                    .withPrinter(Converter.toDto(printerOpt.get()))
                                    .build());

            }
        } else {
            ret = Optional.empty();
        }
        return ret;
    }

    public byte[] getPreview(final String _key)
    {
        return CACHE.getIfPresent(_key);
    }

    public void print(final String _printer,
                      final Object _object,
                      final String _reportOid,
                      final Map<String, Object> _parameters)
    {
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Creating report for {}", jacksonObjectMapper.writerWithDefaultPrettyPrinter()
                                .writeValueAsString(_object));
            }
            final Map<String, Object> parameters = new HashMap<>();
            if (MapUtils.isNotEmpty(_parameters)) {
                parameters.putAll(_parameters);
            }
            final JsonQLDataSource datasource = new JsonQLDataSource(new ByteArrayInputStream(jacksonObjectMapper
                            .writeValueAsBytes(_object)));
            final JasperPrint jasperPrint = JasperFillManager.fillReport(gridFsService.getContent(_reportOid),
                            parameters, datasource);

            final JRPrintServiceExporter exporter = new JRPrintServiceExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            final SimplePrintServiceExporterConfiguration configuration = new SimplePrintServiceExporterConfiguration();
            final javax.print.PrintService[] services = PrintServiceLookup.lookupPrintServices(null,
                            new HashPrintServiceAttributeSet());
            boolean set = false;
            for (final javax.print.PrintService service : services) {
                LOG.debug("Checking for selection {} ", service);
                if (service.getName().equals(_printer)) {
                    configuration.setPrintService(service);
                    set = true;
                    LOG.debug("Selected {} ", service);
                    break;
                }
            }
            if (!set && services.length > 0) {
                configuration.setPrintService(services[0]);
            }
            exporter.setConfiguration(configuration);
            exporter.exportReport();
        } catch (IllegalStateException | JRException | IOException e) {
            LOG.error("Catched", e);
        }
    }

    public void printViaExtension(final String identifier,
                                  final Object object)
    {
        for (final IPrintListener listener : printListeners) {
            listener.print(identifier, object);
        }
    }
}
