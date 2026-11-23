package com.clinic.api.controller;

import com.clinic.api.model.Office;
import com.clinic.api.service.OfficeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/offices")
@CrossOrigin(origins = {
        "http://localhost:5500", "http://127.0.0.1:5500",
        "http://localhost:5173", "http://127.0.0.1:5173",
        "http://localhost:4173", "http://127.0.0.1:4173"
})
public class OfficeController {

    private final OfficeService officeService;

    public OfficeController(OfficeService officeService) {
        this.officeService = officeService;
    }

    @GetMapping
    public List<Office> getAllOffices() {
        return officeService.findAll();
    }

    @GetMapping("/busiest")
    public Object getBusiestOffice() {
        List<Object[]> offices = officeService.findBusiestOffice();
        if (offices.isEmpty()) {
            return "No appointments yet";
        }
        return offices.get(0);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Office> getOfficeById(@PathVariable Long id) {
        return ResponseEntity.of(officeService.findById(id));
    }

    @PostMapping
    public Office createOffice(@RequestBody Office office) {
        return officeService.save(office);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Office> updateOffice(@PathVariable Long id, @RequestBody Office officeDetails) {
        return ResponseEntity.of(officeService.update(id, officeDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOffice(@PathVariable Long id) {
        OfficeService.OfficeDeleteResult result = officeService.deleteById(id);
        return ResponseEntity.status(result.getStatus()).body(result.getMessage());
    }
}