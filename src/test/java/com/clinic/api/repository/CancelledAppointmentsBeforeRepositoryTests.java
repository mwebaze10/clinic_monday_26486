package com.clinic.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
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
class CancelledAppointmentsBeforeRepositoryTests {
    private static final LocalDate CUTOFF_DATE = LocalDate.of(2026, 10, 15);

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Test
    void deletesOnlyCancelledAppointmentsStrictlyBeforeDate() {
        Doctor doctor = new Doctor();
        doctor.setName("Bonus Delete Doctor");
        doctor = doctorRepository.save(doctor);
        Patient patient = patientRepository.save(new Patient(
                null, "Bonus Delete Patient", null, UUID.randomUUID() + "@example.test", null));
        List<Appointment> savedAppointments = appointmentRepository.saveAll(List.of(
                appointment(doctor, patient, CUTOFF_DATE.minusDays(1), AppointmentStatus.CANCELLED),
                appointment(doctor, patient, CUTOFF_DATE.minusDays(1), AppointmentStatus.SCHEDULED),
                appointment(doctor, patient, CUTOFF_DATE, AppointmentStatus.CANCELLED),
                appointment(doctor, patient, CUTOFF_DATE.plusDays(1), AppointmentStatus.CANCELLED)));

        int deleted = appointmentRepository.deleteCancelledBefore(
                CUTOFF_DATE, AppointmentStatus.CANCELLED);

        assertEquals(1, deleted);
        List<Long> remainingIds = appointmentRepository.findAllById(savedAppointments.stream()
                        .map(Appointment::getId).toList()).stream()
                .map(Appointment::getId).toList();
        assertFalse(remainingIds.contains(savedAppointments.get(0).getId()));
        assertTrue(remainingIds.containsAll(List.of(
                savedAppointments.get(1).getId(),
                savedAppointments.get(2).getId(),
                savedAppointments.get(3).getId())));
    }

    private Appointment appointment(
            Doctor doctor, Patient patient, LocalDate date, AppointmentStatus status) {
        return new Appointment(null, doctor, patient, date, status);
    }
}
