package com.clinic.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.clinic.api.dto.AppointmentBookingRequest;
import com.clinic.api.model.Appointment;
import com.clinic.api.model.AppointmentStatus;
import com.clinic.api.service.AppointmentService;

class AppointmentControllerTests {
    @Test
    void bookingIgnoresStatusInJsonRequest() {
        AppointmentService appointmentService = mock(AppointmentService.class);
        AppointmentController controller = new AppointmentController(appointmentService);
        LocalDate date = LocalDate.now().plusDays(1);
        when(appointmentService.createAppointment(1L, 2L, date)).thenReturn(
                new Appointment(null, null, null, date, AppointmentStatus.SCHEDULED));

        Appointment appointment = controller.createAppointment(1L, 2L, date,
                new AppointmentBookingRequest(AppointmentStatus.CANCELLED));

        assertEquals(AppointmentStatus.SCHEDULED, appointment.getStatus());
        verify(appointmentService).createAppointment(1L, 2L, date);
    }

    @Test
    void parsesDateRangeParametersBeforeCallingService() {
        AppointmentService appointmentService = mock(AppointmentService.class);
        AppointmentController controller = new AppointmentController(appointmentService);

        controller.getAppointmentsBetween("2026-10-01", "2026-10-31");

        verify(appointmentService).findBetween(
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31));
    }

    @Test
    void buildsPageableFromPageSizeAndSortParameters() {
        AppointmentService appointmentService = mock(AppointmentService.class);
        AppointmentController controller = new AppointmentController(appointmentService);

        controller.getAppointmentsPage(2, 5, "appointmentDate,desc");

        verify(appointmentService).findPage(
                PageRequest.of(2, 5, Sort.by(Sort.Direction.DESC, "appointmentDate")));
    }

}
