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

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.dto.ReceiptDto;
import org.efaps.pos.entity.Category;
import org.efaps.pos.entity.Pos;
import org.efaps.pos.entity.Product;
import org.efaps.pos.entity.User;
import org.efaps.pos.entity.Workspace;
import org.efaps.pos.respository.ReceiptRepository;
import org.efaps.pos.util.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class SyncService
{

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(SyncService.class);

    private final MongoTemplate mongoTemplate;
    private final EFapsClient eFapsClient;
    private final ReceiptRepository receiptRepository;

    @Autowired
    public SyncService(final MongoTemplate _mongoTemplate,
                       final ReceiptRepository _receiptRepository,
                       final EFapsClient _eFapsClient)
    {
        this.mongoTemplate = _mongoTemplate;
        this.receiptRepository = _receiptRepository;
        this.eFapsClient = _eFapsClient;

    }

    public void syncProducts()
    {
        LOG.info("Syncing Products");
        final List<Product> products = this.eFapsClient.getProducts().stream()
                        .map(dto -> Converter.toEntity(dto))
                        .collect(Collectors.toList());
        final List<Product> existingProducts = this.mongoTemplate.findAll(Product.class);
        existingProducts.forEach(existing -> {
            if (!products.stream().filter(product -> product.getOid().equals(existing.getOid())).findFirst()
                            .isPresent()) {
                this.mongoTemplate.remove(existing);
            }
        });
        products.forEach(product -> this.mongoTemplate.save(product));
    }

    public void syncCategories()
    {
        LOG.info("Syncing Categories");
        final List<Category> categories = this.eFapsClient.getCategories().stream()
                        .map(dto -> Converter.toEntity(dto))
                        .collect(Collectors.toList());
        final List<Category> existingCategories = this.mongoTemplate.findAll(Category.class);
        existingCategories.forEach(existing -> {
            if (!categories.stream()
                            .filter(category -> category.getOid().equals(existing.getOid())).findFirst()
                            .isPresent()) {
                this.mongoTemplate.remove(existing);
            }
        });
        categories.forEach(category -> this.mongoTemplate.save(category));
    }

    public void syncWorkspaces()
    {
        LOG.info("Syncing Workspaces");
        final List<Workspace> workspaces = this.eFapsClient.getWorkspaces().stream()
                        .map(dto -> Converter.toEntity(dto))
                        .collect(Collectors.toList());
        final List<Workspace> existingWorkspaces = this.mongoTemplate.findAll(Workspace.class);
        existingWorkspaces.forEach(existing -> {
            if (!workspaces.stream()
                            .filter(workspace -> workspace.getOid().equals(existing.getOid())).findFirst()
                            .isPresent()) {
                this.mongoTemplate.remove(existing);
            }
        });
        workspaces.forEach(workspace -> this.mongoTemplate.save(workspace));
    }

    public void syncPOSs()
    {
        LOG.info("Syncing POSs");
        final List<Pos> poss = this.eFapsClient.getPOSs().stream()
                        .map(dto -> Converter.toEntity(dto))
                        .collect(Collectors.toList());
        final List<Pos> existingPoss = this.mongoTemplate.findAll(Pos.class);
        existingPoss.forEach(existing -> {
            if (!poss.stream()
                            .filter(pos -> pos.getOid().equals(existing.getOid())).findFirst()
                            .isPresent()) {
                this.mongoTemplate.remove(existing);
            }
        });
        poss.forEach(pos -> this.mongoTemplate.save(pos));
    }

    public void syncUsers()
    {
        LOG.info("Syncing Users");
        final List<User> users = this.eFapsClient.getUsers().stream()
                        .map(dto -> Converter.toEntity(dto))
                        .collect(Collectors.toList());
        final List<User> existingUsers = this.mongoTemplate.findAll(User.class);
        existingUsers.forEach(existing -> {
            if (!users.stream()
                            .filter(user -> user.getOid().equals(existing.getOid())).findFirst()
                            .isPresent()) {
                this.mongoTemplate.remove(existing);
            }
        });
        users.forEach(user -> this.mongoTemplate.save(user));
    }

    public void syncReceipts()
    {
        LOG.info("Syncing Receipts");
        final Collection<ReceiptDto> tosync = this.receiptRepository.findByOidIsNull().stream()
                        .map(receipt -> Converter.toReceiptDto(receipt))
                        .collect(Collectors.toList());
        for (final ReceiptDto dto : tosync) {
            LOG.debug("Syncing Receipt: {}", dto);
            final ReceiptDto recDto = this.eFapsClient.postReceipt(dto);
            LOG.debug("received Receipt: {}", recDto);
        }
    }

}
