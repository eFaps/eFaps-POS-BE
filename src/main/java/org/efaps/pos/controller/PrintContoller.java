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
package org.efaps.pos.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.efaps.pos.config.IApi;
import org.efaps.pos.dto.BalanceSummaryDto;
import org.efaps.pos.dto.PrintResponseDto;
import org.efaps.pos.dto.PrintTarget;
import org.efaps.pos.entity.AbstractDocument;
import org.efaps.pos.entity.Job;
import org.efaps.pos.entity.Order;
import org.efaps.pos.entity.User;
import org.efaps.pos.entity.Workspace;
import org.efaps.pos.service.BalanceService;
import org.efaps.pos.service.DocumentService;
import org.efaps.pos.service.JobService;
import org.efaps.pos.service.PrintService;
import org.efaps.pos.service.ReportService;
import org.efaps.pos.service.WorkspaceService;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(IApi.BASEPATH + "print")
public class PrintContoller
{
    private final WorkspaceService workspaceService;
    private final DocumentService documentService;
    private final BalanceService balanceService;
    private final ReportService reportService;
    private final JobService jobService;
    private final PrintService printService;

    public PrintContoller(final WorkspaceService _workspaceService,
                          final DocumentService _service,
                          final BalanceService _balanceService,
                          final ReportService reportService,
                          final JobService _jobService,
                          final PrintService _printService)
    {
        workspaceService = _workspaceService;
        documentService = _service;
        balanceService = _balanceService;
        this.reportService = reportService;
        jobService = _jobService;
        printService = _printService;
    }

    @GetMapping(path = "/preview/{key}",
                    produces = { MediaType.IMAGE_PNG_VALUE })
    public ResponseEntity<byte[]> getPreview(@PathVariable("key") final String _key)
    {
        final byte[] data = printService.getPreview(_key);
        return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG)
                        .cacheControl(CacheControl.noCache())
                        .body(data);
    }

    @PostMapping(path = "jobs", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PrintResponseDto> printJob(final Authentication _authentication,
                                           @RequestParam(name = "workspaceOid") final String _workspaceOid,
                                           @RequestParam(name = "documentId") final String _documentId)
    {
        final List<PrintResponseDto> ret = new ArrayList<>();
        final Workspace workspace = workspaceService.getWorkspace((User) _authentication.getPrincipal(),
                        _workspaceOid);
        final Order order = documentService.getOrder(_documentId);
        final Collection<Job> jobs = jobService.createJobs(workspace, order);

        for (final Job job : jobs) {
            final Optional<PrintResponseDto> responseOpt = printService.queue(job);
            if (responseOpt.isPresent()) {
                ret.add(responseOpt.get());
            }
        }
        return ret;
    }

    @PostMapping(path = "preliminary", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PrintResponseDto> printPreliminary(final Authentication _authentication,
                                           @RequestParam(name = "workspaceOid") final String _workspaceOid,
                                           @RequestParam(name = "documentId") final String _documentId)
    {
        final List<PrintResponseDto> ret = new ArrayList<>();
        final Workspace workspace = workspaceService.getWorkspace((User) _authentication.getPrincipal(),
                        _workspaceOid);

        final AbstractDocument<?> document = documentService.getDocument(_documentId);

        workspace.getPrintCmds().stream()
            .filter(printCmd -> PrintTarget.PRELIMINARY.equals(printCmd.getTarget()))
            .forEach(printCmd -> {
                final Optional<PrintResponseDto> responseOpt = printService.queue(printCmd, document);
                if (responseOpt.isPresent()) {
                    ret.add(responseOpt.get());
                }
            });
        return ret;
    }

    @PostMapping(path = "copy", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PrintResponseDto> printCopy(final Authentication _authentication,
                                           @RequestParam(name = "workspaceOid") final String _workspaceOid,
                                           @RequestParam(name = "documentId") final String _documentId)
    {
        final List<PrintResponseDto> ret = new ArrayList<>();
        final Workspace workspace = workspaceService.getWorkspace((User) _authentication.getPrincipal(),
                        _workspaceOid);

        final AbstractDocument<?> document = documentService.getDocument(_documentId);

        workspace.getPrintCmds().stream()
            .filter(printCmd -> PrintTarget.COPY.equals(printCmd.getTarget()))
            .forEach(printCmd -> {
                final Optional<PrintResponseDto> responseOpt = printService.queue(printCmd, document);
                if (responseOpt.isPresent()) {
                    ret.add(responseOpt.get());
                }
            });
        return ret;
    }

    @PostMapping(path = "ticket", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PrintResponseDto> printTicket(final Authentication _authentication,
                                           @RequestParam(name = "workspaceOid") final String _workspaceOid,
                                           @RequestParam(name = "documentId") final String _documentId)
    {
        final List<PrintResponseDto> ret = new ArrayList<>();
        final Workspace workspace = workspaceService.getWorkspace((User) _authentication.getPrincipal(),
                        _workspaceOid);

        final AbstractDocument<?> document = documentService.getDocument(_documentId);

        workspace.getPrintCmds().stream()
            .filter(printCmd -> PrintTarget.TICKET.equals(printCmd.getTarget()))
            .forEach(printCmd -> {
                final Optional<PrintResponseDto> responseOpt = printService.queue(printCmd, document);
                if (responseOpt.isPresent()) {
                    ret.add(responseOpt.get());
                }
            });
        return ret;
    }

    @PostMapping(path = "balance", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PrintResponseDto> printBalance(final Authentication authentication,
                                               @RequestParam(name = "workspaceOid") final String workspaceOid,
                                               @RequestParam(name = "balanceId") final String balanceId,
                                               @RequestParam(name = "detailed", required = false) final boolean detailed)
    {
        final List<PrintResponseDto> ret = new ArrayList<>();
        final Workspace workspace = workspaceService.getWorkspace((User) authentication.getPrincipal(),
                        workspaceOid);

        final BalanceSummaryDto summary = balanceService.getSummary(balanceId, detailed);

        workspace.getPrintCmds().stream()
                        .filter(printCmd -> PrintTarget.BALANCE.equals(printCmd.getTarget()))
                        .forEach(printCmd -> {
                            final Optional<PrintResponseDto> responseOpt = printService.queue(printCmd.getPrinterOid(),
                                            printCmd.getReportOid(), summary);
                            if (responseOpt.isPresent()) {
                                ret.add(responseOpt.get());
                            }
                        });
        return ret;
    }

    @PostMapping(path = "sales-report", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PrintResponseDto> printSalesReport(final Authentication authentication,
                                                   @RequestParam(name = "workspaceOid") final String workspaceOid,
                                                   @RequestParam(name = "date") final LocalDate date)
    {
        final List<PrintResponseDto> ret = new ArrayList<>();
        final Workspace workspace = workspaceService.getWorkspace((User) authentication.getPrincipal(), workspaceOid);

        final var content = reportService.getSalesReport(date);

        workspace.getPrintCmds().stream()
                        .filter(printCmd -> PrintTarget.SALESREPORT.equals(printCmd.getTarget()))
                        .forEach(printCmd -> {
                            final Optional<PrintResponseDto> responseOpt = printService.queue(printCmd.getPrinterOid(),
                                            printCmd.getReportOid(), content);
                            if (responseOpt.isPresent()) {
                                ret.add(responseOpt.get());
                            }
                        });
        return ret;
    }

}
