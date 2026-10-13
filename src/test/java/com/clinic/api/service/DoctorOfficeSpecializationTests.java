package com.clinic.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.clinic.api.model.Doctor;
import com.clinic.api.model.Office;
import com.clinic.api.model.Specialization;
import com.clinic.api.repository.DoctorRepository;
import com.clinic.api.repository.OfficeRepository;
import com.clinic.api.repository.SpecializationRepository;

class DoctorOfficeSpecializationTests {
    private final DoctorRepository doctorRepository = mock(DoctorRepository.class);
    private final OfficeRepository officeRepository = mock(OfficeRepository.class);
    private final SpecializationRepository specializationRepository = mock(SpecializationRepository.class);
    private final DoctorService doctorService = new DoctorService(
            doctorRepository, officeRepository, specializationRepository);

    @Test
    void addsSpecializationOnOwningDoctorAndSavesIt() {
        Doctor doctor = new Doctor();
        Specialization specialization = new Specialization(2L, "Cardiology");
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(specializationRepository.findById(2L)).thenReturn(Optional.of(specialization));

        assertEquals("Specialization added to the doctor",
                doctorService.addSpecialization(1L, 2L));

        assertTrue(doctor.getSpecializations().contains(specialization));
        verify(doctorRepository).save(doctor);
    }

    @Test
    void removesSpecializationFromOwningDoctorAndSavesIt() {
        Doctor doctor = new Doctor();
        Specialization specialization = new Specialization(2L, "Cardiology");
        doctor.getSpecializations().add(specialization);
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(specializationRepository.findById(2L)).thenReturn(Optional.of(specialization));

        assertEquals("Specialization removed from the doctor",
                doctorService.removeSpecialization(1L, 2L));

        assertFalse(doctor.getSpecializations().contains(specialization));
        verify(doctorRepository).save(doctor);
    }

    @Test
    void assignsDoctorToRequestedOfficeAndSavesDoctor() {
        Doctor doctor = new Doctor();
        Office office = new Office();
        office.setId(9L);
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(officeRepository.findByOfficeNumber(104)).thenReturn(Optional.of(office));
        when(doctorRepository.existsByOffice_Id(9L)).thenReturn(false);

        assertEquals("Office changed successfully", doctorService.moveToOffice(1L, 104));

        assertEquals(office, doctor.getOffice());
        verify(doctorRepository).save(doctor);
    }
}
