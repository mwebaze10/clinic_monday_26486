package com.clinic.api.repository;

import java.util.List;
import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clinic.api.model.Doctor;
import com.clinic.api.model.AppointmentStatus;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    @Query("""
            SELECT DISTINCT d
            FROM Doctor d
            JOIN FETCH d.specializations s
            WHERE LOWER(s.name) = LOWER(:name)
            ORDER BY d.name
            """)
    List<Doctor> findBySpecializationName(@Param("name") String name);

    @Query("""
            SELECT d
            FROM Doctor d
            WHERE d.office IS NULL
            ORDER BY d.lastName ASC
            """)
    List<Doctor> findDoctorsWithoutOffice();

    @Query("""
            SELECT d
            FROM Doctor d
            WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))
               OR LOWER(d.lastName) LIKE LOWER(CONCAT('%', :name, '%'))
            ORDER BY d.lastName ASC
            """)
    List<Doctor> searchByName(@Param("name") String name);

    @Query("""
            SELECT doctor.name, doctor.lastName, COUNT(appointment)
            FROM Appointment appointment
            JOIN appointment.doctor doctor
            WHERE appointment.status = :status
              AND appointment.appointmentDate BETWEEN :start AND :end
            GROUP BY doctor.id, doctor.name, doctor.lastName
            ORDER BY COUNT(appointment) DESC
            """)
    List<Object[]> findDoctorWorkload(@Param("status") AppointmentStatus status,
            @Param("start") LocalDate start, @Param("end") LocalDate end);
}