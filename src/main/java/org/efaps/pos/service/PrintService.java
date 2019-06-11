package org.efaps.pos.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintServiceAttributeSet;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.efaps.pos.dto.PrintResponseDto;
import org.efaps.pos.dto.PrinterType;
import org.efaps.pos.entity.AbstractDocument;
import org.efaps.pos.entity.Job;
import org.efaps.pos.entity.Order;
import org.efaps.pos.entity.Printer;
import org.efaps.pos.entity.Workspace.PrintCmd;
import org.efaps.pos.repository.PrinterRepository;
import org.efaps.pos.util.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.data.JsonDataSource;
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

    private final GridFsService gridFsService;

    public PrintService(final ObjectMapper _jacksonObjectMapper,
                        final GridFsService _gridFsService,
                        final PrinterRepository _printerRepository,
                        final DocumentService _documentService) {
        this.jacksonObjectMapper = _jacksonObjectMapper;
        this.gridFsService = _gridFsService;
        this.printerRepository = _printerRepository;
    }

    public byte[] print2Image(final Object _object, final String _reportOid, final Map<String, Object> _parameters)
    {
        byte[] ret = null;
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Creating report for {}", this.jacksonObjectMapper.writeValueAsString(_object));
            }
            ret = print2Image(new ByteArrayInputStream(this.jacksonObjectMapper.writeValueAsBytes(_object)),
                            this.gridFsService.getContent(_reportOid), _parameters);
        } catch (final IllegalStateException | IOException e) {
            LOG.error("Catched", e);
        }
        return ret;
    }

    public byte[] print2Image(final InputStream _json, final InputStream _report, final Map<String, Object> _parameters)
    {
        byte[] ret = null;
        try {
            final Map<String, Object> parameters = new HashMap<>();
            if (MapUtils.isNotEmpty(_parameters)) {
                parameters.putAll(_parameters);
            }
            final JsonDataSource datasource = new JsonDataSource(_json);
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

    public Optional<PrintResponseDto> queue(final Job _job)
    {
        return queue(_job.getPrinterOid(), _job.getReportOid(), Converter.toDto(_job));
    }

    public Optional<PrintResponseDto> queue(final PrintCmd _printCmd, final AbstractDocument<?> _document)
    {
        Object content;
        if (_document instanceof Order) {
            content = Converter.toDto((Order) _document);
        } else {
            content = _document;
        }
        return queue(_printCmd.getPrinterOid(), _printCmd.getReportOid(), content);
    }

    private Optional<PrintResponseDto> queue(final String _printerOid, final String _reportOid, final Object _content)
    {
        Optional<PrintResponseDto> ret;
        final Optional<Printer> printerOpt = this.printerRepository.findById(_printerOid);
        if (printerOpt.isPresent()) {
            final Map<String, Object> parameters = new HashMap<>();
            parameters.put("PRINTER", printerOpt.get().getName());
            if (printerOpt.get().getType().equals(PrinterType.PREVIEW)) {
                final byte[] data = print2Image(_content, _reportOid, parameters);
                final String key = RandomStringUtils.randomAlphabetic(12);
                CACHE.put(key, data);
                ret = Optional.of(PrintResponseDto.builder()
                                .withKey(key)
                                .withPrinter(Converter.toDto(printerOpt.get()))
                                .build());
            } else {
                print(printerOpt.get().getName(), _content, _reportOid, parameters);
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

    public void print(final String _printer, final Object _object, final String _reportOid,
                      final Map<String, Object> _parameters)
    {
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Creating report for {}", this.jacksonObjectMapper.writeValueAsString(_object));
            }
            final Map<String, Object> parameters = new HashMap<>();
            if (MapUtils.isNotEmpty(_parameters)) {
                parameters.putAll(_parameters);
            }
            final JsonDataSource datasource = new JsonDataSource(new ByteArrayInputStream(this.jacksonObjectMapper
                            .writeValueAsBytes(_object)));
            final JasperPrint jasperPrint = JasperFillManager.fillReport(this.gridFsService.getContent(_reportOid),
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
}
