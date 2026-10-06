package com.clinic.api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.clinic.api.model.Specialization;
import com.clinic.api.repository.SpecializationRepository;

@Service
public class SpecializationService {
    private final SpecializationRepository specializationRepository;

    public SpecializationService(SpecializationRepository specializationRepository) {
        this.specializationRepository = specializationRepository;
    }

    public List<Specialization> findUnused() {
        return specializationRepository.findUnusedSpecializations();
    }
}
