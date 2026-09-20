package com.healthai.controller;

import com.healthai.healthcare.HealthcareFacilityDto;
import com.healthai.healthcare.HealthcareService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/healthcare")
public class HealthcareController {

    private final HealthcareService healthcareService;

    public HealthcareController(HealthcareService healthcareService) {
        this.healthcareService = healthcareService;
    }

    @GetMapping("/facilities")
    public ResponseEntity<List<HealthcareFacilityDto>> getNearbyFacilities(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false, defaultValue = "50.0") Double radiusKm,
            @RequestParam(required = false, defaultValue = "false") Boolean emergencyOnly,
            @RequestParam(required = false) String query) {

        List<HealthcareFacilityDto> facilities = healthcareService.findNearbyFacilities(
                lat, lng, specialty, radiusKm, emergencyOnly, query);
        return ResponseEntity.ok(facilities);
    }

    @GetMapping("/specialties")
    public ResponseEntity<List<String>> getSpecialties() {
        List<String> specialties = healthcareService.getAvailableSpecialties();
        return ResponseEntity.ok(specialties);
    }

    @GetMapping("/facilities/{id}")
    public ResponseEntity<HealthcareFacilityDto> getFacilityById(@PathVariable Long id) {
        return healthcareService.getFacilityById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
