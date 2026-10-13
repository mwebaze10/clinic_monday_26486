package com.clinic.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.clinic.api.dto.PrescriptionRequest;
import com.clinic.api.model.Prescription;
import com.clinic.api.service.PrescriptionService;

class PrescriptionControllerTests {
    @Test
    void returnsPrescriptionsForAppointment() {
        PrescriptionService prescriptionService = mock(PrescriptionService.class);
        PrescriptionController controller = new PrescriptionController(prescriptionService);
        List<Prescription> prescriptions = List.of(new Prescription());
        when(prescriptionService.findByAppointmentId(5L)).thenReturn(prescriptions);

        assertEquals(prescriptions, controller.findByAppointment(5L));
        verify(prescriptionService).findByAppointmentId(5L);
    }

    @Test
    void returnsPatientPrescriptionHistory() {
        PrescriptionService prescriptionService = mock(PrescriptionService.class);
        PrescriptionController controller = new PrescriptionController(prescriptionService);
        String ssNumber = "1234567890123456";
        List<Prescription> prescriptions = List.of(new Prescription());
        when(prescriptionService.findByPatientSsNumber(ssNumber)).thenReturn(prescriptions);

        assertEquals(prescriptions, controller.findByPatient(ssNumber));
        verify(prescriptionService).findByPatientSsNumber(ssNumber);
    }

    @Test
    void savesPrescriptionAndReturnsCreatedResponse() {
        PrescriptionService prescriptionService = mock(PrescriptionService.class);
        PrescriptionController controller = new PrescriptionController(prescriptionService);
        PrescriptionRequest request = new PrescriptionRequest(
                "Amoxicillin", "1 tablet, 3 times a day", 7);
        when(prescriptionService.save(5L, request)).thenReturn("Prescription saved successfully");

        var response = controller.save(5L, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Prescription saved successfully", response.getBody());
        verify(prescriptionService).save(5L, request);
    }
}
