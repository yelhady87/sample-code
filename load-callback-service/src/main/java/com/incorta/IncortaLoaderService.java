package com.incorta;

import com.incorta.config.IncortaConfiguration;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Map;

@Component
public class IncortaLoaderService {

    @Autowired
    private IncortaConfiguration configs;

    @Autowired
    private RestTemplate restTemplate;

    public LoadResponse startLoad(String...schemaNames) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(configs.getUserPAT());

        //api/v2/demo/schema/load
        String url = configs.getLoadServiceUrl();
        LoadRequest loadRequest = LoadRequest.builder()
                .loadSource(configs.getLoadSource())
                .requestedTimeoutInMilliseconds(configs.getServiceTimeout())
                .schemaNames(schemaNames)
                .build();

        HttpEntity<LoadRequest> body = new HttpEntity<>(loadRequest, headers);

        ResponseEntity<LoadResponse> response = restTemplate.postForEntity(url, body, LoadResponse.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();

        } else {
            String errorMessage = String.format("Failed to start loading schema(s) [%s] due to [%s]", Arrays.asList(schemaNames), response.getBody());
            throw new RuntimeException(errorMessage);
        }
    }

    @Data
    @Builder
    public static class LoadRequest {
        private String[] schemaNames;
        private boolean synchronous;
        private String loadSource;
        private long requestedTimeoutInMilliseconds;

        public LoadRequest(String[] schemaNames, boolean synchronous, String loadSource, long requestedTimeoutInMilliseconds) {
            this.schemaNames = schemaNames;
            this.synchronous = synchronous;
            this.loadSource = loadSource;
            this.requestedTimeoutInMilliseconds = requestedTimeoutInMilliseconds;
        }
    }

    @Data
    public static class LoadResponse {
        private Map<String, String> loadingStatus;
    }

}
