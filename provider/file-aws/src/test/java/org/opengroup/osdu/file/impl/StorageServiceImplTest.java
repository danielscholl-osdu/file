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

package org.opengroup.osdu.file.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opengroup.osdu.core.common.dms.model.RetrievalInstructionsResponse;
import org.opengroup.osdu.core.common.dms.model.StorageInstructionsResponse;
import org.opengroup.osdu.core.common.model.http.AppException;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.file.model.FileRetrievalData;
import org.opengroup.osdu.file.provider.aws.helper.S3Helper;
import org.opengroup.osdu.file.provider.aws.model.ProviderLocation;
import org.opengroup.osdu.file.provider.aws.service.FileLocationProvider;
import org.opengroup.osdu.file.util.ExpiryTimeUtil;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.opengroup.osdu.file.model.SignedUrl;
import org.opengroup.osdu.file.model.SignedUrlParameters;
import org.opengroup.osdu.file.provider.aws.auth.TemporaryCredentials;
import org.opengroup.osdu.file.provider.aws.impl.StorageServiceImpl;
import org.opengroup.osdu.file.util.ExpiryTimeUtil.RelativeTimeValue;

@ExtendWith(MockitoExtension.class)
class StorageServiceImplTest {

    private final String fileID = "fileID";
	private final String authorizationToken = "authorizationToken";
	private final String partitionID = "partitionID";
	private final String datasetId = "datasetId";
	private final String localhost = "http://localhost";
	private final String malform = "hp://localhost,malform";
	private final String s3Uri = "s3://somebucket/something/";

    @Mock
    ObjectMapper objectMapper;

    @Mock
    DpsHeaders headers;

    @Mock
    FileLocationProvider fileLocationProvider;

    @Mock
    ExpiryTimeUtil expiryTimeUtil;

    private StorageServiceImpl storageService;

    @BeforeEach
    void setup() {
        storageService = new StorageServiceImpl(fileLocationProvider, headers, objectMapper, expiryTimeUtil);
    }

    @Test
    void testCreateSignedUrl() throws Exception {
		ProviderLocation fileLocation = mock(ProviderLocation.class);
		when(fileLocation.getSignedUrl()).thenReturn(new URI(localhost));
		when(fileLocationProvider.getUploadFileLocation(fileID, partitionID)).thenReturn(fileLocation);

        SignedUrl url = storageService.createSignedUrl(fileID, authorizationToken, partitionID);
		assertEquals(localhost, url.getUri().toString());
    }

	@Test
    void testCreateSignedUrl_malform() throws Exception {
		ProviderLocation fileLocation = mock(ProviderLocation.class);
		when(fileLocation.getSignedUrl()).thenReturn(new URI(malform));
		when(fileLocationProvider.getUploadFileLocation(fileID, partitionID)).thenReturn(fileLocation);

        assertThrows(AppException.class, () -> {
            storageService.createSignedUrl(fileID, authorizationToken, partitionID);
        });
    }

	@Test
	void testCreateStorageInstructions() throws Exception {
		ProviderLocation fileLocation = mock(ProviderLocation.class);
		when(fileLocation.getUnsignedUrl()).thenReturn(localhost);
		when(fileLocation.getSignedUrl()).thenReturn(new URI(localhost));
		when(fileLocationProvider.getUploadFileLocation(datasetId, partitionID)).thenReturn(fileLocation);

		try (MockedStatic<S3Helper> s3MockedStatic = mockStatic(S3Helper.class)) {
            s3MockedStatic
                    .when(() -> S3Helper.getBucketRegion(anyString(), any()))
                    .thenReturn("us-east-1");

			StorageInstructionsResponse response = storageService.createStorageInstructions(datasetId, partitionID);

			assertNotNull(response);
        }
	}

	@Test
	void testCreateRetrievalInstructions_invalidLocation() throws Exception {
		List<FileRetrievalData> fileRetrievalDatas = new ArrayList<FileRetrievalData>();
		FileRetrievalData fileRetrievalData = mock(FileRetrievalData.class);
		when(fileRetrievalData.getUnsignedUrl()).thenReturn(malform);
		fileRetrievalDatas.add(fileRetrievalData);

        assertThrows(AppException.class, () -> {
            storageService.createRetrievalInstructions(fileRetrievalDatas);
        });
	}

	@Test
	void testCreateRetrievalInstructions() throws Exception {
		List<FileRetrievalData> fileRetrievalDatas = new ArrayList<FileRetrievalData>();
		FileRetrievalData fileRetrievalData = mock(FileRetrievalData.class);
		when(fileRetrievalData.getUnsignedUrl()).thenReturn(s3Uri);

		ExpiryTimeUtil expTimeUtil = new ExpiryTimeUtil();
        RelativeTimeValue relativeTimeValue = expTimeUtil.getExpiryTimeValueInTimeUnit(null);
		when(expiryTimeUtil.getExpiryTimeValueInTimeUnit(any())).thenReturn(relativeTimeValue);

		TemporaryCredentials credentials = new TemporaryCredentials("accessKey", "secretKey", "sessionToken",
                new Date(System.currentTimeMillis() + 3600L * 1000L));
        String expectedRegion = "us-east-1";
        
        try (MockedStatic<S3Helper> s3MockedStatic = mockStatic(S3Helper.class)) {
            s3MockedStatic.when(() -> S3Helper.getBucketRegion(anyString(), any()))
                .thenReturn(expectedRegion);

			fileRetrievalDatas.add(fileRetrievalData);

			ProviderLocation fileLocation = mock(ProviderLocation.class);
			when(fileLocation.getUnsignedUrl()).thenReturn(localhost);
			when(fileLocation.getSignedUrl()).thenReturn(new URI(localhost));
			when(fileLocation.getLocationSource()).thenReturn(localhost);
			when(fileLocation.getCredentials()).thenReturn(credentials);
			when(fileLocationProvider.getRetrievalFileLocation(any(), any())).thenReturn(fileLocation);

			RetrievalInstructionsResponse response = storageService.createRetrievalInstructions(fileRetrievalDatas);

			assertNotNull(response);
        }
	}

	@Test
	void testCreateSignedUrlFileLocation_invalidLocation() {
		SignedUrlParameters signedUrlParameters = mock(SignedUrlParameters.class);

        assertThrows(AppException.class, () -> {
            storageService.createSignedUrlFileLocation(malform, "authorizationToken", signedUrlParameters);
        });
	}

	@Test
	void testCreateSignedUrlFileLocation() throws Exception {
		ExpiryTimeUtil expTimeUtil = new ExpiryTimeUtil();
        RelativeTimeValue relativeTimeValue = expTimeUtil.getExpiryTimeValueInTimeUnit(null);
		when(expiryTimeUtil.getExpiryTimeValueInTimeUnit(any())).thenReturn(relativeTimeValue);

		ProviderLocation fileLocation = mock(ProviderLocation.class);
		when(fileLocation.getSignedUrl()).thenReturn(new URI(localhost));
		when(fileLocationProvider.getRetrievalFileLocation(any(), any(), any())).thenReturn(fileLocation);

		SignedUrlParameters signedUrlParameters = mock(SignedUrlParameters.class);

		SignedUrl url = storageService.createSignedUrlFileLocation(s3Uri, "authorizationToken", signedUrlParameters);

		assertEquals(localhost, url.getUri().toString());
	}
}
