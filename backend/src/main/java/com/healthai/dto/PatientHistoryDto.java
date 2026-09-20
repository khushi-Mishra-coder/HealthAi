package com.healthai.dto;

import java.time.LocalDateTime;

public class PatientHistoryDto {

    private Long id;
    private Long userId;
    private Long chatSessionId;
    private LocalDateTime assessmentDate;
    private String symptoms;
    private String duration;
    private String severity;
    private String riskAssessmentResult;
    private Double riskScore;
    private String conversationSummary;
    private String suggestedSpecialty;
    private String suggestedAction;
    private Integer searchesCount;
    private LocalDateTime createdAt;

    public PatientHistoryDto() {
    }

    public PatientHistoryDto(Long id, Long userId, Long chatSessionId, LocalDateTime assessmentDate, String symptoms,
                             String duration, String severity, String riskAssessmentResult, Double riskScore,
                             String conversationSummary, String suggestedSpecialty, String suggestedAction,
                             Integer searchesCount, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.chatSessionId = chatSessionId;
        this.assessmentDate = assessmentDate;
        this.symptoms = symptoms;
        this.duration = duration;
        this.severity = severity;
        this.riskAssessmentResult = riskAssessmentResult;
        this.riskScore = riskScore;
        this.conversationSummary = conversationSummary;
        this.suggestedSpecialty = suggestedSpecialty;
        this.suggestedAction = suggestedAction;
        this.searchesCount = searchesCount;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getChatSessionId() {
        return chatSessionId;
    }

    public void setChatSessionId(Long chatSessionId) {
        this.chatSessionId = chatSessionId;
    }

    public LocalDateTime getAssessmentDate() {
        return assessmentDate;
    }

    public void setAssessmentDate(LocalDateTime assessmentDate) {
        this.assessmentDate = assessmentDate;
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

    public Integer getSearchesCount() {
        return searchesCount;
    }

    public void setSearchesCount(Integer searchesCount) {
        this.searchesCount = searchesCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
