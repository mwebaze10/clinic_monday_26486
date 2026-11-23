package com.clinic.api.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.api.model.Specialization;
import com.clinic.api.service.SpecializationService;

@RestController
@RequestMapping("/api/specializations")
public class SpecializationController {

    private final SpecializationService specializationService;

    public SpecializationController(SpecializationService specializationService) {
        this.specializationService = specializationService;
    }

    @GetMapping
    public List<Specialization> getAllSpecializations() {
        return specializationService.findAll();
    }

    @GetMapping("/unused")
    public List<Specialization> getUnusedSpecializations() {
        return specializationService.findUnused();
    }

    @GetMapping("/{id}")
    public Optional<Specialization> getSpecializationById(@PathVariable Long id) {
        return specializationService.findById(id);
    }

    @PostMapping
    public Specialization createSpecialization(@RequestBody Specialization specialization) {
        return specializationService.save(specialization);
    }

    @PutMapping("/{id}")
    public Specialization updateSpecialization(@PathVariable Long id,
            @RequestBody Specialization specializationDetails) {
        return specializationService.update(id, specializationDetails).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deleteSpecialization(@PathVariable Long id) {
        specializationService.deleteById(id);
    }
}