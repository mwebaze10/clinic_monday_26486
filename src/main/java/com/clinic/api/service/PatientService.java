package com.clinic.api.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.clinic.api.model.Patient;
import com.clinic.api.repository.DoctorRepository;
import com.clinic.api.repository.PatientRepository;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public PatientService(PatientRepository patientRepository, DoctorRepository doctorRepository) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    public List<Patient> findByLastName(String lastName) {
        return patientRepository.findByLastNameIgnoreCaseOrderByNameAsc(lastName);
    }

    public List<Patient> findByDoctorId(Long doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "The doctor with that id does not exist");
        }
        return patientRepository.findPatientsByDoctorId(doctorId);
    }
}