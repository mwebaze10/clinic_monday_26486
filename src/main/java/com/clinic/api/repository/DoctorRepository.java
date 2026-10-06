package com.clinic.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clinic.api.model.Doctor;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    @Query("""
            SELECT DISTINCT d
            FROM Doctor d
            JOIN FETCH d.specializations s
            WHERE LOWER(s.name) = LOWER(:name)
            ORDER BY d.name
            """)
    List<Doctor> findBySpecializationName(@Param("name") String name);

    @Query("""
            SELECT d
            FROM Doctor d
            WHERE d.office IS NULL
            ORDER BY d.lastName ASC
            """)
    List<Doctor> findDoctorsWithoutOffice();
}