package com.incorta.services;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.*;
import com.incorta.config.GCSConfiguration;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class GCSBucketFileSystem {

    @Autowired
    private GCSConfiguration config;

    public List<GCSFile> listFiles() {
        List<GCSFile> fileNames = new ArrayList<>();
        Storage storage = getStorage(config.getProjectId(), config.getCredentialsFilePath());
        Bucket bucket = storage.get(config.getBucketName());
        for (Blob blob : bucket.list().iterateAll()) {
            GCSFile file = GCSFile.builder().name(blob.getName()).size(blob.getSize()).build();
            fileNames.add(file);
        }

        return fileNames;
    }

    public void writeContentToFile(String filename, byte[] content) {
        BlobId blobId = BlobId.of(config.getBucketName(), filename);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
        Storage storage = getStorage(config.getProjectId(), config.getCredentialsFilePath());
        storage.create(blobInfo, content);
    }

    public String getFileContents(String filename) {
        BlobId blobId = BlobId.of(config.getBucketName(), filename);
        Storage storage = getStorage(config.getProjectId(), config.getCredentialsFilePath());
        Blob blob = storage.get(blobId);

        byte[] result = blob.getContent();
        return new String(result, StandardCharsets.UTF_8);
    }

    private Storage getStorage(String projectId, String credentialsFile) {
        try (FileInputStream credentials = new FileInputStream(credentialsFile)) {
            return StorageOptions.newBuilder()
                    .setProjectId(projectId)
                    .setCredentials(ServiceAccountCredentials.fromStream(credentials))
                    .build()
                    .getService();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Data
    @Builder
    public static class GCSFile {
        private String name;
        private Long size;
    }

}
