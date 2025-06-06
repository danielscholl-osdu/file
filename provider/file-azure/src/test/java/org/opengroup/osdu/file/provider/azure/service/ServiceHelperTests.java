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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opengroup.osdu.file.provider.azure.TestUtils;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.MockitoAnnotations.initMocks;

@ExtendWith(MockitoExtension.class)
public class ServiceHelperTests {

  private ServiceHelper serviceHelper;

  @BeforeEach
  void init() {
    initMocks(this);
    serviceHelper = new ServiceHelper();
  }

  @Test
  void getContainerNameFromAbsoluteFilePath_ShouldReturnContainerName() {
    String expectedContainerName = TestUtils.STAGING_CONTAINER_NAME;
    String actualContainerName = serviceHelper.getContainerNameFromAbsoluteFilePath(TestUtils.ABSOLUTE_FILE_PATH);
    Assertions.assertEquals(expectedContainerName, actualContainerName);
  }

  @Test
  void getContainerNameFromAbsoluteFilePath_ShouldThrow_IfNotAbleToParseContainerNameFromFilePath() {
    Throwable thrown = catchThrowable(() -> serviceHelper.getContainerNameFromAbsoluteFilePath(TestUtils.RELATIVE_FILE_PATH));
    then(thrown)
        .isInstanceOf(InternalServerErrorException.class)
        .hasMessageContaining(String.format("Could not parse {%s} from file path provided {%s}",
            TestUtils.CONTAINER_NAME, TestUtils.RELATIVE_FILE_PATH));
  }

  @Test
  void getRelativeFilePathFromAbsoluteFilePath_ShouldReturnContainerName() {
    String expectedFilePath = TestUtils.RELATIVE_FILE_PATH;
    String actualActualFilePath = serviceHelper.getRelativeFilePathFromAbsoluteFilePath(TestUtils.ABSOLUTE_FILE_PATH);
    Assertions.assertEquals(expectedFilePath, actualActualFilePath);
  }

  @Test
  void getRelativeFilePathFromAbsoluteFilePath_ShouldThrow_IfNotAbleToParseContainerNameFromFilePath() {
    Throwable thrown = catchThrowable(() -> serviceHelper.getRelativeFilePathFromAbsoluteFilePath(TestUtils.RELATIVE_FILE_PATH));
    then(thrown)
        .isInstanceOf(InternalServerErrorException.class)
        .hasMessageContaining(String.format("Could not parse {%s} from file path provided {%s}",
            TestUtils.FILE_PATH ,TestUtils.RELATIVE_FILE_PATH));
  }

  @Test
  void getFileSystemNameFromAbsoluteFilePath_ShouldReturnFileSystemName() {
    String expectedContainerName = TestUtils.STAGING_FILE_SYSTEM_NAME;
    String actualContainerName = serviceHelper.getFileSystemNameFromAbsoluteDirectoryPath(TestUtils.ABSOLUTE_DIRECTORY_PATH);
    Assertions.assertEquals(expectedContainerName, actualContainerName);
  }

  @Test
  void getFileSystemFromAbsoluteFilePath_ShouldThrow_IfNotAbleToParseContainerNameFromFilePath() {
    Throwable thrown = catchThrowable(() -> serviceHelper.getFileSystemNameFromAbsoluteDirectoryPath(TestUtils.RELATIVE_DIRECTORY_PATH));
    then(thrown)
        .isInstanceOf(InternalServerErrorException.class)
        .hasMessageContaining(String.format("Could not parse {%s} from file path provided {%s}",
            TestUtils.FILE_SYSTEM_NAME, TestUtils.RELATIVE_DIRECTORY_PATH));
  }

  @Test
  void getRelativeDirectoryPathFromAbsoluteFilePath_ShouldReturnContainerName() {
    String expectedFilePath = TestUtils.DIRECTORY_NAME;
    String actualActualFilePath = serviceHelper.getRelativeDirectoryPathFromAbsoluteDirectoryPath(TestUtils.ABSOLUTE_DIRECTORY_PATH);
    Assertions.assertEquals(expectedFilePath, actualActualFilePath);
  }

  @Test
  void getRelativeDirectoryPathFromAbsoluteFilePath_ShouldThrow_IfNotAbleToParseContainerNameFromFilePath() {
    Throwable thrown = catchThrowable(() -> serviceHelper.getRelativeDirectoryPathFromAbsoluteDirectoryPath(TestUtils.DIRECTORY_NAME));
    then(thrown)
        .isInstanceOf(InternalServerErrorException.class)
        .hasMessageContaining(String.format("Could not parse {%s} from file path provided {%s}",
            TestUtils.DIRECTORY_PATH , TestUtils.DIRECTORY_NAME));
  }
}
