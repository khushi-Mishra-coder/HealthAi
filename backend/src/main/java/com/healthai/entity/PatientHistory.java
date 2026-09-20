package com.healthai.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "patient_histories", indexes = {
        @Index(name = "idx_patient_histories_user", columnList = "user_id"),
        @Index(name = "idx_patient_histories_date", columnList = "assessment_date")
})
public class PatientHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_session_id")
    private ChatSession chatSession;

    @Column(name = "assessment_date", nullable = false)
    private LocalDateTime assessmentDate;

    @Column(nullable = false, length = 500)
    private String symptoms;

    @Column(length = 100)
    private String duration;

    @Column(length = 50)
    private String severity;

    @Column(name = "risk_assessment_result", nullable = false, length = 200)
    private String riskAssessmentResult;

    @Column(name = "risk_score")
    private Double riskScore;

    @Column(name = "conversation_summary", columnDefinition = "TEXT")
    private String conversationSummary;

    @Column(name = "suggested_specialty", length = 100)
    private String suggestedSpecialty;

    @Column(name = "suggested_action", length = 300)
    private String suggestedAction;

    @Column(name = "searches_count", nullable = false)
    private Integer searchesCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public PatientHistory() {
    }

    public PatientHistory(User user, ChatSession chatSession, String symptoms, String duration, String severity,
                          String riskAssessmentResult, Double riskScore, String conversationSummary,
                          String suggestedSpecialty, String suggestedAction) {
        this.user = user;
        this.chatSession = chatSession;
        this.symptoms = symptoms;
        this.duration = duration;
        this.severity = severity;
        this.riskAssessmentResult = riskAssessmentResult;
        this.riskScore = riskScore;
        this.conversationSummary = conversationSummary;
        this.suggestedSpecialty = suggestedSpecialty;
        this.suggestedAction = suggestedAction;
        this.searchesCount = 0;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.assessmentDate == null) {
            this.assessmentDate = LocalDateTime.now();
        }
        if (this.searchesCount == null) {
            this.searchesCount = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ChatSession getChatSession() {
        return chatSession;
    }

    public void setChatSession(ChatSession chatSession) {
        this.chatSession = chatSession;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
