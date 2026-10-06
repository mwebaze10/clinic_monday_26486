package com.clinic.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;

import com.clinic.api.model.Doctor;
import com.clinic.api.model.Office;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class DoctorsWithoutOfficeRepositoryTests {
    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private OfficeRepository officeRepository;

    @Test
    void findsDoctorsWithoutOfficeSortedByLastName() {
        Doctor zuluDoctor = saveDoctor("Zulu", null);
        Doctor alphaDoctor = saveDoctor("Alpha", null);
        saveDoctor("Assigned", officeRepository.save(new Office(null, "B2 Office", 202, "Clinic")));

        List<Doctor> doctors = doctorRepository.findDoctorsWithoutOffice();

        assertEquals(List.of(alphaDoctor.getId(), zuluDoctor.getId()),
                doctors.stream().map(Doctor::getId).toList());
    }

    private Doctor saveDoctor(String lastName, Office office) {
        Doctor doctor = new Doctor();
        doctor.setName(lastName + " Doctor");
        doctor.setLastName(lastName);
        doctor.setOffice(office);
        return doctorRepository.save(doctor);
    }
}
