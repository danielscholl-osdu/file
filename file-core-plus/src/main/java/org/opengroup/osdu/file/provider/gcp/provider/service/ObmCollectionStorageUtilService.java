/*
 *  Copyright 2020-2023 Google LLC
 *  Copyright 2020-2023 EPAM Systems, Inc
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.opengroup.osdu.file.provider.gcp.provider.service;

import lombok.RequiredArgsConstructor;
import org.opengroup.osdu.core.common.model.tenant.TenantInfo;
import org.opengroup.osdu.core.common.partition.PartitionPropertyResolver;
import org.opengroup.osdu.core.common.provider.interfaces.ITenantFactory;

import org.opengroup.osdu.core.obm.core.EnvironmentResolver;
import org.opengroup.osdu.file.api.FileCollectionDmsApi;
import org.opengroup.osdu.file.provider.gcp.config.PartitionPropertyNames;
import org.opengroup.osdu.file.provider.gcp.config.PropertiesConfiguration;
import org.opengroup.osdu.file.provider.interfaces.IFileCollectionStorageUtilService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Get cloud storage staging and persistent location directory path.
 * Used for getRetrievalInstructions and copyDatasetsToPersistentLocation operations {@link FileCollectionDmsApi}.
 * Directory paths are generated based on `gcp.storage.stagingArea` and `gcp.storage.persistentArea properties`.
 */
@Component
@RequiredArgsConstructor
@Primary
public class ObmCollectionStorageUtilService implements IFileCollectionStorageUtilService {

  private final ITenantFactory tenantFactory;
  private final EnvironmentResolver environmentResolver;
  private final PropertiesConfiguration properties;
  private final PartitionPropertyNames partitionPropertyNames;
  private final PartitionPropertyResolver partitionPropertyResolver;

  @Override
  public String getPersistentLocation(String relativePath, String partitionId) {
    return partitionPropertyResolver.getOptionalPropertyValue(partitionPropertyNames.getPersistentLocationName(), partitionId).orElseGet(() -> {
      TenantInfo tenantInfo = tenantFactory.getTenantInfo(partitionId);
      return String.format("%s%s-%s-%s%s", environmentResolver.getTransferProtocol(partitionId),
          tenantInfo.getProjectId(), tenantInfo.getName(), properties.getPersistentArea(), relativePath);
    });
  }

  @Override
  public String getStagingLocation(String relativePath, String partitionId) {
    return partitionPropertyResolver.getOptionalPropertyValue(partitionPropertyNames.getStagingLocationName(), partitionId).orElseGet(() -> {
      TenantInfo tenantInfo = tenantFactory.getTenantInfo(partitionId);
      return String.format("%s%s-%s-%s%s", environmentResolver.getTransferProtocol(partitionId),
          tenantInfo.getProjectId(), tenantInfo.getName(), properties.getStagingArea(), relativePath);
    });
  }
}
