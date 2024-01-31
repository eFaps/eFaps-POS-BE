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

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.efaps.pos.config.IApi;
import org.efaps.pos.service.GridFsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(IApi.BASEPATH + "images")
public class ImageController
{

    private final GridFsService gridFsService;

    @Autowired
    public ImageController(final GridFsService _gridFsService)
    {
        this.gridFsService = _gridFsService;
    }

    @GetMapping(path = "/{oid}",
                    produces = { MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE })
    public ResponseEntity<byte[]> getImage(@PathVariable("oid") final String _oid)
        throws IllegalStateException, IOException
    {
        final Object[] image = this.gridFsService.getBlob(_oid);
        return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType((String) image[0]))
                        .cacheControl(CacheControl.maxAge(24, TimeUnit.HOURS))
                        .body((byte[]) image[1]);
    }
}
