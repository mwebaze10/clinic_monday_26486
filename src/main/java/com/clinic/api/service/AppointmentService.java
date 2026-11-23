package com.clinic.api.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
import com.clinic.api.repository.PrescriptionRepository;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PrescriptionRepository prescriptionRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
            DoctorRepository doctorRepository, PatientRepository patientRepository,
            PrescriptionRepository prescriptionRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    public Optional<Appointment> findById(Long id) {
        return appointmentRepository.findById(id);
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

    public Page<Appointment> findAll(Pageable pageable) {
        return appointmentRepository.findAll(pageable);
    }

    @Transactional
    public int cancelDay(Long doctorId, LocalDate date) {
        return appointmentRepository.cancelAppointmentsForDoctorOnDate(doctorId, date,
                AppointmentStatus.COMPLETED, AppointmentStatus.CANCELLED);
    }

    @Transactional
    public int deleteCancelledBefore(LocalDate date) {
        return appointmentRepository.deleteAppointmentsBeforeDate(AppointmentStatus.CANCELLED, date);
    }

    public Appointment save(Appointment appointment) {
        resolveReferences(appointment);
        if (appointment.getAppointmentDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Appointment date is required");
        }
        if (appointment.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Appointment date cannot be in the past");
        }
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        if (appointmentRepository.existsByDoctorAndAppointmentDateAndStatusNot(
                appointment.getDoctor(), appointment.getAppointmentDate(), AppointmentStatus.CANCELLED)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Doctor is already booked on that date");
        }
        return appointmentRepository.save(appointment);
    }

    public Optional<Appointment> update(Long id, Appointment appointmentDetails) {
        return appointmentRepository.findById(id).map(appointment -> {
            resolveReferences(appointmentDetails);
            if (appointmentDetails.getAppointmentDate() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Appointment date is required");
            }
            appointment.setDoctor(appointmentDetails.getDoctor());
            appointment.setPatient(appointmentDetails.getPatient());
            appointment.setAppointmentDate(appointmentDetails.getAppointmentDate());
            return appointmentRepository.save(appointment);
        });
    }

    public AppointmentStatusUpdateResult updateAppointmentStatus(Long id, AppointmentStatus newStatus) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);
        if (appointmentOpt.isEmpty()) {
            return new AppointmentStatusUpdateResult(HttpStatus.NOT_FOUND,
                    "The appointment with that id does not exist");
        }

        Appointment appointment = appointmentOpt.get();
        AppointmentStatus currentStatus = appointment.getStatus();
        boolean validTransition = (currentStatus == AppointmentStatus.SCHEDULED
                && (newStatus == AppointmentStatus.CONFIRMED || newStatus == AppointmentStatus.CANCELLED))
                || (currentStatus == AppointmentStatus.CONFIRMED
                        && (newStatus == AppointmentStatus.COMPLETED || newStatus == AppointmentStatus.CANCELLED));
        if (!validTransition) {
            return new AppointmentStatusUpdateResult(HttpStatus.BAD_REQUEST,
                    "Cannot change status from " + currentStatus + " to " + newStatus);
        }

        appointment.setStatus(newStatus);
        appointmentRepository.save(appointment);
        return new AppointmentStatusUpdateResult(HttpStatus.OK, "Appointment status updated successfully");
    }

    public AppointmentStatusUpdateResult rescheduleAppointment(Long id, LocalDate newDate) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);
        if (appointmentOpt.isEmpty()) {
            return new AppointmentStatusUpdateResult(HttpStatus.NOT_FOUND,
                    "The appointment with that id does not exist");
        }

        Appointment appointment = appointmentOpt.get();
        if (appointment.getStatus() == AppointmentStatus.COMPLETED
                || appointment.getStatus() == AppointmentStatus.CANCELLED) {
            return new AppointmentStatusUpdateResult(HttpStatus.BAD_REQUEST,
                    "A " + appointment.getStatus() + " appointment cannot be rescheduled");
        }
        if (newDate == null || newDate.isBefore(LocalDate.now())) {
            return new AppointmentStatusUpdateResult(HttpStatus.BAD_REQUEST,
                    "Appointment date cannot be in the past");
        }
        if (appointmentRepository.existsByDoctorAndAppointmentDateAndStatusNotAndIdNot(
                appointment.getDoctor(), newDate, AppointmentStatus.CANCELLED, id)) {
            return new AppointmentStatusUpdateResult(HttpStatus.CONFLICT,
                    "Doctor is already booked on that date");
        }

        appointment.setAppointmentDate(newDate);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointmentRepository.save(appointment);
        return new AppointmentStatusUpdateResult(HttpStatus.OK, "Appointment rescheduled successfully");
    }

    public void deleteById(Long id) {
        if (prescriptionRepository.existsByAppointment_Id(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "That appointment has prescriptions, delete them first");
        }
        appointmentRepository.deleteById(id);
    }

    private void resolveReferences(Appointment appointment) {
        Doctor doctor = appointment.getDoctor();
        if (doctor == null || doctor.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Doctor id is required");
        }
        appointment.setDoctor(doctorRepository.findById(doctor.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Doctor not found with id " + doctor.getId())));

        Patient patient = appointment.getPatient();
        if (patient == null || patient.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient id is required");
        }
        appointment.setPatient(patientRepository.findById(patient.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Patient not found with id " + patient.getId())));
    }

    public static class AppointmentStatusUpdateResult {
        private final HttpStatus status;
        private final String message;

        public AppointmentStatusUpdateResult(HttpStatus status, String message) {
            this.status = status;
            this.message = message;
        }

        public HttpStatus getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
    }
}