package com.scoperetail.commons.azure.storage.impl;

/*-
 * *****
 * commons-azure-storage
 * -----
 * Copyright (C) 2018 - 2021 Scope Retail Systems Inc.
 * -----
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * =====
 */

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.azure.storage.file.share.ShareClient;
import com.azure.storage.file.share.ShareClientBuilder;
import com.azure.storage.file.share.ShareDirectoryClient;
import com.azure.storage.file.share.ShareFileClient;
import com.azure.storage.file.share.ShareFileClientBuilder;
import com.scoperetail.commons.azure.storage.api.StorageUtils;
import com.scoperetail.commons.azure.storage.config.AzureConfig;
import lombok.extern.slf4j.Slf4j;

@Component("fileUtils")
@Slf4j
public class FileUtilsImpl extends AbstractStorageUtils implements StorageUtils {

  @Autowired
  private AzureConfig azureConfig;

  @Override
  public void uploadData(String fileShare, String directory, String fileName, String message) {
    uploadFile(fileShare, directory, fileName, message);
  }

  @Override
  public String uploadWithURLResponseData(String fileShare, String directory, String fileName,
      String message) throws UnsupportedEncodingException {
    String url = uploadFile(fileShare, directory, fileName, message).getFileUrl();
    return URLDecoder.decode(url, StandardCharsets.UTF_8.toString());
  }

  private ShareFileClient uploadFile(String fileShare, String directory, String fileName,
      String message) {
    ShareFileClient fileClient = getShareFileClient(fileShare, directory, fileName);
    InputStream dataStream = new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8));
    fileClient.create(message.length());
    fileClient.uploadRange(dataStream, message.length());
    return fileClient;
  }

  @Override
  public void deleteData(String fileShare, String directory, String fileName) {
    ShareFileClient fileClient = getShareFileClient(fileShare, directory, fileName);
    fileClient.delete();
  }

  @Override
  public void copyData(String fileShare, String destinationDirectory, String fileName,
      String sourceURL) {
    ShareFileClient fileClient = getShareFileClient(fileShare, destinationDirectory, fileName);
    fileClient.beginCopy(sourceURL, null, null);
  }

  @Override
  public boolean existsData(String fileShare, String directory, String fileName) {
    return getShareFileClient(fileShare, directory, fileName).exists();
  }

  private ShareFileClient getShareFileClient(String fileShare, String directory, String fileName) {
    ShareDirectoryClient dirClient =
        new ShareFileClientBuilder().connectionString(azureConfig.getConnectionStr())
            .shareName(fileShare).resourcePath(directory).buildDirectoryClient();
    return dirClient.getFileClient(fileName);
  }

  public boolean createFileShare(String fileShare) {
    boolean result = true;
    try {
      ShareClient shareClient =
          new ShareClientBuilder().connectionString(azureConfig.getConnectionStr())
              .shareName(fileShare.toLowerCase()).buildClient();
      if (shareClient.exists()) {
        log.trace("FileShare already exists :: {}", fileShare);
      } else {
        shareClient.create();
        log.info("New FileShare created :: {}", fileShare);
      }
    } catch (Exception e) {
      log.error("Create FileShare Exception: " + e);
      result = false;
    }
    return result;
  }

  public boolean createDirectory(String fileShare, String directory) {
    boolean result = false;
    try {
      // Azure does not created nested directories automatically.
      // To create directory like :: /appl/sdavz/live/cams, we need to create 4 directories.
      // 1. /appl 2. /appl/sdavz 3. /appl/sdavz/live 4. /appl/sdavz/live/cams
      String[] directories = directory.split("/");
      StringBuilder currentDirectory = new StringBuilder();
      for (int i = 0; i < directories.length; i++) {
        currentDirectory.append(directories[i]);
        createNestedDirectories(fileShare, currentDirectory.toString());
        currentDirectory.append("/");
      }
      result = true;
    } catch (Exception e) {
      log.error("Create Directory exception: " + e);
    }
    return result;
  }

  private void createNestedDirectories(String fileShare, String directory) {
    ShareDirectoryClient dirClient =
        new ShareFileClientBuilder().connectionString(azureConfig.getConnectionStr())
            .shareName(fileShare).resourcePath(directory).buildDirectoryClient();
    if (dirClient.exists()) {
      log.trace("Directory :: {} already exists under share :: {}", directory, fileShare);
    } else {
      dirClient.create();
      log.info("New Directory :: {} created under share :: {}", directory, fileShare);
    }
  }
}
