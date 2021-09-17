package com.scoperetail.commons.azure.storage.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Component;

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

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.specialized.BlockBlobClient;
import com.scoperetail.commons.azure.storage.api.BlobContainerClientFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component("blockBlobUtils")
@Slf4j
@AllArgsConstructor
public class BlockBlobUtils extends StorageUtilsImpl {

  private final BlobContainerClientFactory blobContainerClientFactory;

  // FileName is not used in case of blob. Directory should have the entire path with fileName
  @Override
  public void uploadData(String container, String directory, String fileName, String message) {
    BlockBlobClient blobClient = getBlockBlobClient(container, directory);
    InputStream dataStream = new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8));
    blobClient.upload(dataStream, message.length(), true);
  }

  @Override
  public void deleteData(String container, String directory, String fileName) {
    BlockBlobClient blobClient = getBlockBlobClient(container, directory);
    blobClient.delete();
  }

  @Override
  public void copyData(String container, String destinationDirectory, String fileName,
      String sourceURL) {
    BlockBlobClient blobClient = getBlockBlobClient(container, destinationDirectory);
    blobClient.copyFromUrl(sourceURL);
  }

  @Override
  public boolean existsData(String container, String blobPath, String fileName) {
    boolean result = getBlockBlobClient(container, blobPath).exists();
    log.info("containerName:[{}], blobPath:[{}], exists :[{}]", container, blobPath, result);
    return result;
  }

  private BlockBlobClient getBlockBlobClient(final String containerName, final String blobPath) {
    final BlobContainerClient blobContainerClient = blobContainerClientFactory.from(containerName);
    return blobContainerClient.getBlobClient(blobPath).getBlockBlobClient();
  }
}
