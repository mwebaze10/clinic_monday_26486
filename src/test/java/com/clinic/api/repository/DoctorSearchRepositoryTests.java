package com.clinic.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;

import com.clinic.api.model.Doctor;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class DoctorSearchRepositoryTests {
    @Autowired
    private DoctorRepository doctorRepository;

    @Test
    void searchesFirstOrLastNameCaseInsensitivelyAndSortsByLastName() {
        Doctor mugisha = saveDoctor("Mugisha", "Aline");
        Doctor giselle = saveDoctor("Uwase", "Giselle");
        saveDoctor("Other", "Person");

        List<Doctor> matches = doctorRepository.searchByName("gIs");

        assertEquals(List.of(mugisha.getId(), giselle.getId()),
                matches.stream().map(Doctor::getId).toList());
    }

    private Doctor saveDoctor(String lastName, String firstName) {
        Doctor doctor = new Doctor();
        doctor.setName(firstName);
        doctor.setLastName(lastName);
        return doctorRepository.save(doctor);
    }
}
