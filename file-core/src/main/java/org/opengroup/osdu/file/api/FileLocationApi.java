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

package org.opengroup.osdu.file.api;

import org.opengroup.osdu.core.common.model.file.FileLocationRequest;
import org.opengroup.osdu.core.common.model.file.FileLocationResponse;
import org.opengroup.osdu.core.common.model.file.LocationRequest;
import org.opengroup.osdu.core.common.model.file.LocationResponse;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.model.storage.StorageRole;
import org.opengroup.osdu.file.constant.FileServiceRole;
import org.opengroup.osdu.file.provider.interfaces.ILocationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestScope
@Validated
public class FileLocationApi {

  final DpsHeaders headers;
  final ILocationService locationService;

  // TODO: Create the permission for os-file and change pre authorize annotation
  @PostMapping("/getLocation")
  @PreAuthorize("@authorizationFilter.hasPermission('" + FileServiceRole.EDITORS + "')")
  public LocationResponse getLocation(@RequestBody LocationRequest request) {
    log.debug("Location request received : {}", request);
    LocationResponse locationResponse = locationService.getLocation(request, headers);
    log.debug("Location result ready : {}", locationResponse);
    return locationResponse;
  }

  // TODO: Create the permission for os-file and change pre authorize annotation
  @PostMapping("/getFileLocation")
  @PreAuthorize("@authorizationFilter.hasPermission('" + FileServiceRole.EDITORS + "')")
  public FileLocationResponse getFileLocation(@RequestBody FileLocationRequest request) {
    log.debug("File location request received : {}", request);
    FileLocationResponse fileLocationResponse = locationService.getFileLocation(request, headers);
    log.debug("File location result ready : {}", fileLocationResponse);
    return fileLocationResponse;
  }

  @GetMapping("/v1/files/uploadURL")
  @PreAuthorize("@authorizationFilter.hasPermission('" + FileServiceRole.EDITORS+ "')")
  public LocationResponse getLocationFile() throws JsonProcessingException {
    LocationRequest req = (new ObjectMapper()).readValue("{}", LocationRequest.class);
    LocationResponse locationResponse = locationService.getLocation(req, headers);
    log.debug("Location result ready : {}", locationResponse);
    return locationResponse;
  }

}
