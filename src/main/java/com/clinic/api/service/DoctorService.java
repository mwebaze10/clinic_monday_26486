package com.clinic.api.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.clinic.api.model.Doctor;
import com.clinic.api.model.Office;
import com.clinic.api.repository.DoctorRepository;
import com.clinic.api.repository.OfficeRepository;

@Service
public class DoctorService {

	private final DoctorRepository doctorRepository;
	private final OfficeRepository officeRepository;

	public DoctorService(DoctorRepository doctorRepository, OfficeRepository officeRepository) {
		this.doctorRepository = doctorRepository;
		this.officeRepository = officeRepository;
	}

	public Doctor createDoctor(Doctor doctor, Long officeId) {
		Office office = officeRepository.findById(officeId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Office not found with id " + officeId));

		doctor.setOffice(office);
		return doctorRepository.save(doctor);
	}
}