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

import org.apache.commons.io.IOUtils;
import org.efaps.pos.error.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;

@Service
public class GridFsService
{

    private final MongoDatabaseFactory dbFactory;
    private final GridFsTemplate gridFsTemplate;

    @Autowired
    public GridFsService(final GridFsTemplate _gridFsTemplate,
                        final MongoDatabaseFactory _dbFactory)
    {
        gridFsTemplate = _gridFsTemplate;
        dbFactory = _dbFactory;
    }

    public InputStream getContent(final String oid)
        throws IllegalStateException, IOException
    {
        final GridFSFile imageFile = gridFsTemplate.findOne(new Query(Criteria.where("metadata.oid").is(oid)));
        return new GridFsResource(imageFile, getGridFs().openDownloadStream(imageFile.getId()))
                                .getInputStream();
    }

    public Object[] getBlob(final String oid)
        throws IllegalStateException, IOException, NotFoundException
    {
        final GridFSFile imageFile = gridFsTemplate.findOne(new Query(Criteria.where("metadata.oid").is(oid)));
        if (imageFile == null) {
            throw new NotFoundException("Coould not get image for oid: " + oid);
        }
        final InputStream resource = new GridFsResource(imageFile, getGridFs().openDownloadStream(imageFile.getId()))
                        .getInputStream();
        return new Object[] { imageFile.getMetadata().getString("contentType"), IOUtils.toByteArray(resource) };
    }

    public GridFSFile getGridFSFileByName(final String fileName)
    {
        return gridFsTemplate.findOne(query(whereFilename().is(fileName)));
    }

    private GridFSBucket getGridFs()
    {
        final MongoDatabase db = dbFactory.getMongoDatabase();
        return GridFSBuckets.create(db);
    }
}
