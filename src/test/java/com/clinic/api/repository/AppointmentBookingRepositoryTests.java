package com.clinic.api.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
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
class AppointmentBookingRepositoryTests {
    private static final LocalDate APPOINTMENT_DATE = LocalDate.of(2026, 10, 20);

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Test
    void checksForNonCancelledAppointmentsForDoctorOnDate() {
        Doctor doctor = doctorRepository.save(new Doctor(null, "A4 Doctor", "General", null));
        Patient patient = patientRepository.save(new Patient(
                null, "A4 Patient", null, UUID.randomUUID() + "@example.test", null));

        assertFalse(isBooked(doctor));
        appointmentRepository.save(appointment(doctor, patient, AppointmentStatus.CANCELLED));
        assertFalse(isBooked(doctor));

        appointmentRepository.save(appointment(doctor, patient, AppointmentStatus.SCHEDULED));
        assertTrue(isBooked(doctor));
    }

    private boolean isBooked(Doctor doctor) {
        return appointmentRepository.existsByDoctor_IdAndAppointmentDateAndStatusNot(
                doctor.getId(), APPOINTMENT_DATE, AppointmentStatus.CANCELLED);
    }

    private Appointment appointment(Doctor doctor, Patient patient, AppointmentStatus status) {
        return new Appointment(null, doctor, patient, APPOINTMENT_DATE, status);
    }
}
