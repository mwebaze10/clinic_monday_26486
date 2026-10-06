package com.clinic.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.clinic.api.repository.DoctorRepository;
import com.clinic.api.repository.PatientRepository;

class PatientServiceTests {
    private static final Long MISSING_DOCTOR_ID = 24680L;

    private PatientRepository patientRepository;
    private DoctorRepository doctorRepository;
    private PatientService patientService;

    @BeforeEach
    void setUp() {
        patientRepository = mock(PatientRepository.class);
        doctorRepository = mock(DoctorRepository.class);
        patientService = new PatientService(patientRepository, doctorRepository);
    }

    @Test
    void returnsNotFoundWhenDoctorDoesNotExist() {
        when(doctorRepository.existsById(MISSING_DOCTOR_ID)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> patientService.findByDoctorId(MISSING_DOCTOR_ID));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("The doctor with that id does not exist", exception.getReason());
        verify(patientRepository, never()).findPatientsByDoctorId(MISSING_DOCTOR_ID);
    }
}
