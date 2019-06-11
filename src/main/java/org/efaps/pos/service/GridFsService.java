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

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

@Service
public class GridFsService
{

    private final MongoDbFactory dbFactory;
    private final GridFsTemplate gridFsTemplate;

    @Autowired
    public GridFsService(final GridFsTemplate _gridFsTemplate,
                        final MongoDbFactory _dbFactory)
    {
        this.gridFsTemplate = _gridFsTemplate;
        this.dbFactory = _dbFactory;
    }

    public InputStream getContent(final String _oid)
                    throws IllegalStateException, IOException
    {
        final GridFSFile imageFile = this.gridFsTemplate.findOne(new Query(Criteria.where("metadata.oid").is(_oid)));
        return new GridFsResource(imageFile, getGridFs().openDownloadStream(imageFile.getId()))
                                .getInputStream();
    }

    public Object[] getBlob(final String _oid)
        throws IllegalStateException, IOException
    {
        final GridFSFile imageFile = this.gridFsTemplate.findOne(new Query(Criteria.where("metadata.oid").is(_oid)));
        final InputStream resource = new GridFsResource(imageFile, getGridFs().openDownloadStream(imageFile.getId()))
                        .getInputStream();
        return new Object[] { imageFile.getMetadata().getString("contentType"), IOUtils.toByteArray(resource) };
    }

    private GridFSBucket getGridFs()
    {
        final MongoDatabase db = this.dbFactory.getDb();
        return GridFSBuckets.create(db);
    }
}
