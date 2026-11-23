package com.clinic.api.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.clinic.api.model.Appointment;
import com.clinic.api.model.AppointmentStatus;
import com.clinic.api.model.Patient;
import com.clinic.api.repository.AppointmentRepository;
import com.clinic.api.repository.DoctorRepository;
import com.clinic.api.repository.PatientRepository;

@Service
public class PatientService {

	private final PatientRepository patientRepository;
	private final DoctorRepository doctorRepository;
	private final AppointmentRepository appointmentRepository;

	public PatientService(PatientRepository patientRepository, DoctorRepository doctorRepository,
			AppointmentRepository appointmentRepository) {
		this.patientRepository = patientRepository;
		this.doctorRepository = doctorRepository;
		this.appointmentRepository = appointmentRepository;
	}

	public List<Patient> findAll() {
		return patientRepository.findAll();
	}

	public Optional<Patient> findById(Long id) {
		return patientRepository.findById(id);
	}

	public List<Patient> findByLastNameIgnoreCase(String lastName) {
		return patientRepository.findByLastNameIgnoreCaseOrderByFirstNameAsc(lastName);
	}

	public List<Patient> findByDoctorId(Long doctorId) {
		if (!doctorRepository.existsById(doctorId)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The doctor with that id does not exist");
		}
		return patientRepository.findDistinctPatientsByDoctorId(doctorId);
	}

	public List<Patient> findFrequent(long min) {
		return patientRepository.findFrequentPatients(min);
	}

	public List<Appointment> getUpcomingAppointments(String ssNumber) {
		if (!patientRepository.existsBySsNumber(ssNumber)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					"That patient with that ssnumber does not exist");
		}
		return appointmentRepository
				.findByPatient_SsNumberAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAsc(
						ssNumber, LocalDate.now(), List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED));
	}

	public Patient save(Patient patient) {
		return patientRepository.save(patient);
	}

	public Optional<Patient> update(Long id, Patient patientDetails) {
		return patientRepository.findById(id).map(patient -> {
			patient.setFirstName(patientDetails.getFirstName());
			patient.setLastName(patientDetails.getLastName());
			patient.setSsNumber(patientDetails.getSsNumber());
			patient.setEmail(patientDetails.getEmail());
			patient.setPhone(patientDetails.getPhone());
			return patientRepository.save(patient);
		});
	}

	public void deleteById(Long id) {
		patientRepository.deleteById(id);
	}
}