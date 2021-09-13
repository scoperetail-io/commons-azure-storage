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
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.azure.storage.file.share.ShareClient;
import com.azure.storage.file.share.ShareClientBuilder;
import com.azure.storage.file.share.ShareDirectoryClient;
import com.azure.storage.file.share.ShareFileClient;
import com.azure.storage.file.share.ShareFileClientBuilder;
import com.scoperetail.commons.azure.storage.api.FileUtils;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FileUtilsImpl implements FileUtils {

  @Value("${azure.storage.connection-string}")
  private String connectStr;

  @Override
  public boolean uploadFile(String fileShare, String directory, String fileName, String message) {
    log.info("Trying to upload File :: {} in directory :: {}, fileshare :: {}", fileName, directory,
        fileShare);
    boolean result = false;
    try {
      if (createFileShare(fileShare) && createDirectory(fileShare, directory)) {
        ShareDirectoryClient dirClient = getShareDirectoryClient(fileShare, directory);
        InputStream dataStream = new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8));
        ShareFileClient fileClient = dirClient.getFileClient(fileName);
        fileClient.create(message.length());
        fileClient.uploadRange(dataStream, message.length());
        result = true;
        log.info("File :: {} uploaded successfully in directory :: {}, fileshare :: {}", fileName,
            directory, fileShare);
      }
    } catch (Exception e) {
      log.error("UploadFile exception: {}", e);
    }
    return result;
  }

  private boolean createFileShare(String shareName) {
    boolean result = true;
    try {
      ShareClient shareClient =
          new ShareClientBuilder().connectionString(connectStr).shareName(shareName).buildClient();
      if (shareClient.exists()) {
        log.trace("FileShare already exists :: {}", shareName);
      } else {
        shareClient.create();
        log.info("New FileShare created :: {}", shareName);
      }
    } catch (Exception e) {
      log.error("Create FileShare Exception: " + e);
      result = false;
    }
    return result;
  }

  private boolean createDirectory(String shareName, String dirName) {
    boolean result = true;
    try {
      ShareDirectoryClient dirClient = getShareDirectoryClient(shareName, dirName);
      if (dirClient.exists()) {
        log.trace("Directory :: {} already exists under share :: {}", dirName, shareName);
      } else {
        dirClient.create();
        log.info("New Directory :: {} created under share :: {}", dirName, shareName);
      }
    } catch (Exception e) {
      log.error("CreateDirectory exception: " + e);
      result = false;
    }
    return result;
  }

  private ShareDirectoryClient getShareDirectoryClient(String shareName, String dirName) {
    return new ShareFileClientBuilder().connectionString(connectStr).shareName(shareName)
        .resourcePath(dirName).buildDirectoryClient();
  }
}
