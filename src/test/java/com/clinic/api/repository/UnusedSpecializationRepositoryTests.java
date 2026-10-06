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
class UnusedSpecializationRepositoryTests {
    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private SpecializationRepository specializationRepository;

    @Test
    void findsSpecializationsWithNoDoctors() {
        Specialization offered = specializationRepository.save(
                new Specialization(null, "B3 Offered"));
        Specialization unused = specializationRepository.save(
                new Specialization(null, "B3 Unused"));
        Doctor doctor = new Doctor();
        doctor.setName("B3 Doctor");
        doctor.setSpecializations(new LinkedHashSet<>(List.of(offered)));
        doctorRepository.save(doctor);

        List<Specialization> specializations =
                specializationRepository.findUnusedSpecializations();

        assertEquals(List.of(unused.getId()),
                specializations.stream().map(Specialization::getId).toList());
    }
}
