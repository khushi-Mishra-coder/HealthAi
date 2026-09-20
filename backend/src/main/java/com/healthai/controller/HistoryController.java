package com.healthai.controller;

import com.healthai.dto.CreatePatientHistoryRequest;
import com.healthai.dto.PatientHistoryDto;
import com.healthai.service.PatientHistoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

    private final PatientHistoryService patientHistoryService;

    public HistoryController(PatientHistoryService patientHistoryService) {
        this.patientHistoryService = patientHistoryService;
    }

    @PostMapping
    public ResponseEntity<PatientHistoryDto> createHistory(
            Authentication authentication,
            @Valid @RequestBody CreatePatientHistoryRequest request) {
        PatientHistoryDto history = patientHistoryService.createHistory(authentication.getName(), request);
        return new ResponseEntity<>(history, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PatientHistoryDto>> getUserHistory(Authentication authentication) {
        List<PatientHistoryDto> historyList = patientHistoryService.getUserHistory(authentication.getName());
        return ResponseEntity.ok(historyList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientHistoryDto> getHistoryDetails(
            Authentication authentication,
            @PathVariable Long id) {
        PatientHistoryDto history = patientHistoryService.getHistoryDetails(authentication.getName(), id);
        return ResponseEntity.ok(history);
    }

    @PostMapping("/{id}/searched")
    public ResponseEntity<PatientHistoryDto> recordHealthcareSearch(
            Authentication authentication,
            @PathVariable Long id) {
        PatientHistoryDto updated = patientHistoryService.recordHealthcareSearch(authentication.getName(), id);
        return ResponseEntity.ok(updated);
    }
}
