package com.clinic.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.clinic.api.model.Patient;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class PatientRepositoryTests {
    @Autowired
    private PatientRepository patientRepository;

    @Test
    void findsLastNameIgnoringCaseAndOrdersByFirstName() {
        String lastName = "Uwase-" + UUID.randomUUID();
        patientRepository.saveAll(List.of(
                new Patient(null, "Uwera", lastName, UUID.randomUUID() + "@example.test", null),
                new Patient(null, "Aline", lastName, UUID.randomUUID() + "@example.test", null),
                new Patient(null, "Benoit", "Niyonsaba-" + UUID.randomUUID(),
                        UUID.randomUUID() + "@example.test", null)));

        List<Patient> patients = patientRepository.findByLastNameIgnoreCaseOrderByNameAsc(
                lastName.toUpperCase(Locale.ROOT));

        assertEquals(List.of("Aline", "Uwera"), patients.stream().map(Patient::getName).toList());
        assertTrue(patientRepository.findByLastNameIgnoreCaseOrderByNameAsc(
                "missing-" + UUID.randomUUID()).isEmpty());
    }
}
