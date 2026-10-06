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
class FrequentPatientsRepositoryTests {
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Test
    void returnsPatientEntitiesMeetingMinimumOrderedByAppointmentCount() {
        Doctor doctor = new Doctor();
        doctor.setName("C2 Doctor");
        doctor = doctorRepository.save(doctor);
        Patient mostFrequent = savePatient("Most frequent");
        Patient lessFrequent = savePatient("Less frequent");
        Patient belowThreshold = savePatient("Below threshold");

        appointmentRepository.saveAll(List.of(
                appointment(doctor, mostFrequent, 1),
                appointment(doctor, mostFrequent, 2),
                appointment(doctor, mostFrequent, 3),
                appointment(doctor, lessFrequent, 4),
                appointment(doctor, lessFrequent, 5),
                appointment(doctor, belowThreshold, 6)));

        List<Patient> patients = patientRepository.findFrequentPatients(2);

        assertEquals(List.of(mostFrequent.getId(), lessFrequent.getId()),
                patients.stream().map(Patient::getId).toList());
    }

    private Patient savePatient(String name) {
        return patientRepository.save(new Patient(
                null, name, "C2", UUID.randomUUID() + "@example.test", null));
    }

    private Appointment appointment(Doctor doctor, Patient patient, int day) {
        return new Appointment(null, doctor, patient, LocalDate.of(2026, 10, day),
                AppointmentStatus.SCHEDULED);
    }
}
