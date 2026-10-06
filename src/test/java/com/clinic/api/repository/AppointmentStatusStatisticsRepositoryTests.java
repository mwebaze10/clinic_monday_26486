package com.clinic.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;

import com.clinic.api.model.Appointment;
import com.clinic.api.model.AppointmentStatus;
import com.clinic.api.model.Doctor;
import com.clinic.api.model.Patient;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class AppointmentStatusStatisticsRepositoryTests {
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Test
    void groupsAppointmentCountsByStatusesThatExist() {
        Doctor doctor = new Doctor();
        doctor.setName("C1 Doctor");
        doctor = doctorRepository.save(doctor);
        Patient patient = patientRepository.save(new Patient(
                null, "C1 Patient", null, UUID.randomUUID() + "@example.test", null));
        appointmentRepository.saveAll(List.of(
                appointment(doctor, patient, AppointmentStatus.SCHEDULED),
                appointment(doctor, patient, AppointmentStatus.SCHEDULED),
                appointment(doctor, patient, AppointmentStatus.COMPLETED)));

        Map<AppointmentStatus, Long> counts = new HashMap<>();
        for (Object[] row : appointmentRepository.countAppointmentsByStatus()) {
            counts.put((AppointmentStatus) row[0], ((Number) row[1]).longValue());
        }

        assertEquals(Map.of(
                AppointmentStatus.SCHEDULED, 2L,
                AppointmentStatus.COMPLETED, 1L), counts);
    }

    private Appointment appointment(Doctor doctor, Patient patient, AppointmentStatus status) {
        return new Appointment(null, doctor, patient, LocalDate.of(2026, 10, 5), status);
    }
}
