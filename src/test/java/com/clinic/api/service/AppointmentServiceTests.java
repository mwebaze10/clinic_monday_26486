package com.clinic.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.clinic.api.model.Appointment;
import com.clinic.api.model.AppointmentStatus;
import com.clinic.api.model.Doctor;
import com.clinic.api.model.Patient;
import com.clinic.api.repository.AppointmentRepository;
import com.clinic.api.repository.DoctorRepository;
import com.clinic.api.repository.PatientRepository;

class AppointmentServiceTests {
    private static final Long DOCTOR_ID = 1L;
    private static final Long PATIENT_ID = 2L;
    private static final LocalDate APPOINTMENT_DATE = LocalDate.of(2026, 10, 20);

    private AppointmentRepository appointmentRepository;
    private DoctorRepository doctorRepository;
    private PatientRepository patientRepository;
    private AppointmentService appointmentService;

    @BeforeEach
    void setUp() {
        appointmentRepository = mock(AppointmentRepository.class);
        doctorRepository = mock(DoctorRepository.class);
        patientRepository = mock(PatientRepository.class);
        appointmentService = new AppointmentService(
                appointmentRepository, doctorRepository, patientRepository);
        when(doctorRepository.findById(DOCTOR_ID)).thenReturn(Optional.of(new Doctor()));
        when(patientRepository.findById(PATIENT_ID)).thenReturn(Optional.of(new Patient()));
    }

    @Test
    void rejectsBookingWhenDoctorAlreadyHasNonCancelledAppointment() {
        when(appointmentRepository.existsByDoctor_IdAndAppointmentDateAndStatusNot(
                DOCTOR_ID, APPOINTMENT_DATE, AppointmentStatus.CANCELLED)).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> appointmentService.createAppointment(DOCTOR_ID, PATIENT_ID, APPOINTMENT_DATE));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Doctor is already booked on that date", exception.getReason());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void savesAppointmentWhenDoctorHasNoActiveBookingOnDate() {
        when(appointmentRepository.existsByDoctor_IdAndAppointmentDateAndStatusNot(
                DOCTOR_ID, APPOINTMENT_DATE, AppointmentStatus.CANCELLED)).thenReturn(false);
        when(appointmentRepository.save(any(Appointment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Appointment appointment = appointmentService.createAppointment(
                DOCTOR_ID, PATIENT_ID, APPOINTMENT_DATE);

        assertEquals(APPOINTMENT_DATE, appointment.getAppointmentDate());
        assertEquals(AppointmentStatus.SCHEDULED, appointment.getStatus());
        verify(appointmentRepository).save(appointment);
    }
}
