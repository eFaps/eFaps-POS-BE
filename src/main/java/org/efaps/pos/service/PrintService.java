package org.efaps.pos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
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

import org.apache.commons.lang3.RandomStringUtils;
import org.efaps.pos.dto.PrintResponseDto;
import org.efaps.pos.dto.PrinterType;
import org.efaps.pos.entity.Job;
import org.efaps.pos.entity.Printer;
import org.efaps.pos.respository.PrinterRepository;
import org.efaps.pos.util.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.data.JsonDataSource;

@Service
public class PrintService
{
    private static final Logger LOG = LoggerFactory.getLogger(PrintService.class);

    private static Cache<String, byte[]> CACHE = Caffeine.newBuilder()
                    .expireAfterWrite(10, TimeUnit.MINUTES)
                    .build();

    private final ObjectMapper jacksonObjectMapper;

    private final PrinterRepository printerRepository;

    public PrintService(final ObjectMapper _jacksonObjectMapper,
                        final PrinterRepository _printerRepository) {
        this.jacksonObjectMapper = _jacksonObjectMapper;
        this.printerRepository = _printerRepository;
    }

    public byte[] print(final Object _object) {
        byte[] ret = null;
        try {
            ret = print(new ByteArrayInputStream(this.jacksonObjectMapper.writeValueAsBytes(_object)));
        } catch (final JsonProcessingException e) {
            LOG.error("Catched", e);
        }
        return ret;
    }

    public byte[] print(final InputStream _json)
    {
        byte[] ret = null;
        try {
            final ClassPathResource jasper = new ClassPathResource("document.jasper");

            final Map<String, Object> parameters = new HashMap<>();
            final JsonDataSource datasource = new JsonDataSource(_json);
            final JasperPrint jasperPrint = JasperFillManager.fillReport(jasper.getInputStream(), parameters,
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
        Optional<PrintResponseDto> ret;
        final Optional<Printer> printerOpt = this.printerRepository.findById(_job.getPrinterOid());
        if (printerOpt.isPresent()) {
            if (printerOpt.get().getType().equals(PrinterType.PREVIEW)) {
                 final byte[] data = print(_job);
                 final String key = RandomStringUtils.randomAlphabetic(12);
                 CACHE.put(key, data);
                 ret = Optional.of(PrintResponseDto.builder()
                     .withKey(key)
                     .withPrinter(Converter.toDto(printerOpt.get()))
                     .build());
            } else {
                // print to real printer
                ret = Optional.of(PrintResponseDto.builder().build());
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
}
