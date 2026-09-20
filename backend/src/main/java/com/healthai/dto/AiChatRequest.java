package com.healthai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AiChatRequest {

    @NotNull(message = "Session ID is required")
    private Long sessionId;

    @NotBlank(message = "User message cannot be blank")
    private String message;

    public AiChatRequest() {
    }

    public AiChatRequest(Long sessionId, String message) {
        this.sessionId = sessionId;
        this.message = message;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
