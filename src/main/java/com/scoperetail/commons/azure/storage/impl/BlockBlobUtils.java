package com.scoperetail.commons.azure.storage.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.azure.storage.blob.specialized.BlockBlobClient;
import com.scoperetail.commons.azure.storage.api.BlobContainerClientFactory;
import com.scoperetail.commons.azure.storage.api.StorageUtils;
import lombok.extern.slf4j.Slf4j;

@Component("blockBlobUtils")
@Slf4j
public class BlockBlobUtils extends AbstractStorageUtils implements StorageUtils {

  @Value("${sas.expiray.days:15}")
  private Integer sasExpiryDays;

  @Value("${sas.read.permission:true}")
  private Boolean sasReadPermission;

  @Autowired
  private BlobContainerClientFactory blobContainerClientFactory;

  @Override
  public String uploadData(String container, String directory, String fileName, String message,
      Boolean isPublic) throws UnsupportedEncodingException {
    BlockBlobClient blobClient = getBlockBlobClient(container, directory, fileName, isPublic);
    InputStream dataStream = new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8));
    blobClient.upload(dataStream, message.length(), true);
    return blobClient.getBlobUrl() + "?" + blobClient.generateSas(getBlobServiceSignatureValues());
  }

  private BlobServiceSasSignatureValues getBlobServiceSignatureValues() {
    return new BlobServiceSasSignatureValues(OffsetDateTime.now().plusDays(sasExpiryDays),
        new BlobSasPermission().setReadPermission(sasReadPermission));
  }

  @Override
  public void deleteData(String container, String directory, String fileName) {
    BlockBlobClient blobClient = getBlockBlobClient(container, directory, fileName, false);
    blobClient.delete();
  }

  @Override
  public void copyData(String container, String destinationDirectory, String fileName,
      String sourceURL) {
    BlockBlobClient blobClient =
        getBlockBlobClient(container, destinationDirectory, fileName, false);
    blobClient.copyFromUrl(sourceURL);
  }

  @Override
  public boolean existsData(String container, String blobPath, String fileName) {
    boolean result = getBlockBlobClient(container, blobPath, fileName, false).exists();
    log.info("containerName:[{}], blobPath:[{}], exists :[{}]", container, blobPath, result);
    return result;
  }

  private BlockBlobClient getBlockBlobClient(final String containerName, final String directory,
      final String fileName, final Boolean isPublic) {
    final BlobContainerClient blobContainerClient =
        blobContainerClientFactory.from(containerName, isPublic);
    return blobContainerClient.getBlobClient(directory + "/" + fileName).getBlockBlobClient();
  }


}
