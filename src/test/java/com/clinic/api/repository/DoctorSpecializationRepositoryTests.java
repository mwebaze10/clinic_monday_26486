package com.clinic.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedHashSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;

import com.clinic.api.model.Doctor;
import com.clinic.api.model.Specialization;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class DoctorSpecializationRepositoryTests {
    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private SpecializationRepository specializationRepository;

    @Test
    void findsDoctorsBySpecializationIgnoringCaseWithoutDuplicates() {
        Specialization cardiology = specializationRepository.save(
                new Specialization(null, "Cardiology"));
        Specialization cardiologyUpperCase = specializationRepository.save(
                new Specialization(null, "CARDIOLOGY"));
        Doctor matchingDoctor = new Doctor();
        matchingDoctor.setName("B1 Doctor");
        matchingDoctor.setSpecializations(
                new LinkedHashSet<>(List.of(cardiology, cardiologyUpperCase)));
        doctorRepository.save(matchingDoctor);

        Doctor otherDoctor = new Doctor();
        otherDoctor.setName("Other Doctor");
        otherDoctor.setSpecializations(new LinkedHashSet<>(List.of(
                specializationRepository.save(new Specialization(null, "Neurology")))));
        doctorRepository.save(otherDoctor);

        List<Doctor> doctors = doctorRepository.findBySpecializationName("cArDiOlOgY");

        assertEquals(List.of(matchingDoctor.getId()),
                doctors.stream().map(Doctor::getId).toList());
    }
}
