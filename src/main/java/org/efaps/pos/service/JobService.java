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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
import org.efaps.pos.repository.CategoryRepository;
import org.efaps.pos.repository.JobRepository;
import org.efaps.pos.repository.PrinterRepository;
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
        jobRepository = _jobRepository;
        categoryRepository = _categoryRepository;
        printerRepository = _printerRepository;
        productService = _productService;
    }

    /**
     * Creates a set of jobs by grouping them by their related printers.
     *
     * @param workspace
     * @param order the order
     * @return the collection of jobs
     */
    public Collection<Job> createJobs(final Workspace workspace,
                                      final Order order)
    {
        final List<Job> ret = new ArrayList<>();
        final Map<String, List<Item>> map = new HashMap<>();
        final Map<String, String> reportmap = new HashMap<>();
        for (final Item item : getNewItems(order)) {
            final Product product = productService.getProduct(item.getProductOid());
            if (product != null) {
                if (filterByCategory(workspace)) {
                    for (final var prod2cat : product.getCategories()) {
                        final Optional<Category> catOpt = categoryRepository.findById(prod2cat.getCategoryOid());
                        if (catOpt.isPresent()) {
                            final Set<PrintCmd> cmds = workspace.getPrintCmds().stream()
                                            .filter(printCmd -> PrintTarget.JOB.equals(printCmd.getTarget())
                                                            && prod2cat.getCategoryOid()
                                                                            .equals(printCmd.getTargetOid()))
                                            .collect(Collectors.toSet());
                            for (final PrintCmd cmd : cmds) {
                                final Optional<Printer> printerOpt = printerRepository.findById(cmd.getPrinterOid());
                                if (printerOpt.isPresent()) {
                                    final Printer printer = printerOpt.get();
                                    List<Item> items;
                                    if (map.containsKey(printer.getOid())) {
                                        items = map.get(printer.getOid());
                                    } else {
                                        items = new ArrayList<>();
                                    }
                                    items.add(item);
                                    map.put(printer.getOid(), items);
                                    reportmap.put(printer.getOid(), cmd.getReportOid());
                                }
                            }
                        }
                    }
                } else {
                    final var printCmdOpt = workspace.getPrintCmds().stream()
                                    .filter(printCmd -> PrintTarget.JOB.equals(printCmd.getTarget())).findFirst();
                    if (printCmdOpt.isPresent()) {
                        final var printCmd = printCmdOpt.get();
                        final Optional<Printer> printerOpt = printerRepository.findById(printCmd.getPrinterOid());
                        if (printerOpt.isPresent()) {
                            final Printer printer = printerOpt.get();
                            List<Item> items;
                            if (map.containsKey(printer.getOid())) {
                                items = map.get(printer.getOid());
                            } else {
                                items = new ArrayList<>();
                            }
                            items.add(item);
                            map.put(printer.getOid(), items);
                            reportmap.put(printer.getOid(), printCmd.getReportOid());
                        }
                    }

                }
            }
        }
        for (final Entry<String, List<Item>> entry : map.entrySet()) {
            ret.add(jobRepository.save(new Job()
                            .setDocumentId(order.getId())
                            .setShoutout(order.getShoutout())
                            .setPrinterOid(entry.getKey())
                            .setReportOid(reportmap.get(entry.getKey()))
                            .setItems(entry.getValue())));
        }
        return ret;
    }

    protected boolean filterByCategory(final Workspace workspace)
    {
        return workspace.getPrintCmds().stream()
                        .anyMatch(printCmd -> PrintTarget.JOB.equals(printCmd.getTarget())
                                        && printCmd.getTargetOid() != null);
    }

    /**
     * Gets the new items. Not added to a job yet.
     *
     * @param _order the order
     * @return the new items
     */
    protected Collection<Item> getNewItems(final Order order)
    {
        final List<Job> jobs = jobRepository.findByDocumentId(order.getId());
        final Set<Integer> indexes = jobs.stream()
                        .flatMap(job -> job.getItems().stream()
                                        .map(Item::getIndex))
                        .collect(Collectors.toSet());
        return order.getItems().stream()
                        .filter(item -> !indexes.contains(item.getIndex()))
                        .collect(Collectors.toSet());
    }
}
