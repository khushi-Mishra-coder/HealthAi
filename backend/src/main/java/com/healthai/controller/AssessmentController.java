package com.healthai.controller;

import com.healthai.prediction.AssessmentService;
import com.healthai.prediction.dto.AssessmentRequest;
import com.healthai.prediction.dto.AssessmentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assessment")
public class AssessmentController {

    private final AssessmentService assessmentService;

    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @PostMapping("/evaluate")
    public ResponseEntity<AssessmentResponse> evaluate(
            @RequestBody AssessmentRequest request,
            Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        AssessmentResponse response = assessmentService.evaluate(request, userEmail);
        return ResponseEntity.ok(response);
    }
}
