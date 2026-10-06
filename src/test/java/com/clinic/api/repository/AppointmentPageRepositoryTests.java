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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.clinic.api.model.Appointment;
import com.clinic.api.model.AppointmentStatus;
import com.clinic.api.model.Doctor;
import com.clinic.api.model.Patient;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class AppointmentPageRepositoryTests {
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Test
    void returnsRequestedSortedPageAndTotalMetadata() {
        Doctor doctor = new Doctor();
        doctor.setName("Bonus Page Doctor");
        doctor = doctorRepository.save(doctor);
        Patient patient = patientRepository.save(new Patient(
                null, "Bonus Page Patient", null, UUID.randomUUID() + "@example.test", null));
        appointmentRepository.saveAll(List.of(
                appointment(doctor, patient, LocalDate.of(2098, 10, 1)),
                appointment(doctor, patient, LocalDate.of(2098, 10, 2)),
                appointment(doctor, patient, LocalDate.of(2098, 10, 3))));

        long totalElements = appointmentRepository.count();
        int pageSize = 2;
        Page<Appointment> firstPage = appointmentRepository.findAll(
                PageRequest.of(0, pageSize, Sort.by(Sort.Direction.DESC, "appointmentDate")));
        Page<Appointment> secondPage = appointmentRepository.findAll(
                PageRequest.of(1, pageSize, Sort.by(Sort.Direction.DESC, "appointmentDate")));

        assertEquals(totalElements, firstPage.getTotalElements());
        assertEquals((int) Math.ceil((double) totalElements / pageSize), firstPage.getTotalPages());
        assertEquals(pageSize, firstPage.getNumberOfElements());
        assertEquals(LocalDate.of(2098, 10, 3), firstPage.getContent().get(0).getAppointmentDate());
        assertEquals(LocalDate.of(2098, 10, 1), secondPage.getContent().get(0).getAppointmentDate());
    }

    private Appointment appointment(Doctor doctor, Patient patient, LocalDate date) {
        return new Appointment(null, doctor, patient, date, AppointmentStatus.SCHEDULED);
    }
}
