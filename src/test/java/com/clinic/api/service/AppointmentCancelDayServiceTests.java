package com.clinic.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.clinic.api.model.AppointmentStatus;
import com.clinic.api.repository.AppointmentRepository;
import com.clinic.api.repository.DoctorRepository;
import com.clinic.api.repository.PatientRepository;

class AppointmentCancelDayServiceTests {
    @Test
    void returnsRealNumberOfCancelledAppointments() {
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        when(appointmentRepository.cancelDay(12L, LocalDate.of(2026, 10, 20),
                AppointmentStatus.CANCELLED, AppointmentStatus.COMPLETED)).thenReturn(3);
        AppointmentService appointmentService = new AppointmentService(
                appointmentRepository, mock(DoctorRepository.class), mock(PatientRepository.class));

        assertEquals("3 appointments cancelled",
                appointmentService.cancelDay(12L, LocalDate.of(2026, 10, 20)));
        verify(appointmentRepository).cancelDay(12L, LocalDate.of(2026, 10, 20),
                AppointmentStatus.CANCELLED, AppointmentStatus.COMPLETED);
    }
}
