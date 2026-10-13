package com.clinic.api.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.clinic.api.model.AppointmentStatus;
import com.clinic.api.model.Doctor;
import com.clinic.api.repository.DoctorRepository;
import com.clinic.api.repository.OfficeRepository;
import com.clinic.api.repository.SpecializationRepository;

class DoctorSearchTests {
    @Test
    void searchesByNameThroughDoctorRepository() {
        DoctorRepository doctorRepository = mock(DoctorRepository.class);
        List<Doctor> matches = List.of(new Doctor());
        when(doctorRepository.searchByName("gis")).thenReturn(matches);

        DoctorService doctorService = new DoctorService(doctorRepository,
                mock(OfficeRepository.class), mock(SpecializationRepository.class));

        assertSame(matches, doctorService.searchByName("gis"));
        verify(doctorRepository).searchByName("gis");
    }

    @Test
    void returnsWorkloadForCompletedAppointmentsInDateRange() {
        DoctorRepository doctorRepository = mock(DoctorRepository.class);
        List<Object[]> workload = List.<Object[]>of(new Object[] { "Aline", "Mugisha", 3L });
        LocalDate start = LocalDate.of(2026, 10, 1);
        LocalDate end = LocalDate.of(2026, 10, 31);
        when(doctorRepository.findDoctorWorkload(AppointmentStatus.COMPLETED, start, end))
                .thenReturn(workload);

        DoctorService doctorService = new DoctorService(doctorRepository,
                mock(OfficeRepository.class), mock(SpecializationRepository.class));

        assertSame(workload, doctorService.findWorkload(start, end));
        verify(doctorRepository).findDoctorWorkload(AppointmentStatus.COMPLETED, start, end);
    }
}
