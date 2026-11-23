package com.clinic.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.clinic.api.model.Doctor;
import com.clinic.api.model.Appointment;
import com.clinic.api.model.AppointmentStatus;
import com.clinic.api.model.Office;
import com.clinic.api.model.Specialization;
import com.clinic.api.repository.AppointmentRepository;
import com.clinic.api.repository.DoctorRepository;
import com.clinic.api.repository.OfficeRepository;
import com.clinic.api.repository.SpecializationRepository;

@Service
public class DoctorService {

	private final DoctorRepository doctorRepository;
	private final OfficeRepository officeRepository;
	private final SpecializationRepository specializationRepository;
	private final AppointmentRepository appointmentRepository;

	public DoctorService(DoctorRepository doctorRepository, OfficeRepository officeRepository,
			SpecializationRepository specializationRepository, AppointmentRepository appointmentRepository) {
		this.doctorRepository = doctorRepository;
		this.officeRepository = officeRepository;
		this.specializationRepository = specializationRepository;
		this.appointmentRepository = appointmentRepository;
	}

	public List<Doctor> findAll() {
		return doctorRepository.findAll();
	}

	public Optional<Doctor> findById(Long id) {
		return doctorRepository.findById(id);
	}

	public List<Doctor> findBySpecializationName(String name) {
		return doctorRepository.findBySpecializationName(name);
	}

	public List<Doctor> searchByName(String name) {
		return doctorRepository.searchByName(name);
	}

	public List<Object[]> getWorkload(LocalDate start, LocalDate end) {
		return doctorRepository.findDoctorWorkload(start, end, AppointmentStatus.COMPLETED);
	}

	public List<Doctor> findWithoutOffice() {
		return doctorRepository.findWithoutOffice();
	}

	public List<Appointment> getAgenda(Long doctorId, LocalDate date) {
		if (!doctorRepository.existsById(doctorId)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The doctor with that id does not exist");
		}
		return appointmentRepository.findByDoctor_IdAndAppointmentDateAndStatusNot(
				doctorId, date, AppointmentStatus.CANCELLED);
	}

	public DoctorSpecializationResult moveDoctorToOffice(Long doctorId, Integer officeNumber) {
		Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
		if (doctorOpt.isEmpty()) {
			return new DoctorSpecializationResult(HttpStatus.NOT_FOUND, "The doctor with that id does not exist");
		}

		Optional<Office> officeOpt = officeRepository.findByOfficeNumber(officeNumber);
		if (officeOpt.isEmpty()) {
			return new DoctorSpecializationResult(HttpStatus.NOT_FOUND,
					"The office with that office number does not exist");
		}

		Office newOffice = officeOpt.get();
		Doctor doctor = doctorOpt.get();
		if (doctorRepository.existsByOfficeAndIdNot(newOffice, doctorId)) {
			return new DoctorSpecializationResult(HttpStatus.CONFLICT, "That office is taken by other doctor");
		}

		doctor.setOffice(newOffice);
		doctorRepository.save(doctor);
		return new DoctorSpecializationResult(HttpStatus.OK, "Office changed successfully");
	}

	public Doctor save(Doctor doctor) {
		doctor.setOffice(resolveOffice(doctor.getOffice()));
		doctor.setSpecializations(resolveSpecializations(doctor.getSpecializations()));
		return doctorRepository.save(doctor);
	}

	public Optional<Doctor> update(Long id, Doctor doctorDetails) {
		return doctorRepository.findById(id).map(doctor -> {
			doctor.setFirstName(doctorDetails.getFirstName());
			doctor.setLastName(doctorDetails.getLastName());
			doctor.setOffice(resolveOffice(doctorDetails.getOffice()));
			doctor.setSpecializations(resolveSpecializations(doctorDetails.getSpecializations()));
			return doctorRepository.save(doctor);
		});
	}

	public void deleteById(Long id) {
		doctorRepository.deleteById(id);
	}

	public String addSpecializationToDoctor(Long doctorId, Long specializationId) {
		Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
		if (doctorOpt.isEmpty()) {
			return "The doctor with that id does not exist";
		}

		Optional<Specialization> specializationOpt = specializationRepository.findById(specializationId);
		if (specializationOpt.isEmpty()) {
			return "That specialization does not exist";
		}

		Doctor doctor = doctorOpt.get();
		Specialization specialization = specializationOpt.get();
		if (doctor.getSpecializations() == null) {
			doctor.setSpecializations(new ArrayList<>());
		}

		if (doctor.getSpecializations().contains(specialization)) {
			return "The doctor already has that specialization";
		}

		doctor.getSpecializations().add(specialization);
		doctorRepository.save(doctor);
		return "Specialization added to the doctor";
	}

	public String removeSpecializationFromDoctor(Long doctorId, Long specializationId) {
		Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
		if (doctorOpt.isEmpty()) {
			return "The doctor with that id does not exist";
		}

		Optional<Specialization> specializationOpt = specializationRepository.findById(specializationId);
		if (specializationOpt.isEmpty()) {
			return "That specialization does not exist";
		}

		Doctor doctor = doctorOpt.get();
		Specialization specialization = specializationOpt.get();
		if (doctor.getSpecializations() == null || !doctor.getSpecializations().contains(specialization)) {
			return "The doctor does not have that specialization";
		}

		doctor.getSpecializations().remove(specialization);
		doctorRepository.save(doctor);
		return "Specialization removed from the doctor";
	}

	private Office resolveOffice(Office office) {
		if (office == null) {
			return null;
		}
		if (office.getId() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Office id is required");
		}
		return officeRepository.findById(office.getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Office not found with id " + office.getId()));
	}

	private List<Specialization> resolveSpecializations(List<Specialization> specializations) {
		List<Specialization> resolved = new ArrayList<>();
		if (specializations == null) {
			return resolved;
		}
		for (Specialization specialization : specializations) {
			if (specialization == null || specialization.getId() == null) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Specialization id is required");
			}
			resolved.add(specializationRepository.findById(specialization.getId())
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
							"Specialization not found with id " + specialization.getId())));
		}
		return resolved;
	}

	public static class DoctorSpecializationResult {
		private final HttpStatus status;
		private final String message;

		public DoctorSpecializationResult(HttpStatus status, String message) {
			this.status = status;
			this.message = message;
		}

		public HttpStatus getStatus() {
			return status;
		}

		public String getMessage() {
			return message;
		}
	}
}