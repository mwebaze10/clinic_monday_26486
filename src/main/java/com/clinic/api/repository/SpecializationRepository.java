package com.clinic.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.clinic.api.model.Specialization;

public interface SpecializationRepository extends JpaRepository<Specialization, Long> {

	@Query("SELECT s FROM Specialization s WHERE s.doctors IS EMPTY")
	List<Specialization> findUnusedSpecializations();
}