package org.efaps.pos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

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

    private final ObjectMapper jacksonObjectMapper;

    public PrintService(final ObjectMapper _jacksonObjectMapper) {
        this.jacksonObjectMapper = _jacksonObjectMapper;
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
}
