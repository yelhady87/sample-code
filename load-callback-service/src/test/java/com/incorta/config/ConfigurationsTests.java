package com.incorta.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class ConfigurationsTests {

    @Autowired
    IncortaConfiguration incortaConfigs;

    @Autowired
    GCSConfiguration gcsConfigs;

    @Autowired
    WebAppConfiguration webAppConfigs;

    @Test
    void testIncortaConfigs() {
        assertFalse(incortaConfigs.isSynchronous());
        assertFalse(incortaConfigs.isAlwaysLoad());
        assertEquals(20_000L, incortaConfigs.getServiceTimeout());
        assertEquals("my-pat", incortaConfigs.getUserPAT());
        assertEquals("FULL", incortaConfigs.getLoadSource());
    }

    @Test
    void testGCSConfigs() {
        assertEquals("my-gcs-project", gcsConfigs.getProjectId());
        assertEquals("my-bucket", gcsConfigs.getBucketName());
        assertEquals("/path/to/credentials.json", gcsConfigs.getCredentialsFilePath());
    }

    @Test
    void testWebAppConfigs() {
        assertEquals("http://localhost:8088, https://secured.com", webAppConfigs.getAllowedOrigins());
    }

}
