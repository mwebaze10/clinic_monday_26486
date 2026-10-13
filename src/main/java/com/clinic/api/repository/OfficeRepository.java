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
    @Query("""
            SELECT office.name, office.officeNumber, COUNT(appointment)
            FROM Appointment appointment
            JOIN appointment.doctor doctor
            JOIN doctor.office office
            GROUP BY office.id, office.name, office.officeNumber
            ORDER BY COUNT(appointment) DESC, office.name ASC
            """)
    List<Object[]> findOfficesByAppointmentCount(Pageable pageable);

    @Query("""
            SELECT office FROM Office office
            WHERE NOT EXISTS (
                SELECT doctor FROM Doctor doctor
                WHERE doctor.office = office
            )
            """)
    List<Office> findFreeOffices();

    boolean existsByOfficeNumber(Integer officeNumber);

    Optional<Office> findByOfficeNumber(Integer officeNumber);
}