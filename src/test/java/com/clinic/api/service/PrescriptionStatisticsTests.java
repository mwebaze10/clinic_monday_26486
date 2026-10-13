package com.clinic.api.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.clinic.api.repository.AppointmentRepository;
import com.clinic.api.repository.PrescriptionRepository;

class PrescriptionStatisticsTests {
    @Test
    void returnsTopMedicinesFromRepository() {
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        PrescriptionRepository prescriptionRepository = mock(PrescriptionRepository.class);
        List<Object[]> topMedicines = List.<Object[]>of(
                new Object[] { "Amoxicillin", 3L },
                new Object[] { "Paracetamol", 2L });
        when(prescriptionRepository.findTopMedicines()).thenReturn(topMedicines);

        PrescriptionService prescriptionService = new PrescriptionService(
                appointmentRepository, prescriptionRepository);

        assertSame(topMedicines, prescriptionService.findTopMedicines());
        verify(prescriptionRepository).findTopMedicines();
    }
}
