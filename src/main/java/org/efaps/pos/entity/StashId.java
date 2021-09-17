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

package org.efaps.pos.entity;

public enum StashId
{
    PRODUCTSYNC("org.efaps.pos.sync.Product"),
    CATEGORYSYNC("org.efaps.pos.sync.Category"),
    CONTACTSYNC("org.efaps.pos.sync.Contact"),
    SEQUENCESYNC("org.efaps.pos.sync.Sequence"),
    IMAGESYNC("org.efaps.pos.sync.Image"),
    TICKETSYNC("org.efaps.pos.sync.Ticket"),
    INVOICESYNC("org.efaps.pos.sync.Invoice"),
    RECEIPTSYNC("org.efaps.pos.sync.Receipt"),
    USERSYNC("org.efaps.pos.sync.User"),
    POSSYNC("org.efaps.pos.sync.POS"),
    PRINTERSYNC("org.efaps.pos.sync.Printer"),
    REPORTSYNC("org.efaps.pos.sync.Report"),
    WAREHOUSESYNC("org.efaps.pos.sync.Warehouse"),
    WORKSPACESYNC("org.efaps.pos.sync.Workspace"),
    BALANCESYNC("org.efaps.pos.sync.Balance"),
    EXCHANGERATES("org.efaps.pos.ExchangeRates");

    private final String key;

    StashId(final String _key)
    {
        key = _key;
    }

    public String getKey()
    {
        return key;
    }

}
