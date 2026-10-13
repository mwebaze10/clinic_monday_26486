package com.clinic.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
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
    private static final LocalDate APPOINTMENT_DATE = LocalDate.now().plusDays(7);

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

    @Test
    void rejectsBookingWhenDateIsInThePast() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> appointmentService.createAppointment(DOCTOR_ID, PATIENT_ID, yesterday));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Appointment date cannot be in the past", exception.getReason());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void allowsOnlyStatusTransitionsFromTheStatusFlow() {
        Appointment appointment = new Appointment();
        appointment.setId(3L);
        when(appointmentRepository.findById(3L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AppointmentStatus[][] allowedTransitions = {
                { AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED },
                { AppointmentStatus.SCHEDULED, AppointmentStatus.CANCELLED },
                { AppointmentStatus.CONFIRMED, AppointmentStatus.COMPLETED },
                { AppointmentStatus.CONFIRMED, AppointmentStatus.CANCELLED }
        };

        for (AppointmentStatus[] transition : allowedTransitions) {
            appointment.setStatus(transition[0]);
            assertEquals("Appointment status updated successfully",
                    appointmentService.updateStatus(3L, transition[1]));
            assertEquals(transition[1], appointment.getStatus());
        }

        verify(appointmentRepository, times(allowedTransitions.length)).save(appointment);
    }

    @Test
    void rejectsEveryStatusTransitionNotInTheStatusFlowWithRealStatusNames() {
        Appointment appointment = new Appointment();
        appointment.setId(3L);
        when(appointmentRepository.findById(3L)).thenReturn(Optional.of(appointment));

        for (AppointmentStatus current : AppointmentStatus.values()) {
            for (AppointmentStatus next : AppointmentStatus.values()) {
                boolean allowed = (current == AppointmentStatus.SCHEDULED
                        && (next == AppointmentStatus.CONFIRMED || next == AppointmentStatus.CANCELLED))
                        || (current == AppointmentStatus.CONFIRMED
                                && (next == AppointmentStatus.COMPLETED || next == AppointmentStatus.CANCELLED));
                if (allowed) {
                    continue;
                }

                appointment.setStatus(current);
                ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                        () -> appointmentService.updateStatus(3L, next));

                assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
                assertEquals("Cannot change status from " + current + " to " + next,
                        exception.getReason());
            }
        }

        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void returnsNotFoundWhenChangingStatusForMissingAppointment() {
        when(appointmentRepository.findById(3L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> appointmentService.updateStatus(3L, AppointmentStatus.COMPLETED));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("The appointment with that id does not exist", exception.getReason());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void returnsDoctorAgendaExcludingCancelledAppointments() {
        LocalDate date = LocalDate.now().plusDays(1);
        List<Appointment> appointments = List.of(new Appointment());
        when(doctorRepository.existsById(DOCTOR_ID)).thenReturn(true);
        when(appointmentRepository.findByDoctor_IdAndAppointmentDateAndStatusNot(
                DOCTOR_ID, date, AppointmentStatus.CANCELLED)).thenReturn(appointments);

        assertEquals(appointments, appointmentService.getDoctorAgenda(DOCTOR_ID, date));
        verify(appointmentRepository).findByDoctor_IdAndAppointmentDateAndStatusNot(
                DOCTOR_ID, date, AppointmentStatus.CANCELLED);
    }

    @Test
    void returnsNotFoundForAgendaOfMissingDoctor() {
        when(doctorRepository.existsById(DOCTOR_ID)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> appointmentService.getDoctorAgenda(DOCTOR_ID, LocalDate.now()));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("The doctor with that id does not exist", exception.getReason());
        verify(appointmentRepository, never())
                .findByDoctor_IdAndAppointmentDateAndStatusNot(any(), any(), any());
    }

    @Test
    void returnsUpcomingPatientAppointmentsWithRequiredStatusesAndDate() {
        String ssNumber = "1234567890123456";
        List<Appointment> appointments = List.of(new Appointment());
        when(patientRepository.existsBySsNumber(ssNumber)).thenReturn(true);
        when(appointmentRepository
                .findByPatient_SsNumberAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAsc(
                        ssNumber, LocalDate.now(),
                        List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED)))
                .thenReturn(appointments);

        assertEquals(appointments, appointmentService.getUpcomingForPatient(ssNumber));
        verify(appointmentRepository)
                .findByPatient_SsNumberAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAsc(
                        ssNumber, LocalDate.now(),
                        List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED));
    }

    @Test
    void returnsNotFoundForUpcomingAppointmentsOfMissingPatient() {
        String ssNumber = "missing";
        when(patientRepository.existsBySsNumber(ssNumber)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> appointmentService.getUpcomingForPatient(ssNumber));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("That patient with that ssnumber does not exist", exception.getReason());
        verify(appointmentRepository, never())
                .findByPatient_SsNumberAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAsc(
                        any(), any(), any());
    }

    @Test
    void reschedulesAppointmentAndResetsStatusToScheduled() {
        LocalDate newDate = LocalDate.now().plusDays(10);
        Doctor doctor = new Doctor();
        doctor.setId(DOCTOR_ID);
        Appointment appointment = new Appointment(
                3L, doctor, new Patient(), APPOINTMENT_DATE, AppointmentStatus.CONFIRMED);
        when(appointmentRepository.findById(3L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.existsByDoctor_IdAndAppointmentDateAndStatusNot(
                DOCTOR_ID, newDate, AppointmentStatus.CANCELLED)).thenReturn(false);
        when(appointmentRepository.save(any(Appointment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        String result = appointmentService.reschedule(3L, newDate);

        assertEquals("Appointment rescheduled successfully", result);
        assertEquals(newDate, appointment.getAppointmentDate());
        assertEquals(AppointmentStatus.SCHEDULED, appointment.getStatus());
        verify(appointmentRepository).save(appointment);
    }

    @Test
    void rejectsReschedulingWhenAppointmentDoesNotExist() {
        when(appointmentRepository.findById(3L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> appointmentService.reschedule(3L, LocalDate.now().plusDays(1)));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("The appointment with that id does not exist", exception.getReason());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void rejectsReschedulingCompletedAndCancelledAppointments() {
        Appointment appointment = new Appointment();
        appointment.setId(3L);
        when(appointmentRepository.findById(3L)).thenReturn(Optional.of(appointment));

        for (AppointmentStatus status : new AppointmentStatus[] {
                AppointmentStatus.COMPLETED, AppointmentStatus.CANCELLED }) {
            appointment.setStatus(status);
            ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                    () -> appointmentService.reschedule(3L, LocalDate.now().plusDays(1)));

            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
            assertEquals("A " + status + " appointment cannot be rescheduled",
                    exception.getReason());
        }

        verify(appointmentRepository, never()).existsByDoctor_IdAndAppointmentDateAndStatusNot(
                any(), any(), any());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void rejectsReschedulingToPastDate() {
        Appointment appointment = new Appointment();
        appointment.setId(3L);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        when(appointmentRepository.findById(3L)).thenReturn(Optional.of(appointment));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> appointmentService.reschedule(3L, LocalDate.now().minusDays(1)));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Appointment date cannot be in the past", exception.getReason());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void rejectsReschedulingWhenDoctorIsBookedOnNewDate() {
        LocalDate newDate = LocalDate.now().plusDays(10);
        Doctor doctor = new Doctor();
        doctor.setId(DOCTOR_ID);
        Appointment appointment = new Appointment(
                3L, doctor, new Patient(), APPOINTMENT_DATE, AppointmentStatus.SCHEDULED);
        when(appointmentRepository.findById(3L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.existsByDoctor_IdAndAppointmentDateAndStatusNot(
                DOCTOR_ID, newDate, AppointmentStatus.CANCELLED)).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> appointmentService.reschedule(3L, newDate));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Doctor is already booked on that date", exception.getReason());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }
}
