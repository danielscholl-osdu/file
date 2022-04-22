// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.opengroup.osdu.file.provider.aws.di.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.opengroup.osdu.core.common.model.storage.Record;
import org.opengroup.osdu.core.common.model.storage.validation.ValidNotNullCollection;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateDatasetRegistryRequest {

    @ValidNotNullCollection
    @NotEmpty(message = DatasetRegistryValidationDoc.MISSING_DATASET_REGISTRIES_ARRAY)
    @Size(min = 1, max = 20, message = DatasetRegistryValidationDoc.MAX_DATASET_REGISTRIES_EXCEEDED)
    //TODO: need to support pagination of storage record get and then extend this back to 500
    private List<Record> datasetRegistries;
}
