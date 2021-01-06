/*
 * Copyright 2003 - 2020 The eFaps Team
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
package org.efaps.pos.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

public class UtilsTest
{

    @Test
    public void toLocal()
    {
        final var offsetDateTime = OffsetDateTime.of(2020, 8, 10, 15, 30, 55, 120, ZoneOffset.UTC);
        final var local = Utils.toLocal(offsetDateTime);
        final var offset = ZonedDateTime.now().getOffset();
        assertEquals(offsetDateTime.plusSeconds(offset.getTotalSeconds()).getHour(), local.getHour());
    }

    @Test
    public void toOffset()
    {
        final var localDateTime = LocalDateTime.of(2020, 7, 3, 16, 20, 34, 156);
        final var offsetDateTime = Utils.toOffset(localDateTime);
        final var offset = ZonedDateTime.now().getOffset();
        assertEquals(localDateTime.minusSeconds(offset.getTotalSeconds()).getHour(), offsetDateTime.getHour());
    }
}
