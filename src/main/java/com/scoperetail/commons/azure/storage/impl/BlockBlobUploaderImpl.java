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

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.specialized.BlockBlobClient;
import com.scoperetail.commons.azure.storage.api.BlobContainerClientFactory;
import com.scoperetail.commons.azure.storage.api.BlobUploader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
@AllArgsConstructor
public class BlockBlobUploaderImpl implements BlobUploader {
  private final BlobContainerClientFactory blobContainerClientFactory;

  @Override
  public void upload(final String containerName, final String blobData, final String blobPath)
      throws IOException {
    log.trace(
        "containerName:[{}], blobData Length:[{}], blobPath:[{}]",
        containerName,
        blobData.length(),
        blobPath);
    final BlobContainerClient blobContainerClient = blobContainerClientFactory.from(containerName);
    upload(blobContainerClient, blobData, blobPath);
  }

  private void upload(
      final BlobContainerClient blobContainerClient, final String blobData, final String blobPath)
      throws IOException {
    // Create BlockBlobClient
    BlockBlobClient blobClient = blobContainerClient.getBlobClient(blobPath).getBlockBlobClient();
    InputStream dataStream = new ByteArrayInputStream(blobData.getBytes(StandardCharsets.UTF_8));
    blobClient.upload(dataStream, blobData.length(), true);
    dataStream.close();
  }
}
