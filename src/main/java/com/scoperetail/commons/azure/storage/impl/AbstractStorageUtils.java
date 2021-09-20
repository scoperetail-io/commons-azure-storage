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

import com.scoperetail.commons.azure.storage.api.StorageUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractStorageUtils implements StorageUtils {

  public boolean upload(String container, String directory, String fileName, String message) {
    boolean result = false;
    log.info("Trying to upload, Container :: {}, directory :: {}, file :: {}", container, directory,
        fileName);
    try {
      uploadData(container, directory, fileName, message);
      result = true;
      log.info("Successfully uploaded, Container :: {}, directory :: {}, file :: {}", container,
          directory, fileName);
    } catch (Exception e) {
      log.error("Upload Exception :: ", e);
    }
    return result;
  }

  public boolean delete(String container, String directory, String fileName) {
    boolean result = false;
    log.info("Trying to delete, Container :: {}, directory :: {}, file :: {}", container, directory,
        fileName);
    try {
      deleteData(container, directory, fileName);
      result = true;
      log.info("Successfully deleted, Container :: {}, directory :: {}, file :: {}", container,
          directory, fileName);
    } catch (Exception e) {
      log.error("Delete Exception :: ", e);
    }
    return result;
  }

  public boolean copy(String container, String destinationDirectory, String fileName,
      String sourceURL) {
    boolean result = false;
    log.info("Trying to copy :: {} from :: {} to :: {}", fileName, sourceURL, destinationDirectory);
    try {
      copyData(container, destinationDirectory, fileName, sourceURL);
      result = true;
      log.info("Successfully copied :: {} from :: {} to :: {}", fileName, sourceURL,
          destinationDirectory);
    } catch (Exception e) {
      log.error("Copy Exception :: ", e);
    }
    return result;
  }

  public boolean exists(String container, String directory, String fileName) {
    log.info("Checking exists :: Container :: {}, direcotry :: {}, file :: {}", container,
        directory, fileName);
    return existsData(container, directory, fileName);
  }

  public abstract void uploadData(String container, String directory, String fileName,
      String message);

  public abstract void deleteData(String container, String directory, String fileName);

  public abstract void copyData(String container, String destinationDirectory, String fileName,
      String sourceURL);

  public abstract boolean existsData(String container, String directory, String fileName);
}
