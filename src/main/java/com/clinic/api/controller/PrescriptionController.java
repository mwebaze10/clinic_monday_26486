package com.clinic.api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.api.model.Prescription;
import com.clinic.api.service.PrescriptionService;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @PostMapping("/save")
    public ResponseEntity<String> savePrescription(
            @RequestParam("appointmentId") Long appointmentId,
            @RequestBody Prescription prescription) {
        PrescriptionService.PrescriptionOperationResult result =
                prescriptionService.savePrescription(appointmentId, prescription);
        return ResponseEntity.status(result.getStatus()).body(result.getMessage());
    }

    @GetMapping("/appointment/{appointmentId}")
    public List<Prescription> getPrescriptionsByAppointment(@PathVariable Long appointmentId) {
        return prescriptionService.getPrescriptionsByAppointmentId(appointmentId);
    }

    @GetMapping("/patient/{ssNumber}")
    public List<Prescription> getPrescriptionsByPatient(@PathVariable String ssNumber) {
        return prescriptionService.getPrescriptionsByPatientSsNumber(ssNumber);
    }

    @GetMapping("/stats/top-medicines")
    public List<Object[]> getTopMedicines() {
        return prescriptionService.getTopMedicines();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updatePrescription(
            @PathVariable UUID id,
            @RequestBody Prescription prescriptionDetails) {
        PrescriptionService.PrescriptionOperationResult result =
                prescriptionService.updatePrescription(id, prescriptionDetails);
        return ResponseEntity.status(result.getStatus()).body(result.getMessage());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePrescription(@PathVariable UUID id) {
        PrescriptionService.PrescriptionOperationResult result =
                prescriptionService.deletePrescription(id);
        return ResponseEntity.status(result.getStatus()).body(result.getMessage());
    }
}
