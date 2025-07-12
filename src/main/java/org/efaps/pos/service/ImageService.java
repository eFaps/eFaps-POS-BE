package org.efaps.pos.service;

import java.io.ByteArrayInputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.efaps.pos.client.Checkout;
import org.efaps.pos.client.EFapsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@Service
public class ImageService
{

    private static final Logger LOG = LoggerFactory.getLogger(ImageService.class);
    private final Map<String, List<String>> toBeSynced = new LinkedHashMap<>();
    private final GridFsTemplate gridFsTemplate;
    private final EFapsClient eFapsClient;

    public ImageService(final GridFsTemplate gridFsTemplate,
                        final EFapsClient eFapsClient)
    {
        this.gridFsTemplate = gridFsTemplate;
        this.eFapsClient = eFapsClient;
    }

    protected void storeImage(final String imageOid)
    {
        final Checkout checkout = eFapsClient.checkout(imageOid);
        if (checkout != null) {
            gridFsTemplate.delete(new Query(Criteria.where("metadata.oid").is(imageOid)));
            final DBObject metaData = new BasicDBObject();
            metaData.put("oid", imageOid);
            metaData.put("contentType", checkout.getContentType().toString());
            gridFsTemplate.store(new ByteArrayInputStream(checkout.getContent()), checkout.getFilename(),
                            metaData);
        }
    }

    public void sync()
    {
        for (final var entry : toBeSynced.entrySet()) {
            LOG.info("Syncing Images for: {}", entry.getKey());
            entry.getValue().forEach(oid -> {
                LOG.debug("Syncing Image {}", oid);
                storeImage(oid);
            });
        }
        toBeSynced.clear();
    }

    public void registerForSync(final String key,
                                final List<String> imageOids)
    {
        toBeSynced.put(key, imageOids);
    }
}
