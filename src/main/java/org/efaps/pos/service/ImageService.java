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

import java.io.ByteArrayInputStream;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;

import org.efaps.pos.client.Checkout;
import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.dto.StoreStatus;
import org.efaps.pos.dto.StoreStatusRequestDto;
import org.efaps.pos.entity.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ImageService
{

    private static final Logger LOG = LoggerFactory.getLogger(ImageService.class);

    private final GridFsService gridFsService;
    private final EFapsClient eFapsClient;
    private final ProductService productService;
    private final WorkspaceService workspaceService;

    public ImageService(final EFapsClient eFapsClient,
                        final GridFsService gridFsService,
                        final ProductService productService,
                        final WorkspaceService workspaceService)
    {
        this.gridFsService = gridFsService;
        this.eFapsClient = eFapsClient;
        this.productService = productService;
        this.workspaceService = workspaceService;
    }

    public void sync()
    {
        LOG.info("Syncing Images");
        final var imageOids = new ArrayList<String>();
        var pageable = Pageable.ofSize(1000);
        while (pageable != null) {
            final var productPage = productService.getProducts(pageable);
            for (final Product product : productPage.getContent()) {
                if (product.getImageOid() != null) {
                    LOG.debug("Marking Product-Image to be synced {}", product.getImageOid());
                    imageOids.add(product.getImageOid());
                }
                for (final var indicationSet : product.getIndicationSets()) {
                    if (indicationSet.getImageOid() != null) {
                        LOG.debug("Marking IndicationSet-Image to be synced {}", indicationSet.getImageOid());
                        imageOids.add(indicationSet.getImageOid());
                    }
                    for (final var indication : indicationSet.getIndications()) {
                        if (indication.getImageOid() != null) {
                            LOG.debug("Marking Indication-Image to be synced {}", indication.getImageOid());
                            imageOids.add(indication.getImageOid());
                        }
                    }
                }

            }
            if (productPage.hasNext()) {
                pageable = productPage.nextPageable();
            } else {
                pageable = null;
            }
        }

        final var workspaces = workspaceService.getWorkspaces();
        for (final var workspace : workspaces) {
            for (final var floor : workspace.getFloors()) {
                LOG.debug("Marking  Floor-Image to be synced {}", floor.getImageOid());
                imageOids.add(floor.getImageOid());
            }
        }

        final var response = eFapsClient.evalStoreStatus(StoreStatusRequestDto.builder()
                        .withOids(imageOids)
                        .build());

        for (final var status : response.getStatus()) {
            if (status.isExisting()) {
                final var file = gridFsService.getGridFSFile(status.getOid());
                // no modification date --> sync always
                if (status.getModifiedAt() == null || file == null) {
                    retrieveImage(status);
                } else if (file.getMetadata().containsKey("modifiedAt")) {
                    final OffsetDateTime local = ((Date) file.getMetadata().get("modifiedAt")).toInstant()
                                    .atOffset(ZoneOffset.UTC);
                    if (!local.withNano(0).equals(status.getModifiedAt().withNano(0))) {
                        retrieveImage(status);
                    }
                } else {
                    retrieveImage(status);
                }
            }
        }
    }

    protected void retrieveImage(final StoreStatus status)
    {
        final Checkout checkout = eFapsClient.checkout(status.getOid());
        if (checkout != null) {
            gridFsService.updateContent(status.getOid(),
                            new ByteArrayInputStream(checkout.getContent()),
                            checkout.getFilename(),
                            checkout.getContentType().toString(),
                            status.getModifiedAt());
        }
    }
}
