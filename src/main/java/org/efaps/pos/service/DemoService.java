/*
 * Copyright 2003 - 2019 The eFaps Team
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

import java.io.IOException;
import java.util.List;

import org.efaps.pos.entity.Category;
import org.efaps.pos.entity.Contact;
import org.efaps.pos.entity.Pos;
import org.efaps.pos.entity.Product;
import org.efaps.pos.entity.Sequence;
import org.efaps.pos.entity.User;
import org.efaps.pos.entity.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
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
            clean(User.class, Product.class, Workspace.class, Pos.class, Category.class, Sequence.class);
            init("users.json", new TypeReference<List<User>>(){});
            init("products.json", new TypeReference<List<Product>>(){});
            init("workspaces.json", new TypeReference<List<Workspace>>(){});
            init("poss.json", new TypeReference<List<Pos>>(){});
            init("categories.json", new TypeReference<List<Category>>(){});
            init("sequences.json", new TypeReference<List<Sequence>>(){});
            init("contacts.json", new TypeReference<List<Contact>>(){});
            loadImages();
        } catch (final IOException e) {
            LOG.error("Errors during init", e);
        }
    }

    private void loadImages()
        throws IOException
    {
        final ClassPathResource resource = new ClassPathResource("images.json");
        final List<Image> images = this.objectMapper.readValue(resource.getInputStream(),
                        new TypeReference<List<Image>>(){});
        for (final Image image : images) {
            final ClassPathResource imgResource = new ClassPathResource("images/" + image.getFileName());
            final DBObject metaData = new BasicDBObject();
            metaData.put("oid", image.getOid());
            metaData.put("contentType", "image/jpeg");
            this.gridFsTemplate.store(imgResource.getInputStream(), image.getFileName(), metaData);
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
        final ClassPathResource resource = new ClassPathResource(_fileName);
        final List<?> values = this.objectMapper.readValue(resource.getInputStream(), _valueTypeRef);
        values.forEach(value -> this.mongoTemplate.save(value));
    }

    public static class Image {
        private String oid;
        private String fileName;

        public String getOid()
        {
            return this.oid;
        }

        public void setOid(final String _oid)
        {
            this.oid = _oid;
        }

        public String getFileName()
        {
            return this.fileName;
        }

        public void setFileName(final String _fileName)
        {
            this.fileName = _fileName;
        }
    }
}
