package com.clinic.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.api.dto.PrescriptionRequest;
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
}
