package com.clinic.api.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.api.model.Appointment;
import com.clinic.api.model.AppointmentStatus;
import com.clinic.api.service.AppointmentService;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public Appointment createAppointment(@RequestParam("doctorId") Long doctorId,
            @RequestParam("patientId") Long patientId) {
        return appointmentService.createAppointment(doctorId, patientId);
    }

    @GetMapping("/by-status")
    public List<Appointment> getAppointmentsByStatus(
            @RequestParam("status") AppointmentStatus status) {
        return appointmentService.findByStatus(status);
    }

    @GetMapping("/between")
    public List<Appointment> getAppointmentsBetween(
            @RequestParam("start") String start,
            @RequestParam("end") String end) {
        return appointmentService.findBetween(LocalDate.parse(start), LocalDate.parse(end));
    }
}