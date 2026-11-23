package com.clinic.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.clinic.api.model.Specialization;
import com.clinic.api.repository.SpecializationRepository;

@Service
public class SpecializationService {

    private final SpecializationRepository specializationRepository;

    public SpecializationService(SpecializationRepository specializationRepository) {
        this.specializationRepository = specializationRepository;
    }

    public List<Specialization> findAll() {
        return specializationRepository.findAll();
    }

    public List<Specialization> findUnused() {
        return specializationRepository.findUnusedSpecializations();
    }

    public Optional<Specialization> findById(Long id) {
        return specializationRepository.findById(id);
    }

    public Specialization save(Specialization specialization) {
        return specializationRepository.save(specialization);
    }

    public Optional<Specialization> update(Long id, Specialization specializationDetails) {
        return specializationRepository.findById(id).map(specialization -> {
            specialization.setName(specializationDetails.getName());
            return specializationRepository.save(specialization);
        });
    }

    public void deleteById(Long id) {
        specializationRepository.deleteById(id);
    }
}