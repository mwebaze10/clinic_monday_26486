package com.clinic.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clinic.api.model.Office;

@Repository
public interface OfficeRepository extends JpaRepository<Office, Long> {
}