package com.clinic.api.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
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
        private String firstName;
        private String lastName;

        @ManyToOne
        @JoinColumn(name = "office_id")
        @ToString.Exclude
        @EqualsAndHashCode.Exclude
        private Office office;

        @ManyToMany
        @JoinTable(name = "doctor_specializations",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "specialization_id"))
        @ToString.Exclude
        @EqualsAndHashCode.Exclude
        private List<Specialization> specializations = new ArrayList<>();
}