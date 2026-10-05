package com.clinic.api.controller;

import com.clinic.api.model.Office;
import com.clinic.api.repository.OfficeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offices")
public class OfficeController {

    @Autowired
    private OfficeRepository officeRepository;

    @GetMapping
    public List<Office> getAllOffices() {
        return officeRepository.findAll();
    }

    @GetMapping("/{id}")
    public Office getOfficeById(@PathVariable Long id) {
        return officeRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Office createOffice(@RequestBody Office office) {
        return officeRepository.save(office);
    }

    @PutMapping("/{id}")
    public Office updateOffice(@PathVariable Long id, @RequestBody Office officeDetails) {
        return officeRepository.findById(id).map(office -> {
            office.setName(officeDetails.getName());
            office.setLocation(officeDetails.getLocation());
            return officeRepository.save(office);
        }).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deleteOffice(@PathVariable Long id) {
        officeRepository.deleteById(id);
    }
}