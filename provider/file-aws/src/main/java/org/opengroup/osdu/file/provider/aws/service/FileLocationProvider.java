/**
* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*      http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.opengroup.osdu.file.provider.aws.service;

import org.opengroup.osdu.file.provider.aws.model.ProviderLocation;
import org.opengroup.osdu.file.provider.aws.model.S3Location;

import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;

import java.time.Duration;

public interface FileLocationProvider {

    ProviderLocation getUploadFileLocation(String fileID, String partitionID);

    ProviderLocation getRetrievalFileLocation(S3Location unsignedLocation, Duration expirationDuration);
    
    ProviderLocation getRetrievalFileLocation(S3Location unsignedLocation, Duration expirationDuration, AwsRequestOverrideConfiguration requestOverrideConfiguration);

    ProviderLocation getFileCollectionUploadLocation(String datasetID, String partitionID);

    ProviderLocation getFileCollectionRetrievalLocation(S3Location unsignedLocation, Duration expirationDuration);

    String getProviderKey();
}
