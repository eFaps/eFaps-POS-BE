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

import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.gridfs.GridFsCriteria.whereFilename;

import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;

import org.apache.commons.io.IOUtils;
import org.efaps.pos.error.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.client.gridfs.model.GridFSFile;

@Service
public class GridFsService
{

    private final GridFsTemplate gridFsTemplate;
    private final GridFsOperations operations;

    @Autowired
    public GridFsService(final GridFsTemplate gridFsTemplate,
                         final GridFsOperations operations)
    {
        this.gridFsTemplate = gridFsTemplate;
        this.operations = operations;
    }

    public InputStream getContent(final String oid)
        throws IllegalStateException, IOException
    {
        final var file = getGridFSFile(oid);
        return operations.getResource(file).getInputStream();
    }

    public Object[] getBlob(final String oid)
        throws IllegalStateException, IOException, NotFoundException
    {
        final GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("metadata.oid").is(oid)));
        if (file == null) {
            throw new NotFoundException("Coould not get image for oid: " + oid);
        }
        final InputStream resource = operations.getResource(file).getInputStream();
        return new Object[] { file.getMetadata().getString("contentType"), IOUtils.toByteArray(resource) };
    }

    public GridFSFile getGridFSFile(final String oid)
    {
        return gridFsTemplate.findOne(new Query(Criteria.where("metadata.oid").is(oid)));
    }

    public GridFSFile getGridFSFileByName(final String fileName)
    {
        return gridFsTemplate.findOne(query(whereFilename().is(fileName)));
    }

    public void updateContent(final String oid,
                              final InputStream content,
                              final String fileName,
                              final String contentType,
                              final OffsetDateTime modifiedAt) {
        gridFsTemplate.delete(new Query(Criteria.where("metadata.oid").is(oid)));
        final var metaData = new BasicDBObject();
        metaData.put("oid", oid);
        metaData.put("modifiedAt", modifiedAt);
        gridFsTemplate.store(content, fileName, contentType, metaData);
    }
}
