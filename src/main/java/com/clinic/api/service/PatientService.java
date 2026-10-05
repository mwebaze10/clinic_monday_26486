package com.clinic.api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.clinic.api.model.Patient;
import com.clinic.api.repository.PatientRepository;

@Service
public class PatientService {
    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<Patient> findByLastName(String lastName) {
        return patientRepository.findByLastNameIgnoreCaseOrderByNameAsc(lastName);
    }
}