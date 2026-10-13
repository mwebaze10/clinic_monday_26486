package com.clinic.api.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.api.dto.AppointmentBookingRequest;
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

    @PostMapping({ "", "/save" })
    public Appointment createAppointment(@RequestParam("doctorId") Long doctorId,
            @RequestParam("patientId") Long patientId,
            @RequestParam("appointmentDate") LocalDate appointmentDate,
            @RequestBody(required = false) AppointmentBookingRequest ignoredRequest) {
        return appointmentService.createAppointment(doctorId, patientId, appointmentDate);
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

    @GetMapping("/stats/by-status")
    public List<Object[]> getAppointmentCountsByStatus() {
        return appointmentService.countByStatus();
    }

    @GetMapping("/page")
    public Page<Appointment> getAppointmentsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "appointmentDate,desc") String sort) {
        String[] sortOptions = sort.split(",", 2);
        String property = sortOptions[0];
        Sort.Direction direction = sortOptions.length == 2
                ? Sort.Direction.fromString(sortOptions[1])
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, property));
        return appointmentService.findPage(pageable);
    }

    @PatchMapping("/cancel-day")
    public String cancelAppointmentsForDoctorOnDate(
            @RequestParam("doctorId") Long doctorId,
            @RequestParam("date") LocalDate date) {
        return appointmentService.cancelDay(doctorId, date);
    }

    @DeleteMapping("/cancelled-before")
    public String deleteCancelledBefore(@RequestParam("date") LocalDate date) {
        return appointmentService.deleteCancelledBefore(date);
    }
}