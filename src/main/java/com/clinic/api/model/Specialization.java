package com.clinic.api.model;

import java.util.LinkedHashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "specializations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Specialization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "specializations")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Doctor> doctors = new LinkedHashSet<>();

    public Specialization(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
