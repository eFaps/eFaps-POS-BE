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
package org.efaps.pos.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.efaps.pos.config.IApi;
import org.efaps.pos.dto.ContactDto;
import org.efaps.pos.service.ContactService;
import org.efaps.pos.util.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * The Class ProductController.
 */
@RestController
@RequestMapping(IApi.BASEPATH + "contacts")
public class ContactController
{

    private final ContactService service;

    public ContactController(final ContactService _service)
    {
        service = _service;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<ContactDto> getContacts(final Pageable pageable) {
      return service.getContacts(pageable).map(contact -> Converter.toDto(contact));
    }

    @GetMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ContactDto getContact(@PathVariable final String id)
    {
        final var contact = service.findContact(id);
        if (contact == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found");
        }
        return Converter.toDto(contact);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, params = { "term" })
    public List<ContactDto> findContacts(@RequestParam(name = "term") final String _term,
                                         @RequestParam(name = "nameSearch", defaultValue = "false") final Boolean _nameSearch)
    {
        return service.findContacts(_term, _nameSearch).stream()
                        .map(contact -> Converter.toDto(contact))
                        .collect(Collectors.toList());
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ContactDto createContact(@RequestBody final ContactDto _posContactDto)
    {
        return Converter.toDto(service.createContact(Converter.toEntity(_posContactDto)));
    }
}
