package com.pushpak.prod_ready_feature.clients.impl;

import com.pushpak.prod_ready_feature.clients.HospitalManagementClient;
import com.pushpak.prod_ready_feature.dto.Doctor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class HospitalManagementClientImpl implements HospitalManagementClient {

    private final RestClient restClient;

    public HospitalManagementClientImpl(@Qualifier("hospitalManagementServiceRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<Doctor> getAllDoctors() {
        try {
            return restClient.get()
                    .uri("/doctors")
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError,(req,res)->{
                        throw new RuntimeException("client side error occurred");
                    })
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
