package com.healthai.dto;

import jakarta.validation.constraints.NotBlank;

public class CreatePatientHistoryRequest {

    private Long chatSessionId;

    @NotBlank(message = "Symptoms cannot be blank")
    private String symptoms;

    private String duration;

    private String severity;

    @NotBlank(message = "Risk assessment result cannot be blank")
    private String riskAssessmentResult;

    private Double riskScore;

    private String conversationSummary;

    private String suggestedSpecialty;

    private String suggestedAction;

    public CreatePatientHistoryRequest() {
    }

    public CreatePatientHistoryRequest(Long chatSessionId, String symptoms, String duration, String severity,
                                      String riskAssessmentResult, Double riskScore, String conversationSummary,
                                      String suggestedSpecialty, String suggestedAction) {
        this.chatSessionId = chatSessionId;
        this.symptoms = symptoms;
        this.duration = duration;
        this.severity = severity;
        this.riskAssessmentResult = riskAssessmentResult;
        this.riskScore = riskScore;
        this.conversationSummary = conversationSummary;
        this.suggestedSpecialty = suggestedSpecialty;
        this.suggestedAction = suggestedAction;
    }

    public Long getChatSessionId() {
        return chatSessionId;
    }

    public void setChatSessionId(Long chatSessionId) {
        this.chatSessionId = chatSessionId;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getRiskAssessmentResult() {
        return riskAssessmentResult;
    }

    public void setRiskAssessmentResult(String riskAssessmentResult) {
        this.riskAssessmentResult = riskAssessmentResult;
    }

    public Double getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Double riskScore) {
        this.riskScore = riskScore;
    }

    public String getConversationSummary() {
        return conversationSummary;
    }

    public void setConversationSummary(String conversationSummary) {
        this.conversationSummary = conversationSummary;
    }

    public String getSuggestedSpecialty() {
        return suggestedSpecialty;
    }

    public void setSuggestedSpecialty(String suggestedSpecialty) {
        this.suggestedSpecialty = suggestedSpecialty;
    }

    public String getSuggestedAction() {
        return suggestedAction;
    }

    public void setSuggestedAction(String suggestedAction) {
        this.suggestedAction = suggestedAction;
    }
}
