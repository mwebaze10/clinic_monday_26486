package com.clinic.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clinic.api.model.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByLastNameIgnoreCaseOrderByNameAsc(String lastName);

    @Query("""
            SELECT DISTINCT appointment.patient
            FROM Appointment appointment
            WHERE appointment.doctor.id = :doctorId
            """)
    List<Patient> findPatientsByDoctorId(@Param("doctorId") Long doctorId);

    @Query("""
            SELECT appointment.patient
            FROM Appointment appointment
            GROUP BY appointment.patient
            HAVING COUNT(appointment) >= :minAppointments
            ORDER BY COUNT(appointment) DESC
            """)
    List<Patient> findFrequentPatients(@Param("minAppointments") long minAppointments);
}