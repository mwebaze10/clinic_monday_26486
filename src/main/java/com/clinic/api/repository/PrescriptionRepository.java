package com.clinic.api.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clinic.api.model.Prescription;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {
    List<Prescription> findByAppointment_Id(Long appointmentId);

    @Query("""
            SELECT prescription
            FROM Prescription prescription
            WHERE prescription.appointment.patient.ssNumber = :ssNumber
            ORDER BY prescription.issuedDate DESC
            """)
    List<Prescription> findByPatientSsNumberOrderByIssuedDateDesc(
            @Param("ssNumber") String ssNumber);
}
