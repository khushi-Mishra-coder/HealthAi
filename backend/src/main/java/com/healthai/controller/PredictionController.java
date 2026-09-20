package com.healthai.controller;

import com.healthai.prediction.PredictionService;
import com.healthai.prediction.dto.MlPredictRequest;
import com.healthai.prediction.dto.MlPredictResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/prediction")
public class PredictionController {

    private final PredictionService predictionService;

    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @PostMapping("/predict")
    public ResponseEntity<MlPredictResponse> predict(@Valid @RequestBody MlPredictRequest request) {
        MlPredictResponse response = predictionService.predictDiseaseRisk(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = predictionService.checkStatus();
        return ResponseEntity.ok(status);
    }

    @GetMapping("/specialties")
    public ResponseEntity<List<String>> getSpecialties() {
        List<String> specialties = predictionService.getAvailableSpecialties();
        return ResponseEntity.ok(specialties);
    }
}
