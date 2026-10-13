package com.clinic.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.clinic.api.dto.PrescriptionRequest;
import com.clinic.api.model.Appointment;
import com.clinic.api.model.AppointmentStatus;
import com.clinic.api.model.Prescription;
import com.clinic.api.repository.AppointmentRepository;
import com.clinic.api.repository.PrescriptionRepository;

class PrescriptionServiceTests {
    private static final Long APPOINTMENT_ID = 5L;

    private AppointmentRepository appointmentRepository;
    private PrescriptionRepository prescriptionRepository;
    private PrescriptionService prescriptionService;

    @BeforeEach
    void setUp() {
        appointmentRepository = mock(AppointmentRepository.class);
        prescriptionRepository = mock(PrescriptionRepository.class);
        prescriptionService = new PrescriptionService(appointmentRepository, prescriptionRepository);
    }

    @Test
    void savesPrescriptionForCompletedAppointmentWithGeneratedFields() {
        Appointment appointment = new Appointment();
        appointment.setId(APPOINTMENT_ID);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        when(appointmentRepository.findById(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));
        when(prescriptionRepository.save(any(Prescription.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        PrescriptionRequest request = new PrescriptionRequest(
                "Amoxicillin", "1 tablet, 3 times a day", 7);

        String result = prescriptionService.save(APPOINTMENT_ID, request);

        assertEquals("Prescription saved successfully", result);
        var prescriptionCaptor = org.mockito.ArgumentCaptor.forClass(Prescription.class);
        verify(prescriptionRepository).save(prescriptionCaptor.capture());
        Prescription saved = prescriptionCaptor.getValue();
        assertEquals(request.medicineName(), saved.getMedicineName());
        assertEquals(request.dosage(), saved.getDosage());
        assertEquals(request.durationDays(), saved.getDurationDays());
        assertEquals(LocalDate.now(), saved.getIssuedDate());
        assertEquals(appointment, saved.getAppointment());
    }

    @Test
    void rejectsPrescriptionForMissingAppointment() {
        when(appointmentRepository.findById(APPOINTMENT_ID)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> prescriptionService.save(APPOINTMENT_ID,
                        new PrescriptionRequest("Amoxicillin", "1 tablet daily", 7)));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("The appointment with that id does not exist", exception.getReason());
        verify(prescriptionRepository, never()).save(any(Prescription.class));
    }

    @Test
    void rejectsPrescriptionUnlessAppointmentIsCompleted() {
        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        when(appointmentRepository.findById(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> prescriptionService.save(APPOINTMENT_ID,
                        new PrescriptionRequest("Amoxicillin", "1 tablet daily", 7)));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Prescriptions are only for COMPLETED appointments", exception.getReason());
        verify(prescriptionRepository, never()).save(any(Prescription.class));
    }

    @Test
    void returnsAppointmentPrescriptionsIncludingAnEmptyList() {
        when(appointmentRepository.existsById(APPOINTMENT_ID)).thenReturn(true);
        List<Prescription> prescriptions = List.of(new Prescription());
        when(prescriptionRepository.findByAppointment_Id(APPOINTMENT_ID)).thenReturn(prescriptions);

        assertEquals(prescriptions, prescriptionService.findByAppointmentId(APPOINTMENT_ID));
        verify(prescriptionRepository).findByAppointment_Id(APPOINTMENT_ID);

        when(prescriptionRepository.findByAppointment_Id(APPOINTMENT_ID)).thenReturn(List.of());
        assertEquals(List.of(), prescriptionService.findByAppointmentId(APPOINTMENT_ID));
    }

    @Test
    void rejectsPrescriptionLookupForMissingAppointment() {
        when(appointmentRepository.existsById(APPOINTMENT_ID)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> prescriptionService.findByAppointmentId(APPOINTMENT_ID));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("The appointment with that id does not exist", exception.getReason());
        verify(prescriptionRepository, never()).findByAppointment_Id(APPOINTMENT_ID);
    }
}
