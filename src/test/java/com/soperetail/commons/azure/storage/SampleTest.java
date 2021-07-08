package com.soperetail.commons.azure.storage;

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

import com.soperetail.commons.azure.storage.api.BlobUploader;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(classes = {CommonsAzureStorageApplication.class})
class SampleTest {
  @Autowired private BlobUploader blobUploader;
  private static final String TEST_CONTAINER =
      LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toString() + 1;

  @Test
  void contextLoads() {
    assertTrue(true);
  }

  void containerOperations() throws IOException {
    // Blob-1
    String order1 = "RELEASED event for Order-1, Division 30, supplier 5216";
    // Blob-2
    String order2 = "RELEASED event for Order-2, Division 30, supplier 5217";
    blobUploader.upload(TEST_CONTAINER, order1, "RELEASED/30/5216/ORDER-1.xml");
    blobUploader.upload(TEST_CONTAINER, order2, "RELEASED/30/5217/ORDER-2.xml");
  }
}
