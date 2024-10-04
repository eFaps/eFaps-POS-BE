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

import java.io.IOException;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.efaps.pos.config.ConfigProperties;
import org.efaps.pos.config.ConfigProperties.Company;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ContextFilter extends OncePerRequestFilter {
  private static final Logger LOG = LoggerFactory.getLogger(ContextFilter.class);

  private final ConfigProperties configProperties;

  public ContextFilter(final ConfigProperties _configProperties) {
    configProperties = _configProperties;
  }

  @Override
  protected void doFilterInternal(final HttpServletRequest _request, final HttpServletResponse _response,
      final FilterChain _filterChain) throws ServletException, IOException {
    final String companyKey = _request.getHeader("X-CONTEXT-COMPANY");
    if (StringUtils.isNotEmpty(companyKey)) {
      final Optional<Company> compOpt = configProperties.getCompanies()
        .stream()
        .filter(company -> companyKey.equals(company.getKey()))
        .findFirst();
      if (compOpt.isPresent()) {
        final Company company = compOpt.get();
        Context.get().setCompany(company);
        MDC.put("company", company.getTenant());
      } else {
        LOG.debug("No company found for given X-CONTEXT-COMPANY Header", companyKey);
      }
    }
    _filterChain.doFilter(_request, _response);
  }
}
