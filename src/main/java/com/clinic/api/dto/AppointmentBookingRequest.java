package com.clinic.api.dto;

import com.clinic.api.model.AppointmentStatus;

public record AppointmentBookingRequest(AppointmentStatus status) {
}
