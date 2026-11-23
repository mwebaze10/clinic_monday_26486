package com.clinic.api.repository;

import java.util.List;
import java.time.LocalDate;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clinic.api.model.Doctor;
import com.clinic.api.model.AppointmentStatus;
import com.clinic.api.model.Office;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

	@Query("SELECT DISTINCT d FROM Doctor d JOIN d.specializations s WHERE LOWER(s.name) = LOWER(:name)")
	List<Doctor> findBySpecializationName(@Param("name") String name);

	@Query("SELECT d FROM Doctor d WHERE d.office IS NULL ORDER BY d.lastName ASC")
	List<Doctor> findWithoutOffice();

	@Query("SELECT d FROM Doctor d WHERE "
			+ "LOWER(d.firstName) LIKE LOWER(CONCAT('%', :name, '%')) "
			+ "OR LOWER(d.lastName) LIKE LOWER(CONCAT('%', :name, '%')) "
			+ "ORDER BY d.lastName ASC")
	List<Doctor> searchByName(@Param("name") String name);

	@Query("SELECT d.firstName, d.lastName, COUNT(a) FROM Doctor d "
			+ "JOIN Appointment a ON a.doctor = d "
			+ "WHERE a.status = :status AND a.appointmentDate BETWEEN :start AND :end "
			+ "GROUP BY d.id, d.firstName, d.lastName ORDER BY COUNT(a) DESC")
	List<Object[]> findDoctorWorkload(@Param("start") LocalDate start, @Param("end") LocalDate end,
			@Param("status") AppointmentStatus status);

	@Query("SELECT o.name, o.officeNumber, COUNT(a) "
			+ "FROM Appointment a JOIN a.doctor d JOIN d.office o "
			+ "GROUP BY o.name, o.officeNumber ORDER BY COUNT(a) DESC")
	List<Object[]> findBusiestOffice(Pageable pageable);

	@Query("SELECT o FROM Office o WHERE NOT EXISTS (SELECT d FROM Doctor d WHERE d.office = o) ORDER BY o.officeNumber ASC")
	List<Office> findFreeOffices();

	boolean existsByOfficeAndIdNot(Office office, Long doctorId);

	boolean existsByOffice_Id(Long officeId);
}