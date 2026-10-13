package com.clinic.api.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.clinic.api.dto.PrescriptionRequest;
import com.clinic.api.model.Appointment;
import com.clinic.api.model.AppointmentStatus;
import com.clinic.api.model.Prescription;
import com.clinic.api.repository.AppointmentRepository;
import com.clinic.api.repository.PrescriptionRepository;

@Service
public class PrescriptionService {
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionService(AppointmentRepository appointmentRepository,
            PrescriptionRepository prescriptionRepository) {
        this.appointmentRepository = appointmentRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    public String save(Long appointmentId, PrescriptionRequest request) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "The appointment with that id does not exist"));

        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Prescriptions are only for COMPLETED appointments");
        }

        Prescription prescription = new Prescription();
        prescription.setMedicineName(request.medicineName());
        prescription.setDosage(request.dosage());
        prescription.setDurationDays(request.durationDays());
        prescription.setIssuedDate(LocalDate.now());
        prescription.setAppointment(appointment);
        prescriptionRepository.save(prescription);

        return "Prescription saved successfully";
    }

    public List<Prescription> findByAppointmentId(Long appointmentId) {
        if (!appointmentRepository.existsById(appointmentId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "The appointment with that id does not exist");
        }
        return prescriptionRepository.findByAppointment_Id(appointmentId);
    }

    public List<Prescription> findByPatientSsNumber(String ssNumber) {
        return prescriptionRepository.findByPatientSsNumberOrderByIssuedDateDesc(ssNumber);
    }

    public List<Object[]> findTopMedicines() {
        return prescriptionRepository.findTopMedicines();
    }

    public String update(UUID id, PrescriptionRequest request) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "The prescription with that id does not exist"));

        prescription.setMedicineName(request.medicineName());
        prescription.setDosage(request.dosage());
        prescription.setDurationDays(request.durationDays());
        prescriptionRepository.save(prescription);
        return "Prescription updated successfully";
    }

    public String delete(UUID id) {
        if (!prescriptionRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "The prescription with that id does not exist");
        }
        prescriptionRepository.deleteById(id);
        return "Prescription deleted successfully";
    }

    public boolean appointmentHasPrescriptions(Long appointmentId) {
        return prescriptionRepository.existsByAppointment_Id(appointmentId);
    }
}
