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

@Service
@RequiredArgsConstructor
public class FileMetadataService {

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

        log.info("[FILE-TEST-FLOW] saveMetadata: STARTED");
        log.info(FileMetadataConstant.METADATA_SAVE_STARTED);
        fileStatusPublisher.publishInProgressStatus();
        FileMetadataResponse fileMetadataResponse = new FileMetadataResponse();
        String stagingLocation = null;
        String persistentLocation = null;
        try {
            validateKind(fileMetadata.getKind());

            DataLakeStorageService dataLakeStorage = this.dataLakeStorageFactory.create(dpsHeaders);
            String filePath = fileMetadata.getData().getDatasetProperties().getFileSourceInfo().getFileSource();
            log.info("[FILE-TEST-FLOW] saveMetadata: filePath=" + filePath);
            fileMetadata.setId(fileMetadataUtil.generateRecordId(dpsHeaders.getPartitionId(),
                    fetchEntityFromKind(fileMetadata.getKind())));
            log.info("[FILE-TEST-FLOW] saveMetadata: generatedRecordId=" + fileMetadata.getId());

            stagingLocation = storageUtilService.getStagingLocation(filePath, dpsHeaders.getPartitionId());
            log.info("[FILE-TEST-FLOW] saveMetadata: stagingLocation=" + stagingLocation);
            persistentLocation = storageUtilService.getPersistentLocation(filePath, dpsHeaders.getPartitionId());
            log.info("[FILE-TEST-FLOW] saveMetadata: persistentLocation=" + persistentLocation);

            log.info("[FILE-TEST-FLOW] saveMetadata: copyFile staging->persistent STARTED");
            cloudStorageOperation.copyFile(stagingLocation, persistentLocation);
            log.info("[FILE-TEST-FLOW] saveMetadata: copyFile staging->persistent COMPLETED");
            String checksum = storageUtilService.getChecksum(persistentLocation);
            log.info("[FILE-TEST-FLOW] saveMetadata: checksum=" + checksum);
            if (!StringUtils.isBlank(checksum)) {
              FileSourceInfo fileSourceInfo = fileMetadata.getData().getDatasetProperties().getFileSourceInfo();
              fileSourceInfo.setChecksum(checksum);
              fileSourceInfo.setChecksumAlgorithm(storageUtilService.getChecksumAlgorithm().toString());
            }
            Record fileMetadataRecord = fileMetadataRecordMapper.fileMetadataToRecord(fileMetadata);

            log.info("[FILE-TEST-FLOW] saveMetadata: upsertRecord to OSDU Storage - id=" + fileMetadataRecord.getId());
            UpsertRecords upsertRecords = dataLakeStorage.upsertRecord(fileMetadataRecord);
            log.info("[FILE-TEST-FLOW] saveMetadata: upsertRecord COMPLETED - recordIds=" + upsertRecords.getRecordIds());
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
            log.info("[FILE-TEST-FLOW] saveMetadata: cleanupStagingLocation STARTED");
            cleanupStagingLocation(stagingLocation, dataLakeStorage, fileMetadataRecord);
            log.info("[FILE-TEST-FLOW] saveMetadata: cleanupStagingLocation COMPLETED");
        } catch (StorageException e) {
            log.error("[FILE-TEST-FLOW] saveMetadata: StorageException - " + e.getMessage(), e);
            cloudStorageOperation.deleteFile(persistentLocation);
            fileStatusPublisher.publishFailureStatus(e.getHttpResponse());
            throw e;
        } catch(OsduBadRequestException e) {
            log.error("[FILE-TEST-FLOW] saveMetadata: OsduBadRequestException - " + e.getMessage(), e);
            fileStatusPublisher.publishFailureStatus(e.getMessage(), HttpStatus.BAD_REQUEST.value());
            throw e;
        }catch (Exception e) {
            log.error("[FILE-TEST-FLOW] saveMetadata: Exception - " + e.getMessage(), e);
            cloudStorageOperation.deleteFile(persistentLocation);
            fileStatusPublisher.publishFailureStatus(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            throw new ApplicationException("Error occurred while creating file metadata", e);
        }
        log.info("[FILE-TEST-FLOW] saveMetadata: COMPLETED successfully, id=" + fileMetadataResponse.getId());
        return fileMetadataResponse;
    }

    private void cleanupStagingLocation(String stagingLocation, DataLakeStorageService dataLakeStorage, Record fileMetadataRecord) {
      try{
        log.info("[FILE-TEST-FLOW] cleanupStagingLocation: verifying record exists in storage, id=" + fileMetadataRecord.getId());
        if(dataLakeStorage.getRecord(fileMetadataRecord.getId()) != null) {
          log.info("[FILE-TEST-FLOW] cleanupStagingLocation: record confirmed, deleting staging blob=" + stagingLocation);
          cloudStorageOperation.deleteFile(stagingLocation);
          log.info("[FILE-TEST-FLOW] cleanupStagingLocation: staging blob deleted successfully");
        } else {
          log.info("[FILE-TEST-FLOW] cleanupStagingLocation: record NOT found in storage, skipping staging delete");
        }
      }
      catch (Exception e){
        log.warning("[FILE-TEST-FLOW] cleanupStagingLocation: FAILED for id=" + fileMetadataRecord.getId() + ", error=" + e.getMessage());
      }
    }

    public RecordVersion getMetadataById(String id)
            throws OsduBadRequestException, NotFoundException, ApplicationException, StorageException {
        log.info("[FILE-TEST-FLOW] getMetadataById: STARTED, id=" + id);
        DataLakeStorageService dataLakeStorage = this.dataLakeStorageFactory.create(dpsHeaders);
        Record rec = null;
        try {
            rec = dataLakeStorage.getRecord(id);
            log.info("[FILE-TEST-FLOW] getMetadataById: record fetched, found=" + (rec != null));

        } catch (StorageException storageExc) {
            log.error("[FILE-TEST-FLOW] getMetadataById: StorageException, responseCode="
                + (storageExc.getHttpResponse() != null ? storageExc.getHttpResponse().getResponseCode() : "null"));

            HttpResponse response = storageExc.getHttpResponse();
            if (FileMetadataConstant.HTTP_CODE_400 == response.getResponseCode()) {
                log.error("Invalid file id", storageExc);
            } else {
                log.error("Failed to find record for the given file id.");
            }
            throw storageExc;
        }

        if (null == rec) {
            log.info("[FILE-TEST-FLOW] getMetadataById: Record Not Found for id=" + id);
            throw new NotFoundException("Record Not Found");
        }

        RecordVersion result = fileMetadataRecordMapper.recordToRecordVersion(rec);
        String fileSource = result.getData() != null && result.getData().getDatasetProperties() != null
                && result.getData().getDatasetProperties().getFileSourceInfo() != null
                ? result.getData().getDatasetProperties().getFileSourceInfo().getFileSource() : "null";
        log.info("[FILE-TEST-FLOW] getMetadataById: COMPLETED, fileSource=" + fileSource);
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
        log.info("[FILE-TEST-FLOW] deleteMetadataRecord: STARTED, recordId=" + recordId);
        log.info(FileMetadataConstant.METADATA_DELETE_STARTED);
        RecordVersion metaRecord = this.getMetadataById(recordId);
        log.info("[FILE-TEST-FLOW] deleteMetadataRecord: metadata fetched, now deleting from OSDU Storage");
        deleteMetadataRecordFromStorage(recordId);
        log.info("[FILE-TEST-FLOW] deleteMetadataRecord: storage record deleted, now deleting persistent blob");
        deleteFileFromPersistentLocation(metaRecord);
        log.info("[FILE-TEST-FLOW] deleteMetadataRecord: COMPLETED successfully for recordId=" + recordId);
    }

    private void deleteFileFromPersistentLocation(RecordVersion metaRecord) {
        String filePath = metaRecord.getData().getDatasetProperties().getFileSourceInfo().getFileSource();
        log.info("[FILE-TEST-FLOW] deleteFileFromPersistentLocation: filePath=" + filePath);
        String persistentLocation = storageUtilService.getPersistentLocation(filePath, dpsHeaders.getPartitionId());
        log.info("[FILE-TEST-FLOW] deleteFileFromPersistentLocation: resolved persistentLocation=" + persistentLocation);
        boolean result = cloudStorageOperation.deleteFile(persistentLocation);
        log.info("[FILE-TEST-FLOW] deleteFileFromPersistentLocation: deleteFile result=" + result);
    }

    private void deleteMetadataRecordFromStorage(String recordId) throws StorageException {
        log.info("[FILE-TEST-FLOW] deleteMetadataRecordFromStorage: STARTED, recordId=" + recordId);
        DataLakeStorageService dataLakeStorage = this.dataLakeStorageFactory.create(dpsHeaders);
        HttpResponse response = dataLakeStorage.deleteRecord(recordId);
        log.info("[FILE-TEST-FLOW] deleteMetadataRecordFromStorage: response code=" + response.getResponseCode()
            + ", body=" + response.getBody());
        if (FileMetadataConstant.HTTP_CODE_204 != response.getResponseCode()) {
            log.error("[FILE-TEST-FLOW] deleteMetadataRecordFromStorage: FAILED, expected 204 but got "
                + response.getResponseCode());
            throw new StorageException(
                    "Unable to delete metadata record from storage. Check the inner HttpResponse for more info.",
                    response);
        }
        log.info("[FILE-TEST-FLOW] deleteMetadataRecordFromStorage: COMPLETED successfully");
    }

}
