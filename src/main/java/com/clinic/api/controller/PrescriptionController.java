package com.clinic.api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.api.dto.PrescriptionRequest;
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
    public ResponseEntity<String> save(@RequestParam("appointmentId") Long appointmentId,
            @RequestBody PrescriptionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(prescriptionService.save(appointmentId, request));
    }

    @GetMapping("/appointment/{appointmentId}")
    public List<Prescription> findByAppointment(@PathVariable Long appointmentId) {
        return prescriptionService.findByAppointmentId(appointmentId);
    }

    @GetMapping("/patient/{ssNumber}")
    public List<Prescription> findByPatient(@PathVariable String ssNumber) {
        return prescriptionService.findByPatientSsNumber(ssNumber);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable UUID id,
            @RequestBody PrescriptionRequest request) {
        return ResponseEntity.ok(prescriptionService.update(id, request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        return ResponseEntity.ok(prescriptionService.delete(id));
    }
}
