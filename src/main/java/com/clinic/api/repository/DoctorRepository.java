package com.clinic.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clinic.api.model.Doctor;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}