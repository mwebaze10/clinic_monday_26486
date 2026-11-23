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
import com.clinic.api.model.Doctor;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByStatusOrderByAppointmentDateAsc(AppointmentStatus status);

    long countByAppointmentDate(LocalDate appointmentDate);

    long countByStatus(AppointmentStatus status);

    List<Appointment> findByDoctor_IdAndAppointmentDateAndStatusNot(
            Long doctorId, LocalDate appointmentDate, AppointmentStatus status);

    List<Appointment> findByPatient_SsNumberAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAsc(
            String ssNumber, LocalDate appointmentDate, List<AppointmentStatus> statuses);

    List<Appointment> findByAppointmentDateBetweenOrderByAppointmentDateAsc(LocalDate start, LocalDate end);

    boolean existsByDoctorAndAppointmentDateAndStatusNot(Doctor doctor, LocalDate appointmentDate,
            AppointmentStatus status);

    boolean existsByDoctorAndAppointmentDateAndStatusNotAndIdNot(Doctor doctor, LocalDate appointmentDate,
            AppointmentStatus status, Long appointmentId);

        @Query("SELECT a.status, COUNT(a) FROM Appointment a GROUP BY a.status")
        List<Object[]> countAppointmentsByStatus();

        @Modifying
        @Query("UPDATE Appointment a SET a.status = :cancelled "
            + "WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date AND a.status <> :completed")
        int cancelAppointmentsForDoctorOnDate(@Param("doctorId") Long doctorId, @Param("date") LocalDate date,
            @Param("completed") AppointmentStatus completed, @Param("cancelled") AppointmentStatus cancelled);

        @Modifying
        @Query("DELETE FROM Appointment a WHERE a.status = :status AND a.appointmentDate < :date")
        int deleteAppointmentsBeforeDate(@Param("status") AppointmentStatus status, @Param("date") LocalDate date);
}