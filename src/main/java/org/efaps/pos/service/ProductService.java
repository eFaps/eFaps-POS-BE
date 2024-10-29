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
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.config.ConfigProperties;
import org.efaps.pos.dto.ProductRelationType;
import org.efaps.pos.dto.ProductType;
import org.efaps.pos.entity.Product;
import org.efaps.pos.entity.SyncInfo;
import org.efaps.pos.repository.ProductRepository;
import org.efaps.pos.util.Converter;
import org.efaps.pos.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ProductService
{

    private static final Logger LOG = LoggerFactory.getLogger(ProductService.class);

    private final ObjectMapper objectMapper;
    private final ConfigProperties configProperties;
    private final ProductRepository productRepository;
    private final EFapsClient eFapsClient;

    @Autowired
    public ProductService(final ObjectMapper objectMapper,
                          final ConfigProperties _configProperties,
                          final ProductRepository _productRepository,
                          final EFapsClient eFapsClient)
    {
        this.objectMapper = objectMapper;
        configProperties = _configProperties;
        productRepository = _productRepository;
        this.eFapsClient = eFapsClient;
    }

    public Page<Product> getProducts(Pageable pageable)
    {
        return productRepository.findAll(pageable);
    }

    public Product getProduct(final String _oid)
    {
        return _oid == null ? null : productRepository.findById(_oid).orElse(null);
    }

    public List<Product> findProducts(final String _term,
                                      boolean textSearch)
    {
        return textSearch
                        ? productRepository
                                        .findText(_term, PageRequest.of(0,
                                                        configProperties.getBeInst().getMaxSearchResult()))
                                        .toList()
                        : productRepository
                                        .find(_term, PageRequest.of(0,
                                                        configProperties.getBeInst().getMaxSearchResult()))
                                        .toList();
    }

    public List<Product> findProductsByCategory(final String _categoryOid)
    {
        return productRepository.findByCategoryOid(_categoryOid);
    }

    public List<Product> findProductsByBarcode(final String _barcode)
    {
        return productRepository.findByBarcode(_barcode);
    }

    public List<Product> findProductsBySku(final String sku)
    {
        return productRepository.findBySku(sku);
    }

    public List<Product> findProductsByType(final ProductType _type)
    {
        return productRepository.findByType(_type);
    }

    public Pair<BigDecimal, BigDecimal> evalPrices(final Product product)
    {

        var netPrice = product.getNetPrice();
        var crossPrice = product.getCrossPrice();
        if (ProductType.BATCH.equals(product.getType()) && product.getRelations() != null) {
            final var relOpt = product.getRelations().stream()
                            .filter(relation -> ProductRelationType.BATCH.equals(relation.getType()))
                            .findFirst();
            if (relOpt.isPresent()) {
                final var baseProduct = getProduct(relOpt.get().getProductOid());
                if (baseProduct != null) {
                    netPrice = baseProduct.getNetPrice();
                    crossPrice = baseProduct.getCrossPrice();
                }
            }
        }
        return Pair.of(netPrice, crossPrice);
    }

    public void syncAllProducts()
    {
        final var dumpDto = eFapsClient.getProductDump();
        if (dumpDto != null) {
            LOG.info("Syncing All Products using dump");
            final var checkout = eFapsClient.checkout(dumpDto.getOid());
            try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(checkout.getContent()))) {
                var zipEntry = zis.getNextEntry();
                var i = 0;
                while (zipEntry != null) {
                    LOG.info("Reading products from: {}", zipEntry.getName());
                    final var writer = new StringWriter();
                    IOUtils.copy(zis, writer, "UTF-8");
                    final var products = objectMapper.readValue(writer.toString(), new TypeReference<List<Product>>()
                    {
                    });
                    LOG.info("Loaded products {} - {} ", i, i + products.size());
                    i += products.size();
                    products.forEach(this::saveToDatabase);
                    zipEntry = zis.getNextEntry();
                }
                zis.closeEntry();
                zis.close();
            } catch (final IOException e) {
                LOG.error("Catched", e);
            }
            syncProducts(new SyncInfo().setLastSync(Utils.toLocal(dumpDto.getUpdateAt())));
        } else {
            LOG.info("Syncing All Products");
            final var allProducts = new ArrayList<Product>();
            final var limit = configProperties.getEFaps().getProductLimit();
            var next = true;
            var i = 0;
            while (next) {
                final var offset = i * limit;
                LOG.info("    Products Batch {} - {}", offset, offset + limit);
                final List<Product> products = eFapsClient.getProducts(limit, offset, null).stream()
                                .map(Converter::toEntity)
                                .collect(Collectors.toList());
                allProducts.addAll(products);
                i++;
                next = !(products.size() < limit);
                products.forEach(this::saveToDatabase);
            }
            if (!allProducts.isEmpty()) {
                final List<Product> existingProducts = productRepository.findAll();
                existingProducts.forEach(existing -> {
                    if (!allProducts.stream().filter(product -> product.getOid().equals(existing.getOid())).findFirst()
                                    .isPresent()) {
                        productRepository.delete(existing);
                    }
                });
            }
        }
    }

    public boolean syncProducts(final SyncInfo syncInfo)
    {
        LOG.info("Syncing Products");
        boolean ret = false;
        final List<Product> allProducts = new ArrayList<>();
        if (syncInfo != null) {
            final var after = OffsetDateTime.of(syncInfo.getLastSync(), ZoneOffset.of("-5")).minusMinutes(10);
            final var limit = configProperties.getEFaps().getProductLimit();
            var next = true;
            var i = 0;
            while (next) {
                final var offset = i * limit;
                LOG.info("    Products Batch {} - {}", offset, offset + limit);
                final List<Product> products = eFapsClient.getProducts(limit, offset, after).stream()
                                .map(Converter::toEntity)
                                .collect(Collectors.toList());
                allProducts.addAll(products);
                i++;
                next = !(products.size() < limit);
                for (final var product : products) {
                    saveToDatabase(product);
                }
            }
            ret = true;
        }
        allProducts.forEach(this::validateParent);
        return ret;
    }

    private void saveToDatabase(final Product product)
    {
        if (configProperties.getBeInst().isSkipProductsMissingPrice()) {
            final var netPrice = evalPrices(product).getLeft();
            if (netPrice != null && BigDecimal.ZERO.compareTo(netPrice) < 0) {
                productRepository.save(product);
            }
        } else {
            productRepository.save(product);
        }
    }

    private void validateParent(final Product product)
    {
        if (ProductType.BATCH.equals(product.getType())) {
            final var parentProductOpt = product.getRelations().stream()
                            .filter(rel -> ProductRelationType.BATCH.equals(rel.getType())).findFirst();
            if (parentProductOpt.isPresent()) {
                final var parent = getProduct(parentProductOpt.get().getProductOid());
                if (parent != null) {
                    if (parent.getRelations().stream()
                                    .filter(rel -> (ProductRelationType.BATCH.equals(rel.getType())
                                                    && rel.getProductOid().equals(product.getOid())))
                                    .findAny().isEmpty()) {
                        final var updatedProduct = eFapsClient.getProduct(parent.getOid());
                        productRepository.save(Converter.toEntity(updatedProduct));
                    }
                }
            }
        }

    }
}
