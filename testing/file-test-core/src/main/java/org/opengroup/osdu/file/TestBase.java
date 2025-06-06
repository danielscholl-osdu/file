/*
 * Copyright 2020 Google LLC
 * Copyright 2020 EPAM Systems, Inc
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

package org.opengroup.osdu.file;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.opengroup.osdu.file.apitest.Config;
import org.opengroup.osdu.file.util.CloudStorageUtil;

public abstract class TestBase {

  protected static HttpClient client;

  protected static CloudStorageUtil cloudStorageUtil;

  public static Map<String, String> getCommonHeader() throws IOException {
    return getHeaders(Config.getDataPartitionId(), client.getAccessToken());
  }

  public static Map<String, String> getHeaders(String dataPartition, String token) {
    Map<String, String> headers = new HashMap<>();
    System.out.println("Building headers here...");
    if (dataPartition != null && !dataPartition.isEmpty()) {
      System.out.println("Using Data partition: " + dataPartition);
      headers.put("Data-Partition-Id", dataPartition);
    }
    if (token != null && !token.isEmpty()) {
      headers.put("Authorization", token);
    }

    final String correlationId = UUID.randomUUID().toString();
    System.out.println("Using correlation-id for the request " + correlationId);
    headers.put("correlation-id", correlationId);

    return headers;
  }
}
