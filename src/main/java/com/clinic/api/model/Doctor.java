package com.clinic.api.model;

import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "doctors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String lastName;

    @ManyToOne
    @JoinColumn(name = "office_id")
    private Office office;

    @ManyToMany
    @JoinTable(name = "doctor_specializations",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "specialization_id"))
    private Set<Specialization> specializations = new LinkedHashSet<>();
}