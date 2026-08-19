package com.pushpak.prod_ready_feature.controllers;

import com.pushpak.prod_ready_feature.clients.HospitalManagementClient;
import com.pushpak.prod_ready_feature.dto.Doctor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final HospitalManagementClient hospitalManagementClient;

    @GetMapping
    public List<Doctor> getAllDoctors() {
        return hospitalManagementClient.getAllDoctors();
    }
}
