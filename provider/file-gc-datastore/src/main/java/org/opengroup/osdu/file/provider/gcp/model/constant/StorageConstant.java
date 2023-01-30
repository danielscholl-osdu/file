/*
 * Copyright 2020 Google LLC
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

package org.opengroup.osdu.file.provider.gcp.model.constant;

public final class StorageConstant {

  public static final int GCS_MAX_FILEPATH = 1024;
  public static final String CREATED_AT = "createdAt";
  public static final String CREATED_BY = "createdBy";
  public static final String FILE_ID = "fileID";
  public static final String MALFORMED_URL = "Malformed URL";
  public static final String INVALID_S3_STORAGE_PATH_REASON =
      "Unsigned url invalid, needs to be full S3 storage path";

  private StorageConstant() {
  }

}
