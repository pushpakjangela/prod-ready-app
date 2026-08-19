package com.pushpak.prod_ready_feature.clients;

import com.pushpak.prod_ready_feature.dto.Doctor;

import java.util.List;

public interface HospitalManagementClient {
    List<Doctor> getAllDoctors();
}
