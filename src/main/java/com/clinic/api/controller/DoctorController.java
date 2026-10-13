package com.clinic.api.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.api.model.Appointment;
import com.clinic.api.model.Doctor;
import com.clinic.api.repository.DoctorRepository;
import com.clinic.api.service.AppointmentService;
import com.clinic.api.service.DoctorService;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorRepository doctorRepository;
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;

    public DoctorController(DoctorRepository doctorRepository, DoctorService doctorService,
            AppointmentService appointmentService) {
        this.doctorRepository = doctorRepository;
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    @GetMapping("/by-specialization")
    public List<Doctor> getDoctorsBySpecialization(@RequestParam("name") String name) {
        return doctorService.findBySpecialization(name);
    }

    @GetMapping("/without-office")
    public List<Doctor> getDoctorsWithoutOffice() {
        return doctorService.findWithoutOffice();
    }

    @GetMapping("/search")
    public List<Doctor> searchDoctors(@RequestParam("name") String name) {
        return doctorService.searchByName(name);
    }

    @GetMapping("/workload")
    public List<Object[]> getDoctorWorkload(@RequestParam("start") LocalDate start,
            @RequestParam("end") LocalDate end) {
        return doctorService.findWorkload(start, end);
    }

    @GetMapping("/{doctorId}/agenda")
    public List<Appointment> getDoctorAgenda(@PathVariable Long doctorId,
            @RequestParam("date") LocalDate date) {
        return appointmentService.getDoctorAgenda(doctorId, date);
    }

    @GetMapping("/{id}")
    public Doctor getDoctorById(@PathVariable Long id) {
        return doctorRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Doctor createDoctor(@RequestBody Doctor doctor, @RequestParam Long officeId) {
        return doctorService.createDoctor(doctor, officeId);
    }

    @PutMapping("/{id}")
    public Doctor updateDoctor(@PathVariable Long id, @RequestBody Doctor doctorDetails) {
        return doctorService.updateDoctor(id, doctorDetails);
    }

    @PutMapping("/{doctorId}/specializations/{specializationId}")
    public ResponseEntity<String> addSpecialization(@PathVariable Long doctorId,
            @PathVariable Long specializationId) {
        return ResponseEntity.ok(doctorService.addSpecialization(doctorId, specializationId));
    }

    @DeleteMapping("/{doctorId}/specializations/{specializationId}")
    public ResponseEntity<String> removeSpecialization(@PathVariable Long doctorId,
            @PathVariable Long specializationId) {
        return ResponseEntity.ok(doctorService.removeSpecialization(doctorId, specializationId));
    }

    @PatchMapping("/{doctorId}/office")
    public ResponseEntity<String> moveToOffice(@PathVariable Long doctorId,
            @RequestParam("officeNumber") Integer officeNumber) {
        return ResponseEntity.ok(doctorService.moveToOffice(doctorId, officeNumber));
    }

    @DeleteMapping("/{id}")
    public void deleteDoctor(@PathVariable Long id) {
        doctorRepository.deleteById(id);
    }
}