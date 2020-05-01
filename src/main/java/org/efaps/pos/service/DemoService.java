/*
 * Copyright 2003 - 2020 The eFaps Team
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

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.List;

import org.efaps.pos.config.DemoConfig;
import org.efaps.pos.entity.Category;
import org.efaps.pos.entity.Contact;
import org.efaps.pos.entity.InventoryEntry;
import org.efaps.pos.entity.Pos;
import org.efaps.pos.entity.Printer;
import org.efaps.pos.entity.Product;
import org.efaps.pos.entity.Sequence;
import org.efaps.pos.entity.User;
import org.efaps.pos.entity.Warehouse;
import org.efaps.pos.entity.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@Service
@Profile(value = { "demo" })
public class DemoService
{

    private static final Logger LOG = LoggerFactory.getLogger(DemoService.class);
    private final DemoConfig config;
    private final MongoTemplate mongoTemplate;
    private final GridFsTemplate gridFsTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public DemoService(final DemoConfig _config,
                       final MongoTemplate _mongoTemplate,
                       final GridFsTemplate _gridFsTemplate,
                       final ObjectMapper _objectMapper)
    {
        config = _config;
        mongoTemplate = _mongoTemplate;
        gridFsTemplate = _gridFsTemplate;
        objectMapper = _objectMapper;
    }

    @SuppressWarnings("unchecked")
    public void init()
    {
        try {
            clean(User.class, Product.class, Workspace.class, Pos.class, Category.class, Sequence.class, Contact.class,
                            InventoryEntry.class, Printer.class, Warehouse.class);
            save(loadDocuments(config.getUsers(), new TypeReference<List<User>>(){}));
            save(loadDocuments(config.getProducts(), new TypeReference<List<Product>>(){}));
            save(loadDocuments(config.getWorkspaces(), new TypeReference<List<Workspace>>(){}));
            save(loadDocuments(config.getPoss(), new TypeReference<List<Pos>>(){}));
            save(loadDocuments(config.getCategories(), new TypeReference<List<Category>>(){}));
            save(loadDocuments(config.getSequences(), new TypeReference<List<Sequence>>(){}));
            save(loadDocuments(config.getContacts(), new TypeReference<List<Contact>>(){}));
            save(loadDocuments(config.getInventory(), new TypeReference<List<InventoryEntry>>(){}));
            save(loadDocuments(config.getPrinters(), new TypeReference<List<Printer>>(){}));
            save(loadDocuments(config.getWarehouses(), new TypeReference<List<Warehouse>>(){}));

            final var files = loadDocuments(config.getFiles(), new TypeReference<List<File>>(){});
            loadFiles(config.getFiles(), (List<File>) files);
        } catch (final IOException e) {
            LOG.error("Errors during init", e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<?> loadDocuments(final String _resourceLocation,
                               @SuppressWarnings("rawtypes") final TypeReference _valueTypeRef)
        throws JsonParseException, JsonMappingException, IOException
    {
        List<?> ret = null;
        if (_resourceLocation != null) {
            final var file = ResourceUtils.getFile(_resourceLocation);
            if (file.exists()) {
                ret = objectMapper.<List<?>>readValue(new FileInputStream(file), _valueTypeRef);
            } else {
                LOG.error("Cannot find: {}", _resourceLocation);
            }
        }
        return ret;
    }

    private void save(final List<?> _values)
    {
        if (_values != null) {
            _values.forEach(value -> mongoTemplate.save(value));
        }
    }

    private void loadFiles(final String _resourceLocation ,final List<File> _files)
        throws IOException
    {
        final var resourceFile = ResourceUtils.getFile(_resourceLocation);
        for (final File fileDoc : _files) {
            final var file  =  ResourceUtils.getFile(resourceFile.getParent() + "/" + fileDoc.getPath());
            final DBObject metaData = new BasicDBObject();
            metaData.put("oid", fileDoc.getOid());
            final String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            metaData.put("contentType", mimeType);
            gridFsTemplate.store(new FileInputStream(file), file.getName(), metaData);
        }
    }

    private void clean(final Class<?>... _classes)
    {
        gridFsTemplate.delete(new Query());
        for (final Class<?> clazz : _classes) {
            mongoTemplate.remove(new Query(), clazz);
        }
    }

    public static class File
    {

        private String oid;
        private String path;

        public String getOid()
        {
            return oid;
        }

        public void setOid(final String _oid)
        {
            oid = _oid;
        }

        public String getPath()
        {
            return path;
        }

        public void setPath(final String path)
        {
            this.path = path;
        }

    }
}
