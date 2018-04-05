package org.efaps.pos.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.efaps.pos.entity.Product;
import org.efaps.pos.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class DemoService
{

    private static final Logger LOG = LoggerFactory.getLogger(DemoService.class);

    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public DemoService(final MongoTemplate _mongoTemplate,
                       final ObjectMapper _objectMapper)
    {
        this.mongoTemplate = _mongoTemplate;
        this.objectMapper = _objectMapper;
    }

    public void init()
    {
        try {
            clean(User.class, Product.class);
            init("users.json", new TypeReference<List<User>>(){});
            init("products.json", new TypeReference<List<Product>>(){});
        } catch (final IOException e) {
            LOG.error("Errors during init", e);
        }
    }

    private void clean(final Class<?>... _classes)
    {
        for (final Class<?> clazz : _classes) {
            this.mongoTemplate.remove(new Query(), clazz);
        }
    }

    private void init(final String _fileName, @SuppressWarnings("rawtypes") final TypeReference _valueTypeRef)
        throws JsonParseException, JsonMappingException, IOException
    {
        final ClassLoader classLoader = getClass().getClassLoader();
        final File file = new File(classLoader.getResource(_fileName).getFile());
        final List<?> values = this.objectMapper.readValue(file, _valueTypeRef);
        values.forEach(value -> this.mongoTemplate.save(value));
    }

}
