package com.clinic.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.clinic.api.model.AppointmentStatus;
import com.clinic.api.model.Office;
import com.clinic.api.repository.AppointmentRepository;
import com.clinic.api.repository.DoctorRepository;
import com.clinic.api.repository.OfficeRepository;
import com.clinic.api.repository.PatientRepository;
import com.clinic.api.repository.PrescriptionRepository;

class DashboardServiceTests {
    @Test
    void returnsSummaryCountsInRequiredOrder() {
        PatientRepository patientRepository = mock(PatientRepository.class);
        DoctorRepository doctorRepository = mock(DoctorRepository.class);
        OfficeRepository officeRepository = mock(OfficeRepository.class);
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        PrescriptionRepository prescriptionRepository = mock(PrescriptionRepository.class);

        when(patientRepository.count()).thenReturn(5L);
        when(doctorRepository.count()).thenReturn(3L);
        when(officeRepository.findFreeOffices()).thenReturn(List.of(new Office(), new Office()));
        when(appointmentRepository.countByAppointmentDate(LocalDate.now())).thenReturn(0L);
        when(appointmentRepository.countByStatus(AppointmentStatus.SCHEDULED)).thenReturn(4L);
        when(prescriptionRepository.count()).thenReturn(6L);

        DashboardService dashboardService = new DashboardService(patientRepository, doctorRepository,
                officeRepository, appointmentRepository, prescriptionRepository);

        Map<String, Long> summary = dashboardService.getSummary();

        assertInstanceOf(java.util.LinkedHashMap.class, summary);
        assertEquals(List.of("patients", "doctors", "freeOffices", "appointmentsToday", "scheduled",
                "prescriptions"), List.copyOf(summary.keySet()));
        assertEquals(Map.of("patients", 5L, "doctors", 3L, "freeOffices", 2L,
                "appointmentsToday", 0L, "scheduled", 4L, "prescriptions", 6L), summary);
    }
}
