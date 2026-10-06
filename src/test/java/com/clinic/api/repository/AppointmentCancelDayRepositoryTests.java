package com.clinic.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
class AppointmentCancelDayRepositoryTests {
    private static final LocalDate CANCEL_DATE = LocalDate.of(2026, 10, 20);

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Test
    void cancelsAllAppointmentsExceptCompletedForDoctorAndDate() {
        Doctor doctor = saveDoctor("C4 Doctor");
        Doctor anotherDoctor = saveDoctor("Other C4 Doctor");
        Patient patient = patientRepository.save(new Patient(
                null, "C4 Patient", null, UUID.randomUUID() + "@example.test", null));
        List<Appointment> savedAppointments = appointmentRepository.saveAll(List.of(
                appointment(doctor, patient, CANCEL_DATE, AppointmentStatus.SCHEDULED),
                appointment(doctor, patient, CANCEL_DATE, AppointmentStatus.CONFIRMED),
                appointment(doctor, patient, CANCEL_DATE, AppointmentStatus.CANCELLED),
                appointment(doctor, patient, CANCEL_DATE, AppointmentStatus.COMPLETED),
                appointment(doctor, patient, CANCEL_DATE.plusDays(1), AppointmentStatus.SCHEDULED),
                appointment(anotherDoctor, patient, CANCEL_DATE, AppointmentStatus.SCHEDULED)));

        int updated = appointmentRepository.cancelDay(
                doctor.getId(), CANCEL_DATE, AppointmentStatus.CANCELLED, AppointmentStatus.COMPLETED);

        assertEquals(3, updated);
        assertEquals(List.of(
                        AppointmentStatus.CANCELLED,
                        AppointmentStatus.CANCELLED,
                        AppointmentStatus.CANCELLED,
                        AppointmentStatus.COMPLETED,
                        AppointmentStatus.SCHEDULED,
                        AppointmentStatus.SCHEDULED),
                appointmentRepository.findAllById(savedAppointments.stream()
                                .map(Appointment::getId).toList()).stream()
                        .sorted((left, right) -> left.getId().compareTo(right.getId()))
                        .map(Appointment::getStatus).toList());
    }

    private Doctor saveDoctor(String name) {
        Doctor doctor = new Doctor();
        doctor.setName(name);
        return doctorRepository.save(doctor);
    }

    private Appointment appointment(
            Doctor doctor, Patient patient, LocalDate date, AppointmentStatus status) {
        return new Appointment(null, doctor, patient, date, status);
    }
}
