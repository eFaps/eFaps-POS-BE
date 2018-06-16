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

package org.efaps.pos.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.efaps.pos.config.IApi;
import org.efaps.pos.dto.PrintResponseDto;
import org.efaps.pos.entity.Job;
import org.efaps.pos.entity.Order;
import org.efaps.pos.entity.User;
import org.efaps.pos.entity.Workspace;
import org.efaps.pos.service.DocumentService;
import org.efaps.pos.service.JobService;
import org.efaps.pos.service.PrintService;
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
    private final JobService jobService;
    private final PrintService printService;

    public PrintContoller(final WorkspaceService _workspaceService,
                          final DocumentService _service,
                          final JobService _jobService,
                          final PrintService _printService)
    {
        this.workspaceService = _workspaceService;
        this.documentService = _service;
        this.jobService = _jobService;
        this.printService = _printService;
    }

    @GetMapping(path = "/preview/{key}",
                    produces = { MediaType.IMAGE_PNG_VALUE })
    public ResponseEntity<byte[]> getPreview(@PathVariable("key") final String _key)
    {
        final byte[] data = this.printService.getPreview(_key);
        return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG)
                        .cacheControl(CacheControl.noCache())
                        .body(data);
    }

    @PostMapping(path = "jobs", produces = MediaType.APPLICATION_JSON_VALUE )
    public List<PrintResponseDto> printJob(final Authentication _authentication,
                                           @RequestParam(name = "workspaceOid") final String _workspaceOid,
                                           @RequestParam(name = "documentId") final String _documentId) {
        final List<PrintResponseDto> ret = new ArrayList<>();
        final Workspace workspace = this.workspaceService.getWorkspace((User) _authentication.getPrincipal(), _workspaceOid);
        final Order order = this.documentService.getOrder(_documentId);
        final Collection<Job> jobs = this.jobService.createJobs(workspace, order);

        for (final Job job : jobs) {
             final Optional<PrintResponseDto> responseOpt = this.printService.queue(job);
             if (responseOpt.isPresent()) {
                 ret.add(responseOpt.get());
             }
        }
        return ret;
    }
}
