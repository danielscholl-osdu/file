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

package org.opengroup.osdu.file.provider.interfaces;

import jakarta.validation.ConstraintViolationException;
import org.opengroup.osdu.core.common.model.file.FileRequest;
import org.opengroup.osdu.core.common.model.file.FileResponse;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;

public interface IFileService {

  /**
   * GetFile return URL for file downloading.
   *
   * @param request location request
   * @param headers request headers
   * @return a paginated file location result.
   * @throws ConstraintViolationException if request is invalid
   */
  FileResponse getFile(FileRequest request, DpsHeaders headers);

}
