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
class AppointmentDateRangeRepositoryTests {
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Test
    void findsAppointmentsOnBothRangeBoundariesOrderedByDate() {
        Doctor doctor = new Doctor();
        doctor.setName("A3 Doctor");
        doctor = doctorRepository.save(doctor);
        Patient patient = patientRepository.save(new Patient(
                null, "A3 Patient", null, UUID.randomUUID() + "@example.test", null));
        appointmentRepository.saveAll(List.of(
                appointment(doctor, patient, LocalDate.of(2026, 10, 1)),
                appointment(doctor, patient, LocalDate.of(2026, 10, 15)),
                appointment(doctor, patient, LocalDate.of(2026, 10, 31)),
                appointment(doctor, patient, LocalDate.of(2026, 9, 30)),
                appointment(doctor, patient, LocalDate.of(2026, 11, 1))));

        List<Appointment> appointments = appointmentRepository
                .findByAppointmentDateBetweenOrderByAppointmentDateAsc(
                        LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31));

        assertEquals(List.of(
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 15),
                LocalDate.of(2026, 10, 31)),
                appointments.stream().map(Appointment::getAppointmentDate).toList());
    }

    private Appointment appointment(Doctor doctor, Patient patient, LocalDate date) {
        return new Appointment(null, doctor, patient, date, AppointmentStatus.SCHEDULED);
    }
}
