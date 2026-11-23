package com.clinic.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clinic.api.model.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

	boolean existsBySsNumber(String ssNumber);

	List<Patient> findByLastNameIgnoreCaseOrderByFirstNameAsc(String lastName);

	@Query("SELECT DISTINCT a.patient FROM Appointment a WHERE a.doctor.id = :doctorId")
	List<Patient> findDistinctPatientsByDoctorId(@Param("doctorId") Long doctorId);

	@Query("SELECT a.patient FROM Appointment a GROUP BY a.patient "
			+ "HAVING COUNT(a) >= :min ORDER BY COUNT(a) DESC")
	List<Patient> findFrequentPatients(@Param("min") long min);
}