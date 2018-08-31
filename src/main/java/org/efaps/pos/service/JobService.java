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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.efaps.pos.dto.PrintTarget;
import org.efaps.pos.entity.AbstractDocument.Item;
import org.efaps.pos.entity.Category;
import org.efaps.pos.entity.Job;
import org.efaps.pos.entity.Order;
import org.efaps.pos.entity.Printer;
import org.efaps.pos.entity.Product;
import org.efaps.pos.entity.Workspace;
import org.efaps.pos.entity.Workspace.PrintCmd;
import org.efaps.pos.respository.CategoryRepository;
import org.efaps.pos.respository.JobRepository;
import org.efaps.pos.respository.PrinterRepository;
import org.springframework.stereotype.Service;

@Service
public class JobService
{

    private final JobRepository jobRepository;
    private final CategoryRepository categoryRepository;
    private final PrinterRepository printerRepository;
    private final ProductService productService;

    public JobService(final JobRepository _jobRepository,
                      final CategoryRepository _categoryRepository,
                      final PrinterRepository _printerRepository,
                      final ProductService _productService)
    {
        this.jobRepository = _jobRepository;
        this.categoryRepository = _categoryRepository;
        this.printerRepository = _printerRepository;
        this.productService = _productService;
    }

    /**
     * Creates a set of jobs by grouping them by their related printers.
     * @param _workspace
     * @param _order the order
     * @return the collection of jobs
     */
    public Collection<Job> createJobs(final Workspace _workspace, final Order _order)
    {
        final List<Job> ret = new ArrayList<>();
        final Map<String, Set<Item>> map = new HashMap<>();
        final Map<String, String> reportmap = new HashMap<>();
        for (final Item item : getNewItems(_order)) {
            final Product product = this.productService.getProduct(item.getProductOid());
            if (product != null) {
                for (final String catOid : product.getCategoryOids()) {
                    final Optional<Category> catOpt = this.categoryRepository.findById(catOid);
                    if (catOpt.isPresent()) {
                        final Set<PrintCmd> cmds = _workspace.getPrintCmds().stream()
                            .filter(printCmd -> PrintTarget.JOB.equals(printCmd.getTarget())
                                            && catOid.equals(printCmd.getTargetOid()))
                            .collect(Collectors.toSet());
                        for (final PrintCmd cmd : cmds) {
                            final Optional<Printer> printerOpt = this.printerRepository.findById(cmd.getPrinterOid());
                            if (printerOpt.isPresent()) {
                                final Printer printer = printerOpt.get();
                                Set<Item> items;
                                if (map.containsKey(printer.getOid())) {
                                    items = map.get(printer.getOid());
                                } else {
                                    items = new HashSet<>();
                                }
                                items.add(item);
                                map.put(printer.getOid(), items);
                                reportmap.put(printer.getOid(), cmd.getReportOid());
                            }
                        }
                    }
                }
            }
        }
        for (final Entry<String, Set<Item>> entry : map.entrySet()) {
            ret.add(this.jobRepository.save(new Job()
                            .setDocumentId(_order.getId())
                            .setPrinterOid(entry.getKey())
                            .setReportOid(reportmap.get(entry.getKey()))
                            .setItems(entry.getValue())));
        }
        return ret;
    }

    /**
     * Gets the new items. Not added to a job yet.
     *
     * @param _order the order
     * @return the new items
     */
    protected Collection<Item> getNewItems(final Order _order) {
        final List<Job> jobs = this.jobRepository.findByDocumentId(_order.getId());
        final Set<Integer> indexes = jobs.stream()
                        .flatMap(job -> job.getItems().stream()
                                        .map(item -> item.getIndex()))
                        .collect(Collectors.toSet());
        return _order.getItems().stream()
                       .filter(item -> !indexes.contains(item.getIndex()))
                       .collect(Collectors.toSet());
    }
}
