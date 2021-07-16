package com.scoperetail.commons.azure.storage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class AzureBlobConfig {

  @Value("${azure.storage.blob.connection-string}")
  private String connectionStr;

  @Bean
  public BlobServiceClient getBlobServiceClient() {
    log.info("connectionStr:{}", connectionStr);
    return new BlobServiceClientBuilder().connectionString(connectionStr).buildClient();
  }

}
