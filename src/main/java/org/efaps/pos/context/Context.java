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
package org.efaps.pos.context;

import org.efaps.pos.config.ConfigProperties.Company;

public class Context
{

    private static final ThreadLocal<Context> CONTEXT = ThreadLocal.withInitial(() -> new Context());

    private Company company;

    public Company getCompany()
    {
        return company;
    }

    public void setCompany(final Company _company)
    {
        company = _company;
    }

    public static Context get()
    {
        return CONTEXT.get();
    }

}
