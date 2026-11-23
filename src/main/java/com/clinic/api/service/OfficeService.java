package com.clinic.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.clinic.api.model.Office;
import com.clinic.api.repository.DoctorRepository;
import com.clinic.api.repository.OfficeRepository;

@Service
public class OfficeService {

	private final OfficeRepository officeRepository;
	private final DoctorRepository doctorRepository;

	public OfficeService(OfficeRepository officeRepository, DoctorRepository doctorRepository) {
		this.officeRepository = officeRepository;
		this.doctorRepository = doctorRepository;
	}

	public List<Office> findAll() {
		return officeRepository.findAll();
	}

	public List<Object[]> findBusiestOffice() {
		return officeRepository.findBusiestOffice(PageRequest.of(0, 1));
	}

	public Optional<Office> findById(Long id) {
		return officeRepository.findById(id);
	}

	public Office save(Office office) {
		return officeRepository.save(office);
	}

	public Optional<Office> update(Long id, Office officeDetails) {
		return officeRepository.findById(id).map(office -> {
			office.setName(officeDetails.getName());
			office.setLocation(officeDetails.getLocation());
			office.setOfficeNumber(officeDetails.getOfficeNumber());
			return officeRepository.save(office);
		});
	}

	public OfficeDeleteResult deleteById(Long id) {
		if (!officeRepository.existsById(id)) {
			return new OfficeDeleteResult(HttpStatus.NOT_FOUND, "The office with that id does not exist");
		}
		if (doctorRepository.existsByOffice_Id(id)) {
			return new OfficeDeleteResult(HttpStatus.CONFLICT,
					"Cannot delete this office while doctors are assigned to it. Reassign or unassign them first.");
		}
		officeRepository.deleteById(id);
		return new OfficeDeleteResult(HttpStatus.OK, "Office deleted successfully");
	}

	public static class OfficeDeleteResult {
		private final HttpStatus status;
		private final String message;

		public OfficeDeleteResult(HttpStatus status, String message) {
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