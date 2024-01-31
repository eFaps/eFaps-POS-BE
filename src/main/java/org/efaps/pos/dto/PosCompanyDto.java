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
package org.efaps.pos.dto;

public class PosCompanyDto {

  private final String label;
  private final String key;

  private PosCompanyDto(final Builder _builder) {
    label = _builder.label;
    key = _builder.key;
  }

  public String getLabel() {
    return label;
  }

  public String getKey() {
    return key;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String label;
    private String key;

    public Builder withLabel(final String _label) {
      label = _label;
      return this;
    }

    public Builder withKey(final String _key) {
      key = _key;
      return this;
    }

    public PosCompanyDto build() {
      return new PosCompanyDto(this);
    }
  }
}
