/*
 * Copyright 2020 Microsoft Corporation
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

package org.opengroup.osdu.file.provider.azure.service;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opengroup.osdu.azure.blobstorage.IBlobServiceClientFactory;
import org.opengroup.osdu.core.common.model.http.AppException;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.file.model.delivery.SignedUrl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeliveryStorageServiceImplTest {

    @InjectMocks
    private DeliveryStorageServiceImpl CUT = new DeliveryStorageServiceImpl();

    @Mock
    private AzureBlobSasTokenServiceImpl tokenService;

    @Mock
    private ExpirationDateHelper expirationDateHelper;

    @Mock
    private InstantHelper instantHelper;

    @Mock
    private IBlobServiceClientFactory blobServiceClientFactory;

    @Mock
    private DpsHeaders headers;

    private String containerName = "azure-osdu-demo-r2";
    private String key = "data/provided/tno/well-logs/7845_l0904s1_1989_comp.las";
    private String unsignedUrl = "https://adodev3353335343xesa.blob.core.windows.net/" + containerName + "/" + key;
    private String authorizationToken = "";

    @Test
    public void createSignedUrl() throws IOException, URISyntaxException {
        // Arrange
        Date testDate = new Date();
        lenient().when(expirationDateHelper.getExpirationDate(anyInt())).thenReturn(testDate);
        String srn = "srn:file:-965274437";

        URL url = new URL("http://testsignedurl.com");

        Instant instant = Instant.now();
        lenient().when(instantHelper.getCurrentInstant()).thenReturn(instant);

        SignedUrl expected = new SignedUrl();
        expected.setUri(new URI(url.toString()));
        expected.setUrl(url);
        expected.setCreatedAt(instant);
        expected.setConnectionString("");

        lenient().when(tokenService.sign(any(String.class))).thenReturn(url.toString());

        // Act
        SignedUrl actual = CUT.createSignedUrl(srn, unsignedUrl, authorizationToken);

        // Assert
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void createSignedUrlForBlob() throws IOException, URISyntaxException {
        // Arrange
        Date testDate = new Date();
        lenient().when(expirationDateHelper.getExpirationDate(anyInt())).thenReturn(testDate);
        String srn = "srn:file:-965274437";

        URL url = new URL("http://testsignedurl.com");

        Instant instant = Instant.now();
        lenient().when(instantHelper.getCurrentInstant()).thenReturn(instant);


        SignedUrl expected = new SignedUrl();
        expected.setUri(new URI(url.toString()));
        expected.setUrl(url);
        expected.setCreatedAt(instant);
        expected.setConnectionString("");

        lenient().when(tokenService.sign(any(String.class))).thenReturn(url.toString());

        // Act
        SignedUrl actual = CUT.createSignedUrl(srn, unsignedUrl, authorizationToken);

        // Assert
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void createSignedUrlForContainer() throws IOException, URISyntaxException {
        // Arrange
        Date testDate = new Date();
        lenient().when(expirationDateHelper.getExpirationDate(anyInt())).thenReturn(testDate);
        String srn = "srn:file/ovds:-965274437";

        URL url = new URL("http://testsignedurl.com");

        Instant instant = Instant.now();
        lenient().when(instantHelper.getCurrentInstant()).thenReturn(instant);


        SignedUrl expected = new SignedUrl();
        expected.setUri(new URI(url.toString()));
        expected.setUrl(url);
        expected.setCreatedAt(instant);
        expected.setConnectionString("");

        lenient().when(tokenService.signContainer(any(String.class))).thenReturn(url.toString());

        // Act
        SignedUrl actual = CUT.createSignedUrl(srn, unsignedUrl, authorizationToken);

        // Assert
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void createSignedUrl_malformedUnsignedUrl_throwsAppException() {
        try {
            // Arrange
            String unsignedUrl = "testunsignedurl";
            String authorizationToken = "testAuthorizationToken";
            String srn = "srn:file/:-965274437";

            lenient().when(tokenService.sign(any(String.class))).thenReturn(unsignedUrl);

            // Act
            CUT.createSignedUrl(srn, unsignedUrl, authorizationToken);

            // Assert
            fail("Should not succeed!");
        } catch (AppException e) {
            // Assert
            assertEquals(HttpStatus.SC_BAD_REQUEST, e.getError().getCode());
            assertEquals("Malformed URL", e.getError().getReason());
            assertEquals("Unsigned url invalid, needs to be full path", e.getError().getMessage());
        } catch (Exception e) {
            // Assert
            fail("Should not get different exception");
        }
    }

    @Test
    public void createSignedUrl_malformedUnsignedUrl2_throwsAppException() {
        try {
            // Arrange p
            String unsignedUrl = "http://testunsignedurl.com/";
            String authorizationToken = "testAuthorizationToken";
            String srn = "srn:file/:-965274437";

            lenient().when(tokenService.sign(any(String.class))).thenReturn(unsignedUrl);

            // Act
            CUT.createSignedUrl(srn, unsignedUrl, authorizationToken);

            // Assert
            fail("Should not succeed!");
        } catch (AppException e) {
            // Assert
            assertEquals(HttpStatus.SC_BAD_REQUEST, e.getError().getCode());
            assertEquals("Malformed URL", e.getError().getReason());
            assertEquals("Unsigned url invalid, needs to be full path", e.getError().getMessage());
        } catch (Exception e) {
            // Assert
            fail("Should not get different exception");
        }
    }

    @Test
    public void createSignedUrl_unsupportedOperationServiceError_throwsUnsupportedOperationException() {
        try {
            Date testDate = new Date();
            lenient().when(expirationDateHelper.getExpirationDate(anyInt())).thenReturn(testDate);


            Instant instant = Instant.now();
            lenient().when(instantHelper.getCurrentInstant()).thenReturn(instant);

            // Act
            CUT.createSignedUrl(unsignedUrl, authorizationToken);

            // Assert
            fail("Should not succeed!");
        } catch (AppException e) {
            // Assert
            assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getError().getCode());
            assertEquals("Unsupported Operation Exception", e.getError().getReason());
        } catch (Exception e) {
            // Assert
            fail("Should not get different exception");
        }
    }
}
