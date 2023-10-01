package com.incorta.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "incorta")
public class IncortaConfiguration {
    private boolean alwaysLoad;
    private long serviceTimeout;
    private boolean synchronous;
    private String loadSource;
    private String userPAT;
    private String loadServiceUrl;
}
