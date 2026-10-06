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
class AppointmentStatusRepositoryTests {
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Test
    void findsOnlyRequestedStatusOrderedByAppointmentDate() {
        Doctor doctor = new Doctor();
        doctor.setName("A2 Doctor");
        doctor = doctorRepository.save(doctor);
        Patient patient = patientRepository.save(new Patient(
                null, "A2 Patient", null, UUID.randomUUID() + "@example.test", null));
        appointmentRepository.saveAll(List.of(
                appointment(doctor, patient, LocalDate.of(2026, 10, 20), AppointmentStatus.SCHEDULED),
                appointment(doctor, patient, LocalDate.of(2026, 10, 10), AppointmentStatus.SCHEDULED),
                appointment(doctor, patient, LocalDate.of(2026, 10, 5), AppointmentStatus.CONFIRMED)));

        List<Appointment> scheduled = appointmentRepository
                .findByStatusOrderByAppointmentDateAsc(AppointmentStatus.SCHEDULED);

        assertEquals(List.of(LocalDate.of(2026, 10, 10), LocalDate.of(2026, 10, 20)),
                scheduled.stream().map(Appointment::getAppointmentDate).toList());
        assertEquals(AppointmentStatus.SCHEDULED,
                scheduled.get(0).getStatus());
        assertEquals(0, appointmentRepository
                .findByStatusOrderByAppointmentDateAsc(AppointmentStatus.CANCELLED).size());
    }

    private Appointment appointment(
            Doctor doctor, Patient patient, LocalDate date, AppointmentStatus status) {
        return new Appointment(null, doctor, patient, date, status);
    }
}
