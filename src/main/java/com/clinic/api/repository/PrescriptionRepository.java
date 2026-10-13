package com.clinic.api.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clinic.api.model.Prescription;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {
}
