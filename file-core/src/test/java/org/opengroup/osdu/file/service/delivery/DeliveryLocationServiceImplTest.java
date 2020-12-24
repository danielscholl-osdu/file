// Copyright © 2020 Amazon Web Services
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

package org.opengroup.osdu.file.service.delivery;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.file.model.delivery.SignedUrl;
import org.opengroup.osdu.file.model.delivery.SrnFileData;
import org.opengroup.osdu.file.model.delivery.UrlSigningResponse;
import org.opengroup.osdu.file.FileApplication;
import org.opengroup.osdu.file.provider.interfaces.delivery.IDeliveryStorageService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// @RunWith(MockitoJUnitRunner.class)
// @SpringBootTest(classes = { FileApplication.class })
@ExtendWith(SpringExtension.class)
public class DeliveryLocationServiceImplTest {

    @InjectMocks
    private DeliveryLocationServiceImpl CUT;

    @Mock
    private IDeliveryStorageService storageService;

    @Mock
    private IDeliverySearchService searchService;

    @Test
    public void getSignedUrlsBySrn() throws URISyntaxException, MalformedURLException {
        // Arrange
        String srn1 = "srn:file/csv:7344999246049527:";
        String srn2 = "srn:file/csv:69207556434748899880399:";
        String srn3 = "srn:file/csv:59158134479121976019:";
        String srnOvds = "srn:type:file/ovds:12345::";
        String unsignedUrl1 = "http://unsignedurl1.com";
        String unsignedUrl2 = "http://unsignedurl2.com";
        String unsignedUrl3 = "http://unsignedurl3.com";
        String unsignedUrlOvds = "http://unsignedurlOvds.com";
        String kind = "opendes:osdu:file:0.0.4";
        String kindOvds = "opendes:osdu:file:0.2.0";
        String connectionString = "connectionString";
        String connectionStringOvds = "connectionStringOvds";

        List<String> srns = new ArrayList<>();
        srns.add(srn1);
        srns.add(srn2);
        srns.add(srn3);
        srns.add(srnOvds);

        Map<String, SrnFileData> processed = new HashMap<>();
        processed.put(srn1, new SrnFileData(null, unsignedUrl1, kind, connectionString));
        processed.put(srn2, new SrnFileData(null, unsignedUrl2, kind, connectionString));
        processed.put(srn3, new SrnFileData(null, unsignedUrl3, kind, connectionString));
        processed.put(srnOvds, new SrnFileData(null, unsignedUrlOvds, kindOvds, connectionStringOvds));
        List<String> unprocessed = new ArrayList<>();

        UrlSigningResponse unsignedUrlsResponse = UrlSigningResponse.builder().processed(processed)
                .unprocessed(unprocessed).build();
        Mockito.when(searchService.GetUnsignedUrlsBySrn(Mockito.eq(srns))).thenReturn(unsignedUrlsResponse);

        SignedUrl signedUrl1 = new SignedUrl();
        signedUrl1.setUri(new URI(unsignedUrl1));
        signedUrl1.setUrl(new URL(unsignedUrl1));
        signedUrl1.setCreatedAt(Instant.now());
        signedUrl1.setConnectionString(connectionString);

        SignedUrl signedUrl2 = new SignedUrl();
        signedUrl2.setUri(new URI(unsignedUrl2));
        signedUrl2.setUrl(new URL(unsignedUrl2));
        signedUrl2.setCreatedAt(Instant.now());
        signedUrl2.setConnectionString(connectionString);

        SignedUrl signedUrl3 = new SignedUrl();
        signedUrl3.setUri(new URI(unsignedUrl3));
        signedUrl3.setUrl(new URL(unsignedUrl3));
        signedUrl3.setCreatedAt(Instant.now());
        signedUrl3.setConnectionString(connectionString);

        SignedUrl signedUrlOvds = new SignedUrl();
        signedUrlOvds.setUrl(new URL(unsignedUrlOvds));
        signedUrlOvds.setCreatedAt(Instant.now());
        signedUrlOvds.setConnectionString(connectionStringOvds);

        Mockito.when(storageService.createSignedUrl(Mockito.eq(srn1), Mockito.eq(unsignedUrl1), Mockito.any())).thenReturn(signedUrl1);
        Mockito.when(storageService.createSignedUrl(Mockito.eq(srn2), Mockito.eq(unsignedUrl2), Mockito.any())).thenReturn(signedUrl2);
        Mockito.when(storageService.createSignedUrl(Mockito.eq(srn3), Mockito.eq(unsignedUrl3), Mockito.any())).thenReturn(signedUrl3);
        Mockito.when(storageService.createSignedUrl(Mockito.eq(srnOvds), Mockito.eq(unsignedUrlOvds), Mockito.any())).thenReturn(signedUrlOvds);

        ReflectionTestUtils.setField(CUT, "headers", new DpsHeaders());

        // Act
        UrlSigningResponse actual = CUT.getSignedUrlsBySrn(srns);

        // Assert
        assertEquals(processed, actual.getProcessed());
        assertEquals(unprocessed, actual.getUnprocessed());
    }
}
