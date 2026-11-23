package com.clinic.api.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @GetMapping
    public List<Appointment> getAllAppointments() {
        return appointmentService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Appointment> getAppointmentById(@PathVariable Long id) {
        return appointmentService.findById(id);
    }

    @GetMapping("/by-status")
    public List<Appointment> getAppointmentsByStatus(@RequestParam("status") AppointmentStatus status) {
        return appointmentService.findByStatus(status);
    }

    @GetMapping("/between")
    public List<Appointment> getAppointmentsBetween(@RequestParam String start, @RequestParam String end) {
        return appointmentService.findBetween(LocalDate.parse(start), LocalDate.parse(end));
    }

    @GetMapping("/stats/by-status")
    public List<Object[]> getAppointmentCountsByStatus() {
        return appointmentService.countByStatus();
    }

    @GetMapping("/page")
    public Page<Appointment> getAppointmentsPage(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "appointmentDate,desc") String sort) {
        String[] sortParts = sort.split(",");
        Sort.Direction direction = sortParts.length > 1
                ? Sort.Direction.fromString(sortParts[1])
                : Sort.Direction.ASC;
        return appointmentService.findAll(PageRequest.of(page, size, Sort.by(direction, sortParts[0])));
    }

    @PatchMapping("/cancel-day")
    public String cancelAppointmentsForDoctorOnDate(@RequestParam Long doctorId, @RequestParam String date) {
        int cancelled = appointmentService.cancelDay(doctorId, LocalDate.parse(date));
        return cancelled + " appointments cancelled";
    }

    @DeleteMapping("/cancelled-before")
    public String deleteCancelledBefore(@RequestParam String date) {
        int deleted = appointmentService.deleteCancelledBefore(LocalDate.parse(date));
        return deleted + " appointments deleted";
    }

    @PostMapping({"", "/save"})
    public Appointment saveAppointment(@RequestBody Appointment appointment) {
        return appointmentService.save(appointment);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<String> updateAppointmentStatus(
            @PathVariable Long id,
            @RequestParam("status") AppointmentStatus status) {
        AppointmentService.AppointmentStatusUpdateResult result =
                appointmentService.updateAppointmentStatus(id, status);
        return ResponseEntity.status(result.getStatus()).body(result.getMessage());
    }

    @PatchMapping("/{id}/reschedule")
    public ResponseEntity<String> rescheduleAppointment(
            @PathVariable Long id,
            @RequestParam("newDate") String newDate) {
        AppointmentService.AppointmentStatusUpdateResult result =
                appointmentService.rescheduleAppointment(id, LocalDate.parse(newDate));
        return ResponseEntity.status(result.getStatus()).body(result.getMessage());
    }

    @PutMapping("/{id}")
    public Appointment updateAppointment(@PathVariable Long id, @RequestBody Appointment appointmentDetails) {
        return appointmentService.update(id, appointmentDetails).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteById(id);
    }
}