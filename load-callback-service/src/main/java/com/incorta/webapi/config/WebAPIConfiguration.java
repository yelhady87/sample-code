package com.incorta.webapi.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.incorta.config.WebAppConfiguration;
import com.incorta.webapi.ApiKeyFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class WebAPIConfiguration implements WebMvcConfigurer {

    @Autowired
    WebAppConfiguration config;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        List<String> origins = new ArrayList<>(Arrays.asList("null", "http://localhost:8080", "http://localhost:3000"));
        String allowedOrigins = config.getAllowedOrigins();
        if (StringUtils.isNotBlank(allowedOrigins)) {
            String[] externalOrigin = allowedOrigins.split(",");
            for (String origin : externalOrigin) {
                if (!origins.contains(origin)) {
                    origins.add(origin.trim());
                }
            }
        }

        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedOrigins(origins.toArray(new String[0]))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }

    @Bean
    public FilterRegistrationBean<ApiKeyFilter> apiKeyFilter() {
        FilterRegistrationBean<ApiKeyFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ApiKeyFilter(config.getApiKey()));
        registrationBean.addUrlPatterns("/*"); // Specify the URL patterns to protect
        return registrationBean;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }
}
