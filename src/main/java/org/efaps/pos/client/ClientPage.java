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
package org.efaps.pos.client;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class ClientPage<T> extends PageImpl<T> {
    private static final long serialVersionUID = 1L;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public ClientPage(@JsonProperty("content") final List<T> content,
                        @JsonProperty("number") final int number,
                        @JsonProperty("size") final int size,
                        @JsonProperty("totalElements") final Long totalElements,
                        @JsonProperty("pageable") final JsonNode pageable,
                        @JsonProperty("last") final boolean last,
                        @JsonProperty("totalPages") final int totalPages,
                        @JsonProperty("sort") final JsonNode sort,
                        @JsonProperty("first") final boolean first,
                        @JsonProperty("numberOfElements") final int numberOfElements) {
        super(content, PageRequest.of(number, size), totalElements);
    }

    public ClientPage(final List<T> content, final Pageable pageable, final long total) {
        super(content, pageable, total);
    }

    public ClientPage(final List<T> content) {
        super(content);
    }

    public ClientPage() {
        super(new ArrayList<>());
    }
}