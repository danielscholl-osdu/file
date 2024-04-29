/*
 * Copyright 2021 Microsoft Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opengroup.osdu.file.model.filecollection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DatasetProperties {

  // schema - https://community.opengroup.org/osdu/platform/system/schema-service/-/blob/master/deployments/shared-schemas/osdu/abstract/AbstractFileCollection.1.0.0.json#L31
  @JsonProperty("FileCollectionPath")
  @NotNull(message = "FileCollectionPath cannot be null")
  @Valid
  private String fileCollectionPath;
}
