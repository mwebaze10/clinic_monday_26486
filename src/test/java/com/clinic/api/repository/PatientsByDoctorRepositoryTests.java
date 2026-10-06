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
class PatientsByDoctorRepositoryTests {
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Test
    void returnsEachPatientOnceForAppointmentsWithRequestedDoctor() {
        Doctor doctor = saveDoctor("B4 Doctor");
        Doctor anotherDoctor = saveDoctor("Other B4 Doctor");
        Patient patientWithMultipleAppointments = savePatient("Multiple");
        Patient patientWithOneAppointment = savePatient("Single");
        Patient patientOfAnotherDoctor = savePatient("Other");

        appointmentRepository.saveAll(List.of(
                appointment(doctor, patientWithMultipleAppointments, 1),
                appointment(doctor, patientWithMultipleAppointments, 2),
                appointment(doctor, patientWithOneAppointment, 3),
                appointment(anotherDoctor, patientOfAnotherDoctor, 4)));

        List<Patient> patients = patientRepository.findPatientsByDoctorId(doctor.getId());

        assertEquals(List.of(patientWithMultipleAppointments.getId(), patientWithOneAppointment.getId()),
                patients.stream().map(Patient::getId).sorted().toList());
    }

    private Doctor saveDoctor(String name) {
        Doctor doctor = new Doctor();
        doctor.setName(name);
        return doctorRepository.save(doctor);
    }

    private Patient savePatient(String name) {
        return patientRepository.save(new Patient(
                null, name, "B4", UUID.randomUUID() + "@example.test", null));
    }

    private Appointment appointment(Doctor doctor, Patient patient, int day) {
        return new Appointment(null, doctor, patient, LocalDate.of(2026, 10, day),
                AppointmentStatus.SCHEDULED);
    }
}
