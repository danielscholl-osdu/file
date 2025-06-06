/*
 * Copyright 2020  Microsoft Corporation
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

import com.azure.cosmos.implementation.InternalServerErrorException;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.opengroup.osdu.azure.blobstorage.BlobStore;
import org.opengroup.osdu.azure.di.MSIConfiguration;
import org.opengroup.osdu.core.common.dms.model.DatasetRetrievalProperties;
import org.opengroup.osdu.core.common.dms.model.RetrievalInstructionsResponse;
import org.opengroup.osdu.core.common.dms.model.StorageInstructionsResponse;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.file.exception.OsduBadRequestException;
import org.opengroup.osdu.file.model.FileRetrievalData;
import org.opengroup.osdu.file.model.SignedObject;
import org.opengroup.osdu.file.model.SignedUrl;
import org.opengroup.osdu.file.model.SignedUrlParameters;
import org.opengroup.osdu.file.provider.azure.config.BlobStoreConfig;
import org.opengroup.osdu.file.provider.azure.model.AzureFileDmsDownloadLocation;
import org.opengroup.osdu.file.provider.azure.model.AzureFileDmsUploadLocation;
import org.opengroup.osdu.file.provider.azure.model.constant.StorageConstant;
import org.opengroup.osdu.file.provider.azure.model.property.FileLocationProperties;
import org.opengroup.osdu.file.provider.interfaces.IStorageRepository;
import org.opengroup.osdu.file.provider.interfaces.IStorageService;
import org.opengroup.osdu.file.util.ExpiryTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

@Service
@Slf4j
@AllArgsConstructor
@Primary
public class StorageServiceImpl implements IStorageService {
  @Autowired
  BlobStore blobStore;

  private static final DateTimeFormatter DATE_TIME_FORMATTER
      = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS");

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  static final String PROVIDER_KEY = "AZURE";

  @Autowired
  DpsHeaders dpsHeaders;

  @Autowired
  final FileLocationProperties fileLocationProperties;
  @Autowired
  final IStorageRepository storageRepository;

  @Autowired
  final BlobStoreConfig blobStoreConfig;

  @Autowired
  private final ExpiryTimeUtil expiryTimeUtil;

  @Autowired
  private MSIConfiguration msiConfiguration;

  @Autowired
  ServiceHelper serviceHelper;

  @Override
  public SignedUrl createSignedUrl(String fileID, String authorizationToken, String partitionID) {
    return createSignedUrl(fileID, authorizationToken, partitionID, new SignedUrlParameters());
  }

  @Override
  public SignedUrl createSignedUrl(String fileID, String authorizationToken, String partitionID,
                                   SignedUrlParameters signedUrlParameters) {
    log.debug("Creating the signed blob for fileID : {} : {}, partitionID : {}",
        fileID, partitionID);
    Instant now = Instant.now(Clock.systemUTC());

    String containerName = getContainerName();

    String userDesID = getUserDesID();
    String filepath = getFileLocationPrefix(now, fileID, userDesID);
    log.debug("Create storage object for fileID {} in container {} with filepath {}",
        fileID, containerName, filepath);

    if (filepath.length() > StorageConstant.AZURE_MAX_FILEPATH) {
      throw new IllegalArgumentException(format(
          "The maximum filepath length is %s characters, but got a name with %s characters",
          StorageConstant.AZURE_MAX_FILEPATH, filepath.length()));
    }

    SignedObject signedObject = storageRepository.getSignedObjectBasedOnParams(containerName, filepath, signedUrlParameters);

    return SignedUrl.builder()
        .url(signedObject.getUrl())
        .uri(signedObject.getUri())
        .fileSource(getRelativeFileSource(filepath))
        .createdBy(userDesID)
        .createdAt(now)
        .build();
  }

  @Override
  public StorageInstructionsResponse createStorageInstructions(String blobId, String partitionID) {
    return createStorageInstructions(blobId, partitionID, new SignedUrlParameters());
  }

  @Override
  public RetrievalInstructionsResponse createRetrievalInstructions(List<FileRetrievalData> fileRetrievalDataList) {
    return createRetrievalInstructions(fileRetrievalDataList, new SignedUrlParameters());
  }

  private String getRelativeFileSource(String filePath) {
    return "/" + filePath;
  }

  private String getContainerName() {
    return blobStoreConfig.getStagingContainer();
  }

  private String getUserDesID() {
    return fileLocationProperties.getUserId();
  }

  private String getFileLocationPrefix(Instant instant, String filename, String userDesID) {
    String folderName = instant.toEpochMilli() + "-"
        + DATE_TIME_FORMATTER
        .withZone(ZoneOffset.UTC)
        .format(instant);

    return format("%s/%s/%s", userDesID, folderName, filename);
  }

  @SneakyThrows
  @Override
  public SignedUrl createSignedUrlFileLocation(String unsignedUrl,
      String authorizationToken, SignedUrlParameters signedUrlParameters) {

    if (StringUtils.isBlank(authorizationToken) || StringUtils.isBlank(unsignedUrl)) {
      throw new IllegalArgumentException(
          String.format("invalid received for authorizationToken (value: %s) or unsignedURL (value: %s)",
              authorizationToken, unsignedUrl));
    }

    String containerName = serviceHelper.getContainerNameFromAbsoluteFilePath(unsignedUrl);
    String filePath = serviceHelper.getRelativeFilePathFromAbsoluteFilePath(unsignedUrl);

    BlobSasPermission permission = new BlobSasPermission();
    permission.setReadPermission(true);
    OffsetDateTime expiryTime = expiryTimeUtil
            .getExpiryTimeInOffsetDateTime(signedUrlParameters.getExpiryTime());


    String signedUrlString = null;
    if (StringUtils.isEmpty(signedUrlParameters.getFileName())) {
      if(!msiConfiguration.getIsEnabled()) {
        signedUrlString = blobStore.generatePreSignedURL(
            dpsHeaders.getPartitionId(),
            filePath.toString(),
            containerName,
            expiryTime,
            permission);
      } else {
        signedUrlString = blobStore.generatePreSignedUrlWithUserDelegationSas(
            dpsHeaders.getPartitionId(),
            containerName,
            filePath.toString(),
            expiryTime,
            permission);
      }
    }else {

      if(!msiConfiguration.getIsEnabled()) {
        signedUrlString = blobStore.generatePreSignedURL(
            dpsHeaders.getPartitionId(),
            filePath.toString(),
            containerName,
            expiryTime,
            permission,
            encompassFilename(UriUtils.encodePath(signedUrlParameters.getFileName(), StandardCharsets.UTF_8)),
            signedUrlParameters.getContentType());
      } else {
        signedUrlString = blobStore.generatePreSignedUrlWithUserDelegationSas(
            dpsHeaders.getPartitionId(),
            filePath.toString(),
            containerName,
            expiryTime,
            permission,
            encompassFilename(UriUtils.encodePath(signedUrlParameters.getFileName(), StandardCharsets.UTF_8)),
            signedUrlParameters.getContentType());
      }
    }

   if(StringUtils.isBlank(signedUrlString)) {
      throw new InternalServerErrorException(String.format("Could not generate signed URL for file location %s", unsignedUrl), 500);
    }

    return SignedUrl.builder()
          .url(new URL(signedUrlString))
          .uri(URI.create(UriUtils.encodePath(unsignedUrl, StandardCharsets.UTF_8)))
          .createdBy(getUserDesID())
          .createdAt(Instant.now(Clock.systemUTC()))
          .build();
  }

  @Override
  public Boolean revokeUrl(Map<String, String> revokeURLRequest) {
    validateInputFor(revokeURLRequest);
    return storageRepository.revokeUserDelegationKeys(revokeURLRequest);
  }

  private static void validateInputFor(Map<String, String> revokeURLRequest) {
    String resourceGroupName = revokeURLRequest.get("resourceGroup");
    String storageAccountName = revokeURLRequest.get("storageAccount");
    if (Strings.isBlank(resourceGroupName) || Strings.isBlank(storageAccountName)) {
      throw new OsduBadRequestException(
          String.format("Illegal argument for resourceGroup { %s } or storageAccount { %s }",
              resourceGroupName, storageAccountName));
    }
  }

  private String encompassFilename(String filepath) {
    return '"' + filepath + '"';
  }

  @Override
  public RetrievalInstructionsResponse createRetrievalInstructions(List<FileRetrievalData> fileRetrievalDataList, SignedUrlParameters signedUrlParameters) {

    List<DatasetRetrievalProperties> datasetRetrievalProperties = new ArrayList<>(fileRetrievalDataList.size());

    for(FileRetrievalData fileRetrievalData : fileRetrievalDataList) {

      SignedUrl signedUrl = this.createSignedUrlFileLocation(fileRetrievalData.getUnsignedUrl(),
          dpsHeaders.getAuthorization(), signedUrlParameters);

      AzureFileDmsDownloadLocation dmsLocation = AzureFileDmsDownloadLocation.builder()
          .signedUrl(signedUrl.getUrl().toString())
          .fileSource(signedUrl.getFileSource())
          .createdBy(signedUrl.getCreatedBy())
          .expiryTime(expiryTimeUtil.getExpiryTimeInString(signedUrlParameters))
          .build();

      Map<String, Object> downloadLocation = OBJECT_MAPPER.convertValue(dmsLocation, new TypeReference<Map<String, Object>>() {});
      DatasetRetrievalProperties datasetRetrievalProperty = DatasetRetrievalProperties.builder()
          .retrievalProperties(downloadLocation)
          .datasetRegistryId(fileRetrievalData.getRecordId())
          .providerKey(PROVIDER_KEY)
          .build();

      datasetRetrievalProperties.add(datasetRetrievalProperty);
    }

    return RetrievalInstructionsResponse.builder()
        .datasets(datasetRetrievalProperties)
        .build();
  }

  @Override
  public StorageInstructionsResponse createStorageInstructions(String blobId, String partitionID, SignedUrlParameters signedUrlParameters) {
    SignedUrl signedUrl = this.createSignedUrl(blobId, dpsHeaders.getAuthorization(), partitionID, signedUrlParameters);

    AzureFileDmsUploadLocation dmsLocation = AzureFileDmsUploadLocation.builder()
        .signedUrl(signedUrl.getUrl().toString())
        .createdBy(signedUrl.getCreatedBy())
        .fileSource(signedUrl.getFileSource())
        .expiryTime(expiryTimeUtil.getExpiryTimeInString(signedUrlParameters))
        .build();

    Map<String, Object> uploadLocation = OBJECT_MAPPER.convertValue(dmsLocation, new TypeReference<Map<String, Object>>() {});
    StorageInstructionsResponse response = StorageInstructionsResponse.builder()
        .providerKey(PROVIDER_KEY)
        .storageLocation(uploadLocation).build();

    return response;
  }
}
