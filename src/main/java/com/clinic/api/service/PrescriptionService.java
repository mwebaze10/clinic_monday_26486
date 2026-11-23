package com.clinic.api.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.clinic.api.model.Appointment;
import com.clinic.api.model.AppointmentStatus;
import com.clinic.api.model.Prescription;
import com.clinic.api.repository.AppointmentRepository;
import com.clinic.api.repository.PrescriptionRepository;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository,
            AppointmentRepository appointmentRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public PrescriptionOperationResult savePrescription(Long appointmentId, Prescription prescription) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isEmpty()) {
            return new PrescriptionOperationResult(HttpStatus.NOT_FOUND,
                    "The appointment with that id does not exist");
        }

        Appointment appointment = appointmentOpt.get();
        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            return new PrescriptionOperationResult(HttpStatus.BAD_REQUEST,
                    "Prescriptions are only for COMPLETED appointments");
        }

        prescription.setAppointment(appointment);
        prescription.setIssuedDate(LocalDate.now());
        prescriptionRepository.save(prescription);
        return new PrescriptionOperationResult(HttpStatus.CREATED, "Prescription saved successfully");
    }

    public List<Prescription> getPrescriptionsByAppointmentId(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "The appointment with that id does not exist"));
        return prescriptionRepository.findByAppointment(appointment);
    }

    public List<Prescription> getPrescriptionsByPatientSsNumber(String ssNumber) {
        return prescriptionRepository.findByPatientSsNumberOrderByIssuedDateDesc(ssNumber);
    }

    public List<Object[]> getTopMedicines() {
        return prescriptionRepository.findTopMedicines();
    }

    public PrescriptionOperationResult updatePrescription(UUID id, Prescription details) {
        Optional<Prescription> prescriptionOpt = prescriptionRepository.findById(id);
        if (prescriptionOpt.isEmpty()) {
            return new PrescriptionOperationResult(HttpStatus.NOT_FOUND,
                    "The prescription with that id does not exist");
        }

        Prescription prescription = prescriptionOpt.get();
        prescription.setMedicineName(details.getMedicineName());
        prescription.setDosage(details.getDosage());
        prescription.setDurationDays(details.getDurationDays());
        prescriptionRepository.save(prescription);
        return new PrescriptionOperationResult(HttpStatus.OK, "Prescription updated successfully");
    }

    public PrescriptionOperationResult deletePrescription(UUID id) {
        if (prescriptionRepository.findById(id).isEmpty()) {
            return new PrescriptionOperationResult(HttpStatus.NOT_FOUND,
                    "The prescription with that id does not exist");
        }

        prescriptionRepository.deleteById(id);
        return new PrescriptionOperationResult(HttpStatus.OK, "Prescription deleted successfully");
    }

    public static class PrescriptionOperationResult {
        private final HttpStatus status;
        private final String message;

        public PrescriptionOperationResult(HttpStatus status, String message) {
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
