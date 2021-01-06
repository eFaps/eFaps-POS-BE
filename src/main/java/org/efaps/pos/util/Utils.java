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

package org.efaps.pos.util;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public final class Utils
{

    private Utils()
    {
    }

    public static boolean isOid(final String _value)
    {
        return _value != null && _value.matches("^\\d+\\.\\d+$");
    }

    public static LocalDateTime toLocal(final OffsetDateTime _offsetDateTime) {
        LocalDateTime ret = null;
        if (_offsetDateTime != null) {
            ret = _offsetDateTime.withOffsetSameInstant(ZonedDateTime.now().getOffset()).toLocalDateTime();
        }
        return ret;
    }

    public static OffsetDateTime toOffset(final LocalDateTime _localDateTime) {
        OffsetDateTime ret = null;
        if (_localDateTime != null) {
            ret = _localDateTime.atOffset(ZonedDateTime.now().getOffset()).withOffsetSameInstant(ZoneOffset.UTC);
        }
        return ret;
    }
}
