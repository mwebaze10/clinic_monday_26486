package com.clinic.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clinic.api.model.Specialization;

@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, Long> {
    Optional<Specialization> findByNameIgnoreCase(String name);
}
