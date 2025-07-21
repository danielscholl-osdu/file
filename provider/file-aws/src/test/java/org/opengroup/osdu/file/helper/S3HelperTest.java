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

package org.opengroup.osdu.file.helper;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.core.ResponseInputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opengroup.osdu.file.provider.aws.auth.TemporaryCredentials;
import org.opengroup.osdu.file.provider.aws.helper.S3Helper;
import org.opengroup.osdu.file.provider.aws.model.S3Location;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class S3HelperTest {

    private final TemporaryCredentials credentials = new TemporaryCredentials("accessKey", "secretKey", "sessionToken",
            new Date(System.currentTimeMillis() + 3600L * 1000L));
    private final String uri = "s3://my-bucket/my-key";
    private final S3Location location = S3Location.of(uri);

    @Test
    void testGeneratePresignedUrlForGet() throws SdkException, MalformedURLException {
        try (MockedStatic<S3Helper> s3HelperMock = mockStatic(S3Helper.class)) {
            URL expectedUrl = new URL("http://localhost");
            
            s3HelperMock.when(() -> S3Helper.generatePresignedUrl(
                any(S3Location.class), any(SdkHttpMethod.class), any(Date.class), any(TemporaryCredentials.class)
            )).thenReturn(expectedUrl);
            
            URL actual = S3Helper.generatePresignedUrl(location, SdkHttpMethod.GET, new Date(), credentials);
            
            assertEquals(expectedUrl, actual);
        }
    }

    @Test
    void testGeneratePresignedUrlForPut() throws SdkException, MalformedURLException {
        try (MockedStatic<S3Helper> s3HelperMock = mockStatic(S3Helper.class)) {
            URL expectedUrl = new URL("http://localhost");

            s3HelperMock.when(() -> S3Helper.generatePresignedUrl(
                any(S3Location.class), any(SdkHttpMethod.class), any(Date.class), any(TemporaryCredentials.class)
            )).thenReturn(expectedUrl);

            URL actual = S3Helper.generatePresignedUrl(location, SdkHttpMethod.PUT, new Date(), credentials);

            assertEquals(expectedUrl, actual);
        }
    }

    @Test
    void testGeneratePresignedUrlResponseHeaders() throws SdkException, MalformedURLException {
        try (MockedStatic<S3Helper> s3HelperMock = mockStatic(S3Helper.class)) {
            URL expectedUrl = new URL("http://localhost");
            
            s3HelperMock.when(() -> S3Helper.generatePresignedUrl(
                any(S3Location.class), any(SdkHttpMethod.class), any(Date.class), 
                any(TemporaryCredentials.class), any(AwsRequestOverrideConfiguration.class)
            )).thenReturn(expectedUrl);
            
            AwsRequestOverrideConfiguration awsRequestOverrideConfiguration = mock(AwsRequestOverrideConfiguration.class);
            URL actual = S3Helper.generatePresignedUrl(location, SdkHttpMethod.GET, new Date(), credentials, awsRequestOverrideConfiguration);
            
            assertEquals(expectedUrl, actual);
        }
    }

    @Test
    void testDoesObjectExist(){
        try (MockedStatic<S3Helper> s3HelperMock = mockStatic(S3Helper.class)) {
            s3HelperMock.when(() -> S3Helper.doesObjectExist(any(S3Location.class), any(TemporaryCredentials.class)))
                .thenReturn(true);
            
            boolean actual = S3Helper.doesObjectExist(location, credentials);
            assertTrue(actual);
            
            s3HelperMock.when(() -> S3Helper.doesObjectExist(any(S3Location.class), any(TemporaryCredentials.class)))
                .thenReturn(false);
            
            actual = S3Helper.doesObjectExist(location, credentials);
            assertFalse(actual);
        }
    }

    @Test
    void testDoesObjectCollectionExist() {
        try (MockedStatic<S3Helper> s3HelperMock = mockStatic(S3Helper.class)) {
            s3HelperMock.when(() -> S3Helper.doesObjectCollectionExist(any(S3Location.class), any(TemporaryCredentials.class)))
                .thenReturn(false);
            
            boolean actual = S3Helper.doesObjectCollectionExist(location, credentials);
            assertFalse(actual);
            
            s3HelperMock.when(() -> S3Helper.doesObjectCollectionExist(any(S3Location.class), any(TemporaryCredentials.class)))
                .thenReturn(true);
            
            actual = S3Helper.doesObjectCollectionExist(location, credentials);
            assertTrue(actual);
        }
    }

    @Test
    void testGetBucketRegion() {
        String expectedRegion = "us-east-1";
        String bucketName = "my-bucket";
        
        try (MockedStatic<S3Helper> s3HelperMock = mockStatic(S3Helper.class)) {
            s3HelperMock.when(() -> S3Helper.getBucketRegion(any(String.class), any(TemporaryCredentials.class)))
                .thenReturn(expectedRegion);
            
            String actual = S3Helper.getBucketRegion(bucketName, credentials);
            assertEquals(expectedRegion, actual);
        }
    }

    @Test
    void testGetObject() {
        ResponseInputStream<GetObjectResponse> responseStreamMock = mock(ResponseInputStream.class);
        
        try (MockedStatic<S3Helper> s3HelperMock = mockStatic(S3Helper.class)) {
            s3HelperMock.when(() -> S3Helper.getObject(any(S3Location.class), any(TemporaryCredentials.class)))
                .thenReturn(responseStreamMock);
            
            ResponseInputStream<GetObjectResponse> actual = S3Helper.getObject(location, credentials);
            assertEquals(responseStreamMock, actual);
            assertNotNull(actual);
        }
    }

}