package com.clinic.api.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clinic.api.model.Appointment;
import com.clinic.api.model.AppointmentStatus;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByStatusOrderByAppointmentDateAsc(AppointmentStatus status);

    List<Appointment> findByAppointmentDateBetweenOrderByAppointmentDateAsc(
            LocalDate start, LocalDate end);

    boolean existsByDoctor_IdAndAppointmentDateAndStatusNot(
            Long doctorId, LocalDate appointmentDate, AppointmentStatus status);

    @Query("""
            SELECT appointment.status, COUNT(appointment)
            FROM Appointment appointment
            GROUP BY appointment.status
            """)
    List<Object[]> countAppointmentsByStatus();

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            UPDATE Appointment appointment
            SET appointment.status = :cancelledStatus
            WHERE appointment.doctor.id = :doctorId
              AND appointment.appointmentDate = :date
              AND appointment.status <> :completedStatus
            """)
    int cancelDay(@Param("doctorId") Long doctorId,
            @Param("date") LocalDate date,
            @Param("cancelledStatus") AppointmentStatus cancelledStatus,
            @Param("completedStatus") AppointmentStatus completedStatus);
}