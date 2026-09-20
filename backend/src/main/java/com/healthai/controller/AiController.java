package com.healthai.controller;

import com.healthai.ai.AiAssistantService;
import com.healthai.ai.adaptive.StructuredSymptomState;
import com.healthai.dto.AiChatRequest;
import com.healthai.dto.AiChatResponse;
import com.healthai.dto.PatientHistoryDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiAssistantService aiAssistantService;

    public AiController(AiAssistantService aiAssistantService) {
        this.aiAssistantService = aiAssistantService;
    }

    @PostMapping("/chat")
    public ResponseEntity<AiChatResponse> chat(
            Authentication authentication,
            @Valid @RequestBody AiChatRequest request) {
        AiChatResponse response = aiAssistantService.chat(
                authentication.getName(),
                request.getSessionId(),
                request.getMessage()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sessions/{sessionId}/state")
    public ResponseEntity<StructuredSymptomState> getSessionState(
            Authentication authentication,
            @PathVariable Long sessionId) {
        StructuredSymptomState state = aiAssistantService.getSessionState(
                authentication.getName(),
                sessionId
        );
        return ResponseEntity.ok(state);
    }

    @PostMapping("/evaluate/{sessionId}")
    public ResponseEntity<PatientHistoryDto> evaluateSession(
            Authentication authentication,
            @PathVariable Long sessionId) {
        PatientHistoryDto history = aiAssistantService.evaluateAndSaveSession(
                authentication.getName(),
                sessionId
        );
        return ResponseEntity.ok(history);
    }
}
