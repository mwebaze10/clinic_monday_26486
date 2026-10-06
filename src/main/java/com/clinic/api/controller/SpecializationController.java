package com.clinic.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/unused")
    public List<Specialization> getUnusedSpecializations() {
        return specializationService.findUnused();
    }
}
