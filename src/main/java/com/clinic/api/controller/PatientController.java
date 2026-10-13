package com.clinic.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.api.model.Appointment;
import com.clinic.api.model.Patient;
import com.clinic.api.repository.PatientRepository;
import com.clinic.api.service.AppointmentService;
import com.clinic.api.service.PatientService;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientRepository patientRepository;
    private final PatientService patientService;
    private final AppointmentService appointmentService;

    public PatientController(PatientRepository patientRepository, PatientService patientService,
            AppointmentService appointmentService) {
        this.patientRepository = patientRepository;
        this.patientService = patientService;
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @GetMapping("/by-last-name")
    public List<Patient> getPatientsByLastName(@RequestParam("lastName") String lastName) {
        return patientService.findByLastName(lastName);
    }

    @GetMapping("/of-doctor/{doctorId}")
    public List<Patient> getPatientsByDoctor(@PathVariable Long doctorId) {
        return patientService.findByDoctorId(doctorId);
    }

    @GetMapping("/frequent")
    public List<Patient> getFrequentPatients(@RequestParam("min") long minAppointments) {
        return patientService.findFrequentPatients(minAppointments);
    }

    @GetMapping("/{ssNumber}/upcoming")
    public List<Appointment> getUpcomingAppointments(@PathVariable String ssNumber) {
        return appointmentService.getUpcomingForPatient(ssNumber);
    }

    @GetMapping("/{id}")
    public Patient getPatientById(@PathVariable Long id) {
        return patientRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Patient createPatient(@RequestBody Patient patient) {
        return patientRepository.save(patient);
    }

    @PutMapping("/{id}")
    public Patient updatePatient(@PathVariable Long id, @RequestBody Patient patientDetails) {
        return patientRepository.findById(id).map(patient -> {
            patient.setName(patientDetails.getName());
            patient.setLastName(patientDetails.getLastName());
            patient.setEmail(patientDetails.getEmail());
            patient.setPhone(patientDetails.getPhone());
            patient.setSsNumber(patientDetails.getSsNumber());
            return patientRepository.save(patient);
        }).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deletePatient(@PathVariable Long id) {
        patientRepository.deleteById(id);
    }
}