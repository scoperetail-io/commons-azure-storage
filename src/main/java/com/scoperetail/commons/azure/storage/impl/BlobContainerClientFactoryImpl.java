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
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.PublicAccessType;
import com.scoperetail.commons.azure.storage.api.BlobContainerClientFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
@AllArgsConstructor
public class BlobContainerClientFactoryImpl implements BlobContainerClientFactory {
  private final BlobServiceClient blobServiceClient;

  @Override
  public BlobContainerClient from(final String containerName, final Boolean publicReadAccess) {
    Objects.requireNonNull(containerName);
    final BlobContainerClient blobContainerClient =
        blobServiceClient.getBlobContainerClient(containerName);

    if (blobContainerClient.exists()) {
      log.trace("Container:[{}] already exists!", containerName);
    } else {
      log.info("Creating Container:[{}]", containerName);
      blobContainerClient.create();
      if (publicReadAccess) {
        blobContainerClient.setAccessPolicy(PublicAccessType.BLOB, null);
      }
    }
    return blobContainerClient;
  }
}
