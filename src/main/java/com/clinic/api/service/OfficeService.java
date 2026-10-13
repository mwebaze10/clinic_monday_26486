package com.clinic.api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;

import com.clinic.api.model.Office;
import com.clinic.api.repository.OfficeRepository;

@Service
public class OfficeService {
    private final OfficeRepository officeRepository;

    public OfficeService(OfficeRepository officeRepository) {
        this.officeRepository = officeRepository;
    }

    public Object findBusiestOffice() {
        List<Object[]> offices = officeRepository.findOfficesByAppointmentCount(PageRequest.of(0, 1));
        return offices.isEmpty() ? "No appointments yet" : offices.get(0);
    }

    public List<Office> findFreeOffices() {
        return officeRepository.findFreeOffices();
    }
}