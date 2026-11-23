package com.clinic.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.clinic.api.model.Office;

@Repository
public interface OfficeRepository extends JpaRepository<Office, Long> {

	Optional<Office> findByOfficeNumber(Integer officeNumber);

	@Query("SELECT o.name, o.officeNumber, COUNT(a) "
			+ "FROM Appointment a JOIN a.doctor d JOIN d.office o "
			+ "GROUP BY o.name, o.officeNumber ORDER BY COUNT(a) DESC")
	List<Object[]> findBusiestOffice(Pageable pageable);
}