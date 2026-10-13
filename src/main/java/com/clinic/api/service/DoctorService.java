package com.clinic.api.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.clinic.api.model.Doctor;
import com.clinic.api.model.AppointmentStatus;
import com.clinic.api.model.Office;
import com.clinic.api.model.Specialization;
import com.clinic.api.repository.DoctorRepository;
import com.clinic.api.repository.OfficeRepository;
import com.clinic.api.repository.SpecializationRepository;

@Service
public class DoctorService {

	private final DoctorRepository doctorRepository;
	private final OfficeRepository officeRepository;
	private final SpecializationRepository specializationRepository;

	public DoctorService(DoctorRepository doctorRepository, OfficeRepository officeRepository,
			SpecializationRepository specializationRepository) {
		this.doctorRepository = doctorRepository;
		this.officeRepository = officeRepository;
		this.specializationRepository = specializationRepository;
	}

	public Doctor createDoctor(Doctor doctor, Long officeId) {
		Office office = officeRepository.findById(officeId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Office not found with id " + officeId));

		doctor.setOffice(office);
		doctor.setSpecializations(resolveSpecializations(doctor));
		return doctorRepository.save(doctor);
	}

	public Doctor updateDoctor(Long id, Doctor details) {
		Doctor doctor = doctorRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Doctor not found with id " + id));
		doctor.setName(details.getName());
		doctor.setLastName(details.getLastName());
		doctor.setOffice(details.getOffice());
		doctor.setSpecializations(resolveSpecializations(details));
		return doctorRepository.save(doctor);
	}

	public List<Doctor> findBySpecialization(String name) {
		return doctorRepository.findBySpecializationName(name);
	}

	public List<Doctor> findWithoutOffice() {
		return doctorRepository.findDoctorsWithoutOffice();
	}

	public List<Doctor> searchByName(String name) {
		return doctorRepository.searchByName(name);
	}

	public List<Object[]> findWorkload(LocalDate start, LocalDate end) {
		return doctorRepository.findDoctorWorkload(AppointmentStatus.COMPLETED, start, end);
	}

	public String addSpecialization(Long doctorId, Long specializationId) {
		Doctor doctor = doctorRepository.findById(doctorId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"The doctor with that id does not exist"));
		Specialization specialization = specializationRepository.findById(specializationId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"That specialization does not exist"));

		if (doctor.getSpecializations().contains(specialization)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"The doctor already has that specialization");
		}

		doctor.getSpecializations().add(specialization);
		doctorRepository.save(doctor);
		return "Specialization added to the doctor";
	}

	public String removeSpecialization(Long doctorId, Long specializationId) {
		Doctor doctor = doctorRepository.findById(doctorId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"The doctor with that id does not exist"));
		Specialization specialization = specializationRepository.findById(specializationId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"That specialization does not exist"));

		if (!doctor.getSpecializations().contains(specialization)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"The doctor does not have that specialization");
		}

		doctor.getSpecializations().remove(specialization);
		doctorRepository.save(doctor);
		return "Specialization removed from the doctor";
	}

	public String moveToOffice(Long doctorId, Integer officeNumber) {
		Doctor doctor = doctorRepository.findById(doctorId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"The doctor with that id does not exist"));
		Office office = officeRepository.findByOfficeNumber(officeNumber)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"The office with that office number does not exist"));

		boolean officeOccupied = doctorRepository.existsByOffice_Id(office.getId());
		boolean alreadyAssigned = doctor.getOffice() != null
				&& doctor.getOffice().getId().equals(office.getId());
		if (officeOccupied && !alreadyAssigned) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"That office is taken by other doctor");
		}

		doctor.setOffice(office);
		doctorRepository.save(doctor);
		return "Office changed successfully";
	}

	private Set<Specialization> resolveSpecializations(Doctor doctor) {
		Set<Specialization> specializations = new LinkedHashSet<>();
		if (doctor.getSpecializations() != null) {
			for (Specialization requested : doctor.getSpecializations()) {
				if (requested.getId() == null
						&& (requested.getName() == null || requested.getName().isBlank())) {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
							"Each specialization requires an id or a name");
				}
				Specialization specialization = requested.getId() == null
						? specializationRepository.findByNameIgnoreCase(requested.getName())
								.orElseGet(() -> specializationRepository.save(
										new Specialization(null, requested.getName())))
						: specializationRepository.findById(requested.getId())
								.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
										"Specialization not found with id " + requested.getId()));
				specializations.add(specialization);
			}
		}
		return specializations;
	}
}