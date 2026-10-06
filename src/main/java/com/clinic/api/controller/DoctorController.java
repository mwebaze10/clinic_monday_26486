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

import com.clinic.api.model.Doctor;
import com.clinic.api.repository.DoctorRepository;
import com.clinic.api.service.DoctorService;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorRepository doctorRepository;
    private final DoctorService doctorService;

    public DoctorController(DoctorRepository doctorRepository, DoctorService doctorService) {
        this.doctorRepository = doctorRepository;
        this.doctorService = doctorService;
    }

    @GetMapping
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    @GetMapping("/by-specialization")
    public List<Doctor> getDoctorsBySpecialization(@RequestParam("name") String name) {
        return doctorService.findBySpecialization(name);
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

    @DeleteMapping("/{id}")
    public void deleteDoctor(@PathVariable Long id) {
        doctorRepository.deleteById(id);
    }
}