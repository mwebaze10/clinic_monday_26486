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
import com.clinic.api.model.Prescription;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class PrescriptionPatientHistoryRepositoryTests {
    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Test
    void findsAllPatientPrescriptionsAcrossAppointmentsNewestFirst() {
        Doctor doctor = new Doctor();
        doctor.setName("History Doctor");
        doctor = doctorRepository.save(doctor);

        Patient patient = new Patient(null, "History Patient", null,
                UUID.randomUUID() + "@example.test", null);
        patient.setSsNumber("history-" + UUID.randomUUID());
        patient = patientRepository.save(patient);

        Patient anotherPatient = new Patient(null, "Other Patient", null,
                UUID.randomUUID() + "@example.test", null);
        anotherPatient.setSsNumber("other-" + UUID.randomUUID());
        anotherPatient = patientRepository.save(anotherPatient);

        Appointment firstAppointment = appointmentRepository.save(new Appointment(
                null, doctor, patient, LocalDate.now().minusDays(4), AppointmentStatus.COMPLETED));
        Appointment secondAppointment = appointmentRepository.save(new Appointment(
                null, doctor, patient, LocalDate.now().minusDays(2), AppointmentStatus.COMPLETED));
        Appointment otherAppointment = appointmentRepository.save(new Appointment(
                null, doctor, anotherPatient, LocalDate.now(), AppointmentStatus.COMPLETED));

        prescriptionRepository.saveAllAndFlush(List.of(
                prescription("Older", LocalDate.now().minusDays(4), firstAppointment),
                prescription("Newer", LocalDate.now().minusDays(1), secondAppointment),
                prescription("Other patient", LocalDate.now(), otherAppointment)));

        List<Prescription> history = prescriptionRepository
                .findByPatientSsNumberOrderByIssuedDateDesc(patient.getSsNumber());

        assertEquals(List.of("Newer", "Older"),
                history.stream().map(Prescription::getMedicineName).toList());
        assertEquals(List.of(), prescriptionRepository
                .findByPatientSsNumberOrderByIssuedDateDesc("missing-" + UUID.randomUUID()));
    }

    private Prescription prescription(String medicineName, LocalDate issuedDate, Appointment appointment) {
        return new Prescription(null, medicineName, "1 tablet daily", 3, issuedDate, appointment);
    }
}
