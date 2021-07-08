# Azure Storage Utility

#### Utility library for Azure Blob Storage.

Azure Blob Storage is Microsoft's object storage solution for the cloud. Blob Storage is optimized for storing massive
amounts of unstructured data. Unstructured data is data that does not adhere to a particular data model or definition,
such as text or binary data.

## Usage

* Import the latest dependency from [Maven][1]

```xml

<dependency>
    <groupId>com.soperetail.commons.azure.storage</groupId>
    <artifactId>commons-azure-storage</artifactId>
    <version>1.0</version>
</dependency>
```

* Define these four properties. You can get the details from you azure subscription with a storage account.

```properties
azure.storage.account-name=my-storage-account

# Fill storage account access key copied from portal
azure.storage.account-key=my-access-key

# Fill storage blob endpoint URL copied from portal
azure.storage.blob-endpoint=my-https-url

azure.storage.blob.connection-string=my-connection-string
```
* Autowire [BlobUploader][2] in your spring boot application

* Use `upload()` API for uploading the BlockBlob to your container

```java
blobUploader.upload("mycontainer", "Hello World", "folder/subfolder/filename.txt");
```

[1]: https://mvnrepository.com/artifact/com.scoperetail.commons/commons-azure-storage
[2]: src/main/java/com/soperetail/commons/azure/storage/api/BlobUploader.java




