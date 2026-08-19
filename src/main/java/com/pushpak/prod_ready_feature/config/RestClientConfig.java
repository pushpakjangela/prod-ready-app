package com.pushpak.prod_ready_feature.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${hospitalManagementBaseUrl}")
    private String HOSPITAL_MANAGEMENT_BASE_URL;

    @Bean
    @Qualifier("hospitalManagementServiceRestClient")
    RestClient getHostpitalManagementServiceRestClient() {
        return RestClient.builder()
                .baseUrl(HOSPITAL_MANAGEMENT_BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType .APPLICATION_JSON_VALUE)
                .defaultStatusHandler(HttpStatusCode::is5xxServerError,(req,resp)->{
                    throw new RuntimeException("Server Error");
                })
                .build();
    }
}
