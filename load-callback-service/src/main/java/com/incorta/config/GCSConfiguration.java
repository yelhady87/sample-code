package com.incorta.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "gcs")
public class GCSConfiguration {
    private String projectId;
    private String credentialsFilePath;
    private String bucketName;
}
