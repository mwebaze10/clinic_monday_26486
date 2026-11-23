package com.clinic.api.controller;

import java.util.List;
import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.api.model.Doctor;
import com.clinic.api.model.Appointment;
import com.clinic.api.service.DoctorService;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public List<Doctor> getAllDoctors() {
        return doctorService.findAll();
    }

    @GetMapping("/by-specialization")
    public List<Doctor> getDoctorsBySpecialization(@RequestParam String name) {
        return doctorService.findBySpecializationName(name);
    }

    @GetMapping("/search")
    public List<Doctor> searchDoctorsByName(@RequestParam String name) {
        return doctorService.searchByName(name);
    }

    @GetMapping("/workload")
    public List<Object[]> getDoctorWorkload(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end) {
        return doctorService.getWorkload(start, end);
    }

    @GetMapping("/without-office")
    public List<Doctor> getDoctorsWithoutOffice() {
        return doctorService.findWithoutOffice();
    }

    @GetMapping("/{doctorId}/agenda")
    public List<Appointment> getDoctorAgenda(
            @PathVariable Long doctorId,
            @RequestParam LocalDate date) {
        return doctorService.getAgenda(doctorId, date);
    }

    @PatchMapping("/{doctorId}/office")
    public ResponseEntity<String> moveDoctorToOffice(
            @PathVariable Long doctorId,
            @RequestParam Integer officeNumber) {
        DoctorService.DoctorSpecializationResult result =
                doctorService.moveDoctorToOffice(doctorId, officeNumber);
        return ResponseEntity.status(result.getStatus()).body(result.getMessage());
    }

    @GetMapping("/{id}")
    public java.util.Optional<Doctor> getDoctorById(@PathVariable Long id) {
        return doctorService.findById(id);
    }

    @PostMapping
    public Doctor createDoctor(@RequestBody Doctor doctor) {
        return doctorService.save(doctor);
    }

    @PutMapping("/{id}")
    public Doctor updateDoctor(@PathVariable Long id, @RequestBody Doctor doctorDetails) {
        return doctorService.update(id, doctorDetails).orElse(null);
    }

    @PutMapping("/{doctorId}/specializations/{specializationId}")
    public ResponseEntity<String> addSpecializationToDoctor(
            @PathVariable Long doctorId,
            @PathVariable Long specializationId) {
        String message = doctorService.addSpecializationToDoctor(doctorId, specializationId);
        if ("The doctor with that id does not exist".equals(message)
                || "That specialization does not exist".equals(message)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        } else if ("The doctor already has that specialization".equals(message)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
        } else {
            return ResponseEntity.ok(message);
        }
    }

    @DeleteMapping("/{doctorId}/specializations/{specializationId}")
    public ResponseEntity<String> removeSpecializationFromDoctor(
            @PathVariable Long doctorId,
            @PathVariable Long specializationId) {
        String message = doctorService.removeSpecializationFromDoctor(doctorId, specializationId);
        if ("The doctor with that id does not exist".equals(message)
                || "That specialization does not exist".equals(message)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        } else if ("The doctor does not have that specialization".equals(message)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
        } else {
            return ResponseEntity.ok(message);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteDoctor(@PathVariable Long id) {
        doctorService.deleteById(id);
    }
}