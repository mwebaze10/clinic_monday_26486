package com.clinic.api.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.clinic.api.model.Appointment;
import com.clinic.api.model.AppointmentStatus;
import com.clinic.api.model.Doctor;
import com.clinic.api.model.Patient;
import com.clinic.api.repository.AppointmentRepository;
import com.clinic.api.repository.DoctorRepository;
import com.clinic.api.repository.PatientRepository;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
            DoctorRepository doctorRepository, PatientRepository patientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    public Appointment createAppointment(Long doctorId, Long patientId, LocalDate appointmentDate) {
        if (appointmentDate.isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Appointment date cannot be in the past");
        }

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Doctor not found with id " + doctorId));
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Patient not found with id " + patientId));

        if (appointmentRepository.existsByDoctor_IdAndAppointmentDateAndStatusNot(
                doctorId, appointmentDate, AppointmentStatus.CANCELLED)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Doctor is already booked on that date");
        }

        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setAppointmentDate(appointmentDate);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        return appointmentRepository.save(appointment);
    }

    public String updateStatus(Long id, AppointmentStatus newStatus) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "The appointment with that id does not exist"));

        AppointmentStatus currentStatus = appointment.getStatus();
        boolean validTransition = (currentStatus == AppointmentStatus.SCHEDULED
                && (newStatus == AppointmentStatus.CONFIRMED || newStatus == AppointmentStatus.CANCELLED))
                || (currentStatus == AppointmentStatus.CONFIRMED
                        && (newStatus == AppointmentStatus.COMPLETED || newStatus == AppointmentStatus.CANCELLED));

        if (!validTransition) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot change status from " + currentStatus + " to " + newStatus);
        }

        appointment.setStatus(newStatus);
        appointmentRepository.save(appointment);
        return "Appointment status updated successfully";
    }

    public String reschedule(Long id, LocalDate newDate) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "The appointment with that id does not exist"));

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A COMPLETED appointment cannot be rescheduled");
        }
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A CANCELLED appointment cannot be rescheduled");
        }
        if (newDate.isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Appointment date cannot be in the past");
        }

        if (appointmentRepository.existsByDoctor_IdAndAppointmentDateAndStatusNot(
                appointment.getDoctor().getId(), newDate, AppointmentStatus.CANCELLED)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Doctor is already booked on that date");
        }

        appointment.setAppointmentDate(newDate);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointmentRepository.save(appointment);
        return "Appointment rescheduled successfully";
    }

    public List<Appointment> findByStatus(AppointmentStatus status) {
        return appointmentRepository.findByStatusOrderByAppointmentDateAsc(status);
    }

    public List<Appointment> findBetween(LocalDate start, LocalDate end) {
        return appointmentRepository.findByAppointmentDateBetweenOrderByAppointmentDateAsc(start, end);
    }

    public List<Object[]> countByStatus() {
        return appointmentRepository.countAppointmentsByStatus();
    }

    public Page<Appointment> findPage(Pageable pageable) {
        return appointmentRepository.findAll(pageable);
    }

    @Transactional
    public String cancelDay(Long doctorId, LocalDate date) {
        int cancelledAppointments = appointmentRepository.cancelDay(
                doctorId, date, AppointmentStatus.CANCELLED, AppointmentStatus.COMPLETED);
        return cancelledAppointments + " appointments cancelled";
    }

    @Transactional
    public String deleteCancelledBefore(LocalDate date) {
        int deletedAppointments = appointmentRepository.deleteCancelledBefore(
                date, AppointmentStatus.CANCELLED);
        return deletedAppointments + " appointments deleted";
    }

    public void deleteById(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "The appointment with that id does not exist");
        }
        appointmentRepository.deleteById(id);
    }
}