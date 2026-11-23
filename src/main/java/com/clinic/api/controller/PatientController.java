package com.clinic.api.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.api.model.Patient;
import com.clinic.api.model.Appointment;
import com.clinic.api.service.PatientService;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public List<Patient> getAllPatients() {
        return patientService.findAll();
    }

    @GetMapping("/by-last-name")
    public List<Patient> getPatientsByLastName(@RequestParam("lastName") String lastName) {
        return patientService.findByLastNameIgnoreCase(lastName);
    }

    @GetMapping("/of-doctor/{doctorId}")
    public List<Patient> getPatientsOfDoctor(@PathVariable Long doctorId) {
        return patientService.findByDoctorId(doctorId);
    }

    @GetMapping("/frequent")
    public List<Patient> getFrequentPatients(@RequestParam long min) {
        return patientService.findFrequent(min);
    }

    @GetMapping("/{ssNumber}/upcoming")
    public List<Appointment> getUpcomingAppointments(@PathVariable String ssNumber) {
        return patientService.getUpcomingAppointments(ssNumber);
    }

    @GetMapping("/{id}")
    public Optional<Patient> getPatientById(@PathVariable Long id) {
        return patientService.findById(id);
    }

    @PostMapping
    public Patient createPatient(@RequestBody Patient patient) {
        return patientService.save(patient);
    }

    @PutMapping("/{id}")
    public Patient updatePatient(@PathVariable Long id, @RequestBody Patient patientDetails) {
        return patientService.update(id, patientDetails).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deletePatient(@PathVariable Long id) {
        patientService.deleteById(id);
    }
}