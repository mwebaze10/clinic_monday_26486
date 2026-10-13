package com.clinic.api.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

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
}
