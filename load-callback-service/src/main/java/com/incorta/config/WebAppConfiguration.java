package com.incorta.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "web-app")
public class WebAppConfiguration {

    //CORS settings
    private String allowedOrigins;

    private String apiKey;

}
