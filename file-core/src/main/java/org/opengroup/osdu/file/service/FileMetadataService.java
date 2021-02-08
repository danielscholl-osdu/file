// Copyright © 2021 Amazon Web Services
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

package org.opengroup.osdu.file.service;

import org.opengroup.osdu.core.common.http.HttpResponse;
import org.opengroup.osdu.core.common.logging.JaxRsDpsLog;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.file.constant.FileMetadataConstant;
import org.opengroup.osdu.file.exception.ApplicationException;
import org.opengroup.osdu.file.exception.OsduBadRequestException;
import org.opengroup.osdu.file.exception.NotFoundException;
import org.opengroup.osdu.file.mapper.FileMetadataRecordMapper;
import org.opengroup.osdu.file.model.filemetadata.FileMetadata;
import org.opengroup.osdu.file.model.filemetadata.FileMetadataResponse;
import org.opengroup.osdu.file.model.filemetadata.RecordVersion;
import org.opengroup.osdu.file.model.storage.Record;
import org.opengroup.osdu.file.model.storage.UpsertRecords;
import org.opengroup.osdu.file.provider.interfaces.ICloudStorageOperation;
import org.opengroup.osdu.file.provider.interfaces.IStorageUtilService;
import org.opengroup.osdu.file.service.storage.DataLakeStorageFactory;
import org.opengroup.osdu.file.service.storage.DataLakeStorageService;
import org.opengroup.osdu.file.service.storage.StorageException;
import org.opengroup.osdu.file.util.FileMetadataUtil;
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

	public FileMetadataResponse saveMetadata(FileMetadata fileMetadata)
			throws OsduBadRequestException, StorageException, ApplicationException {

		log.info(FileMetadataConstant.METADATA_SAVE_STARTED);

		DataLakeStorageService dataLakeStorage = this.dataLakeStorageFactory.create(dpsHeaders);
		String filePath = fileMetadata.getData().getDatasetProperties().getFileSourceInfo().getFileSource();
		fileMetadata.setId(fileMetadataUtil.generateRecordId(dpsHeaders.getPartitionId()));

		String stagingLocation = storageUtilService.getStagingLocation(filePath, dpsHeaders.getPartitionId());
		String persistentLocation = storageUtilService.getPersistentLocation(filePath, dpsHeaders.getPartitionId());

    cloudStorageOperation.copyFile(stagingLocation, persistentLocation);
		FileMetadataResponse fileMetadataResponse = new FileMetadataResponse();
		Record record = fileMetadataRecordMapper.fileMetadataToRecord(fileMetadata);

		try {
			log.info("Save Record Id " + record.getId());
			UpsertRecords upsertRecords = dataLakeStorage.upsertRecord(record);
			log.info(upsertRecords.toString());
			fileMetadataResponse.setId(upsertRecords.getRecordIds().get(0));
      cloudStorageOperation.deleteFile(stagingLocation);
		} catch (StorageException e) {
			log.error("Error occurred while creating file metadata storage record", e);
      cloudStorageOperation.deleteFile(persistentLocation);
			throw e;
		} catch (Exception e) {
			log.error("Error occurred while creating file metadata ", e);
      cloudStorageOperation.deleteFile(persistentLocation);
			throw new ApplicationException("Error occurred while creating file metadata", e);
		}
		return fileMetadataResponse;
	}

	public RecordVersion getMetadataById(String id)
			throws OsduBadRequestException, NotFoundException, ApplicationException {
		DataLakeStorageService dataLakeStorage = this.dataLakeStorageFactory.create(dpsHeaders);
		Record rec = null;
		log.info("Fetcing Record Id ");
		try {
			rec = dataLakeStorage.getRecord(id);

		} catch (StorageException storageExc) {
			log.error("Error occurred while fetching metadata from storage ", storageExc);

			HttpResponse response = storageExc.getHttpResponse();
			if (FileMetadataConstant.HTTP_CODE_400 == response.getResponseCode())
				throw new OsduBadRequestException("Invalid file id");

			throw new ApplicationException("Failed to find record for the given file id.", storageExc);
		}

		if (null == rec) {
			log.warning("Record Not Found");
			throw new NotFoundException("Record Not Found");
		}

		return fileMetadataRecordMapper.recordToRecordVersion(rec);
	}
}
