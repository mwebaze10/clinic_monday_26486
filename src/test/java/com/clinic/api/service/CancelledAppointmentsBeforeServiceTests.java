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

class CancelledAppointmentsBeforeServiceTests {
    @Test
    void returnsActualNumberOfDeletedAppointments() {
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        LocalDate cutoffDate = LocalDate.of(2026, 10, 15);
        when(appointmentRepository.deleteCancelledBefore(cutoffDate, AppointmentStatus.CANCELLED))
                .thenReturn(2);
        AppointmentService appointmentService = new AppointmentService(
                appointmentRepository, mock(DoctorRepository.class), mock(PatientRepository.class));

        assertEquals("2 appointments deleted",
                appointmentService.deleteCancelledBefore(cutoffDate));
        verify(appointmentRepository).deleteCancelledBefore(cutoffDate, AppointmentStatus.CANCELLED);
    }
}
