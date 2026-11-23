package com.clinic.api.model;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "prescription")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String medicineName;
    private String dosage;
    private int durationDays;
    private LocalDate issuedDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "appointment_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Appointment appointment;
}
