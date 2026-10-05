package com.clinic.api.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.clinic.api.service.AppointmentService;

class AppointmentControllerTests {
    @Test
    void parsesDateRangeParametersBeforeCallingService() {
        AppointmentService appointmentService = mock(AppointmentService.class);
        AppointmentController controller = new AppointmentController(appointmentService);

        controller.getAppointmentsBetween("2026-10-01", "2026-10-31");

        verify(appointmentService).findBetween(
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31));
    }
}
