package com.clinic.api.dto;

public record PrescriptionRequest(String medicineName, String dosage, int durationDays) {
}
