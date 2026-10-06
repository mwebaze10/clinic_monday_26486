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
import org.springframework.data.domain.PageRequest;

import com.clinic.api.model.Appointment;
import com.clinic.api.model.AppointmentStatus;
import com.clinic.api.model.Doctor;
import com.clinic.api.model.Office;
import com.clinic.api.model.Patient;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class BusiestOfficeRepositoryTests {
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private OfficeRepository officeRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Test
    void returnsOfficeNameNumberAndHighestAppointmentCount() {
        List<Object[]> currentTop = officeRepository.findOfficesByAppointmentCount(PageRequest.of(0, 1));
        long currentMaximum = currentTop.isEmpty() ? 0L : ((Number) currentTop.get(0)[2]).longValue();

        Office busiestOffice = officeRepository.save(new Office(null, "C3 Busiest", 303, "Clinic"));
        Doctor doctor = new Doctor();
        doctor.setName("C3 Doctor");
        doctor.setOffice(busiestOffice);
        doctor = doctorRepository.save(doctor);
        Patient patient = patientRepository.save(new Patient(
                null, "C3 Patient", null, UUID.randomUUID() + "@example.test", null));
        int appointmentsToCreate = Math.toIntExact(currentMaximum + 1);
        for (int day = 1; day <= appointmentsToCreate; day++) {
            appointmentRepository.save(new Appointment(null, doctor, patient,
                    LocalDate.of(2026, 10, 1).plusDays(day - 1), AppointmentStatus.SCHEDULED));
        }

        List<Object[]> result = officeRepository.findOfficesByAppointmentCount(PageRequest.of(0, 1));

        assertEquals(1, result.size());
        assertEquals("C3 Busiest", result.get(0)[0]);
        assertEquals(303, ((Number) result.get(0)[1]).intValue());
        assertEquals(currentMaximum + appointmentsToCreate,
                ((Number) result.get(0)[2]).longValue());
    }
}
