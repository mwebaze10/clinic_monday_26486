package com.clinic.api.controller;

import com.clinic.api.model.Office;
import com.clinic.api.repository.OfficeRepository;
import com.clinic.api.service.OfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offices")
public class OfficeController {

    @Autowired
    private OfficeRepository officeRepository;

    @Autowired
    private OfficeService officeService;

    @GetMapping
    public List<Office> getAllOffices() {
        return officeRepository.findAll();
    }

    @GetMapping("/busiest")
    public Object getBusiestOffice() {
        return officeService.findBusiestOffice();
    }

    @GetMapping("/free")
    public List<Office> getFreeOffices() {
        return officeService.findFreeOffices();
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
            office.setOfficeNumber(officeDetails.getOfficeNumber());
            office.setLocation(officeDetails.getLocation());
            return officeRepository.save(office);
        }).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deleteOffice(@PathVariable Long id) {
        officeRepository.deleteById(id);
    }
}