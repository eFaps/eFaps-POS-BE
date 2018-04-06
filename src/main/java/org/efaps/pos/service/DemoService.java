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
package org.efaps.pos.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.efaps.pos.entity.Product;
import org.efaps.pos.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

@Service
public class DemoService
{

    private static final Logger LOG = LoggerFactory.getLogger(DemoService.class);

    private final MongoTemplate mongoTemplate;
    private final GridFsTemplate gridFsTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public DemoService(final MongoTemplate _mongoTemplate,
                       final GridFsTemplate _gridFsTemplate,
                       final ObjectMapper _objectMapper)
    {
        this.mongoTemplate = _mongoTemplate;
        this.gridFsTemplate = _gridFsTemplate;
        this.objectMapper = _objectMapper;
    }

    public void init()
    {
        try {
            clean(User.class, Product.class);
            init("users.json", new TypeReference<List<User>>(){});
            init("products.json", new TypeReference<List<Product>>(){});
            loadImages(new String[] { "apple.jpeg", "1234.1" },
                            new String[] { "orange.jpeg", "1234.2" });
        } catch (final IOException e) {
            LOG.error("Errors during init", e);
        }
    }

    private void loadImages(final String[]... _images)
        throws FileNotFoundException
    {
        for (final String[] image : _images) {
            final ClassLoader classLoader = getClass().getClassLoader();
            final File file = new File(classLoader.getResource(image[0]).getFile());
            final DBObject metaData = new BasicDBObject();
            metaData.put("oid", image[1]);
            this.gridFsTemplate.store(new FileInputStream(file), image[0], "image/jpeg", metaData);
        }
    }

    private void clean(final Class<?>... _classes)
    {
        this.gridFsTemplate.delete(new Query());
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
