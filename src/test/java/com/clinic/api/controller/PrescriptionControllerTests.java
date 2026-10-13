package com.clinic.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.clinic.api.dto.PrescriptionRequest;
import com.clinic.api.service.PrescriptionService;

class PrescriptionControllerTests {
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
