/*
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

package org.opengroup.osdu.file.service;

import org.apache.commons.lang3.StringUtils;
import org.opengroup.osdu.core.common.http.HttpResponse;
import org.opengroup.osdu.core.common.logging.JaxRsDpsLog;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.file.constant.FileMetadataConstant;
import org.opengroup.osdu.file.exception.ApplicationException;
import org.opengroup.osdu.file.exception.KindValidationException;
import org.opengroup.osdu.file.exception.NotFoundException;
import org.opengroup.osdu.file.exception.OsduBadRequestException;
import org.opengroup.osdu.file.mapper.FileMetadataRecordMapper;
import org.opengroup.osdu.file.model.filemetadata.FileMetadata;
import org.opengroup.osdu.file.model.filemetadata.FileMetadataResponse;
import org.opengroup.osdu.file.model.filemetadata.RecordVersion;
import org.opengroup.osdu.file.model.filemetadata.filedetails.FileSourceInfo;
import org.opengroup.osdu.file.model.storage.Record;
import org.opengroup.osdu.file.model.storage.UpsertRecords;
import org.opengroup.osdu.file.provider.interfaces.ICloudStorageOperation;
import org.opengroup.osdu.file.provider.interfaces.IStorageUtilService;
import org.opengroup.osdu.file.service.status.FileDatasetDetailsPublisher;
import org.opengroup.osdu.file.service.status.FileStatusPublisher;
import org.opengroup.osdu.file.service.storage.DataLakeStorageFactory;
import org.opengroup.osdu.file.service.storage.DataLakeStorageService;
import org.opengroup.osdu.file.service.storage.StorageException;
import org.opengroup.osdu.file.util.FileMetadataUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class FileMetadataService {

    private static final Logger logger = LoggerFactory.getLogger(FileMetadataService.class);

    final JaxRsDpsLog log;
    final DataLakeStorageFactory dataLakeStorageFactory;
    final IStorageUtilService storageUtilService;
    final ICloudStorageOperation cloudStorageOperation;
    final DpsHeaders dpsHeaders;
    final FileMetadataUtil fileMetadataUtil;
    final FileMetadataRecordMapper fileMetadataRecordMapper;
    final FileStatusPublisher fileStatusPublisher;
    final FileDatasetDetailsPublisher fileDatasetDetailsPublisher;

    public FileMetadataResponse saveMetadata(FileMetadata fileMetadata)
            throws OsduBadRequestException, StorageException, ApplicationException {

        logger.debug("Saving metadata started");
        log.info(FileMetadataConstant.METADATA_SAVE_STARTED);
        fileStatusPublisher.publishInProgressStatus();
        FileMetadataResponse fileMetadataResponse = new FileMetadataResponse();
        String stagingLocation = null;
        String persistentLocation = null;
        try {
            validateKind(fileMetadata.getKind());

            DataLakeStorageService dataLakeStorage = this.dataLakeStorageFactory.create(dpsHeaders);
            String filePath = fileMetadata.getData().getDatasetProperties().getFileSourceInfo().getFileSource();
            logger.debug("Resolved file source path: {}", filePath);
            fileMetadata.setId(fileMetadataUtil.generateRecordId(dpsHeaders.getPartitionId(),
                    fetchEntityFromKind(fileMetadata.getKind())));
            logger.debug("Generated record ID: {}", fileMetadata.getId());

            stagingLocation = storageUtilService.getStagingLocation(filePath, dpsHeaders.getPartitionId());
            logger.debug("Resolved staging location: {}", stagingLocation);
            persistentLocation = storageUtilService.getPersistentLocation(filePath, dpsHeaders.getPartitionId());
            logger.debug("Resolved persistent location: {}", persistentLocation);

            logger.debug("Copying blob from staging to persistent");
            cloudStorageOperation.copyFile(stagingLocation, persistentLocation);
            logger.debug("Copied blob from staging to persistent");
            String checksum = storageUtilService.getChecksum(persistentLocation);
            logger.debug("Computed checksum: {}", checksum);
            if (!StringUtils.isBlank(checksum)) {
              FileSourceInfo fileSourceInfo = fileMetadata.getData().getDatasetProperties().getFileSourceInfo();
              fileSourceInfo.setChecksum(checksum);
              fileSourceInfo.setChecksumAlgorithm(storageUtilService.getChecksumAlgorithm().toString());
            }
            Record fileMetadataRecord = fileMetadataRecordMapper.fileMetadataToRecord(fileMetadata);

            logger.info("Upserting record to storage: id={}", fileMetadataRecord.getId());
            UpsertRecords upsertRecords = dataLakeStorage.upsertRecord(fileMetadataRecord);
            logger.info("Upserted record: recordIds={}", upsertRecords.getRecordIds());
            fileMetadataResponse.setId(upsertRecords.getRecordIds().get(0));
            fileStatusPublisher.publishSuccessStatus(upsertRecords.getRecordIds().get(0),
                    upsertRecords.getRecordIdVersions().get(0));
            fileDatasetDetailsPublisher.publishDatasetDetails(upsertRecords.getRecordIds().get(0),
                    upsertRecords.getRecordIdVersions().get(0));

            /**
             * Issue: https://community.opengroup.org/osdu/platform/system/file/-/issues/76
             * Resolution:
             * 1. Check the staging file exists
             * 2. Catch deletion failures and ignore them as deletion failure should not
             *    invalidate the call to save metadata
             * 3. Delete should be the last step of metadata save process
             * */
            logger.debug("Cleaning up staging location");
            cleanupStagingLocation(stagingLocation, dataLakeStorage, fileMetadataRecord);
            logger.debug("Cleaned up staging location");
        } catch (StorageException e) {
            logger.error("Failed to save metadata (StorageException): {}", e.getMessage(), e);
            cloudStorageOperation.deleteFile(persistentLocation);
            fileStatusPublisher.publishFailureStatus(e.getHttpResponse());
            throw e;
        } catch(OsduBadRequestException e) {
            logger.error("Failed to save metadata (bad request): {}", e.getMessage(), e);
            fileStatusPublisher.publishFailureStatus(e.getMessage(), HttpStatus.BAD_REQUEST.value());
            throw e;
        }catch (Exception e) {
            logger.error("Failed to save metadata (unexpected): {}", e.getMessage(), e);
            cloudStorageOperation.deleteFile(persistentLocation);
            fileStatusPublisher.publishFailureStatus(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            throw new ApplicationException("Error occurred while creating file metadata", e);
        }
        logger.debug("Saved metadata: id={}", fileMetadataResponse.getId());
        return fileMetadataResponse;
    }

    private void cleanupStagingLocation(String stagingLocation, DataLakeStorageService dataLakeStorage, Record fileMetadataRecord) {
      try{
        logger.debug("Verifying record exists before staging cleanup: id={}", fileMetadataRecord.getId());
        if(dataLakeStorage.getRecord(fileMetadataRecord.getId()) != null) {
          logger.debug("Deleting staging blob: {}", stagingLocation);
          cloudStorageOperation.deleteFile(stagingLocation);
          logger.debug("Deleted staging blob");
        } else {
          logger.debug("Record not found in storage, skipping staging delete: id={}", fileMetadataRecord.getId());
        }
      }
      catch (Exception e){
        logger.warn("Staging cleanup failed for id={}: {}", fileMetadataRecord.getId(), e.getMessage());
      }
    }

    public RecordVersion getMetadataById(String id)
            throws OsduBadRequestException, NotFoundException, ApplicationException, StorageException {
        logger.debug("Fetching metadata: id={}", id);
        DataLakeStorageService dataLakeStorage = this.dataLakeStorageFactory.create(dpsHeaders);
        Record rec = null;
        log.info("Fetching Record Id");
        try {
            rec = dataLakeStorage.getRecord(id);
            logger.debug("Fetched record: id={}, found={}", id, rec != null);

        } catch (StorageException storageExc) {
            logger.error("Storage error fetching record: id={}, responseCode={}",
                id, storageExc.getHttpResponse() != null ? storageExc.getHttpResponse().getResponseCode() : "null");

            HttpResponse response = storageExc.getHttpResponse();
            if (FileMetadataConstant.HTTP_CODE_400 == response.getResponseCode()) {
                log.error("Invalid file id", storageExc);
            } else {
                log.error("Failed to find record for the given file id.");
            }
            throw storageExc;
        }

        if (null == rec) {
            logger.info("Record not found: id={}", id);
            throw new NotFoundException("Record Not Found");
        }

        RecordVersion result = fileMetadataRecordMapper.recordToRecordVersion(rec);
        String fileSource = result.getData() != null && result.getData().getDatasetProperties() != null
                && result.getData().getDatasetProperties().getFileSourceInfo() != null
                ? result.getData().getDatasetProperties().getFileSourceInfo().getFileSource() : "null";
        logger.debug("Fetched metadata: id={}, fileSource={}", id, fileSource);
        return result;
    }

    private void validateKind(String kind) {
        String[] kindArr = kind.split(FileMetadataConstant.KIND_SEPRATOR);

        if (kindArr.length != 4) {
            throw new KindValidationException("Invalid kind");
        } else if (!kindArr[1].equalsIgnoreCase(FileMetadataConstant.FILE_KIND_SOURCE)) {
            throw new KindValidationException("Invalid source in kind");
        } else if (!kindArr[2].equalsIgnoreCase(FileMetadataConstant.FILE_KIND_ENTITY)) {
            throw new KindValidationException("Invalid entity in kind");
        }
    }

    private String fetchEntityFromKind(String kind) {
        String[] kindArr = kind.split(FileMetadataConstant.KIND_SEPRATOR);

        return kindArr[2];
    }

    public void deleteMetadataRecord(String recordId)
            throws OsduBadRequestException, StorageException, NotFoundException, ApplicationException {
        logger.debug("Deleting metadata record: id={}", recordId);
        log.info(FileMetadataConstant.METADATA_DELETE_STARTED);
        RecordVersion metaRecord = this.getMetadataById(recordId);
        logger.debug("Deleting record from storage: id={}", recordId);
        deleteMetadataRecordFromStorage(recordId);
        logger.debug("Deleting persistent blob for record: id={}", recordId);
        deleteFileFromPersistentLocation(metaRecord);
        logger.debug("Deleted metadata record: id={}", recordId);
    }

    private void deleteFileFromPersistentLocation(RecordVersion metaRecord) {
        String filePath = metaRecord.getData().getDatasetProperties().getFileSourceInfo().getFileSource();
        logger.debug("Resolving persistent location for deletion: filePath={}", filePath);
        String persistentLocation = storageUtilService.getPersistentLocation(filePath, dpsHeaders.getPartitionId());
        logger.debug("Deleting persistent blob: location={}", persistentLocation);
        boolean result = cloudStorageOperation.deleteFile(persistentLocation);
        logger.info("Deleted persistent blob: result={}", result);
    }

    private void deleteMetadataRecordFromStorage(String recordId) throws StorageException {
        logger.debug("Deleting storage record: id={}", recordId);
        DataLakeStorageService dataLakeStorage = this.dataLakeStorageFactory.create(dpsHeaders);
        HttpResponse response = dataLakeStorage.deleteRecord(recordId);
        logger.info("Delete storage record response: code={}, body={}", response.getResponseCode(), response.getBody());
        if (FileMetadataConstant.HTTP_CODE_204 != response.getResponseCode()) {
            logger.error("Failed to delete storage record: id={}, responseCode={}", recordId, response.getResponseCode());
            throw new StorageException(
                    "Unable to delete metadata record from storage. Check the inner HttpResponse for more info.",
                    response);
        }
        logger.debug("Deleted storage record: id={}", recordId);
    }

}
