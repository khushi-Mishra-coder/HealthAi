package com.healthai.dto;

import java.util.List;

public class AiChatResponse {

    private Long sessionId;
    private Long userMessageId;
    private Long aiMessageId;
    private String reply;
    private List<String> symptoms;
    private String duration;
    private String severity;
    private String suggestedSpecialty;
    private boolean isEmergency;
    private boolean isAssessmentComplete;
    private String disclaimer = "Preliminary health assessment and healthcare navigation guidance only. Not medical diagnosis or treatment.";

    public AiChatResponse() {
    }

    public AiChatResponse(Long sessionId, Long userMessageId, Long aiMessageId, String reply, List<String> symptoms,
                          String duration, String severity, String suggestedSpecialty, boolean isEmergency, boolean isAssessmentComplete) {
        this.sessionId = sessionId;
        this.userMessageId = userMessageId;
        this.aiMessageId = aiMessageId;
        this.reply = reply;
        this.symptoms = symptoms;
        this.duration = duration;
        this.severity = severity;
        this.suggestedSpecialty = suggestedSpecialty;
        this.isEmergency = isEmergency;
        this.isAssessmentComplete = isAssessmentComplete;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getUserMessageId() {
        return userMessageId;
    }

    public void setUserMessageId(Long userMessageId) {
        this.userMessageId = userMessageId;
    }

    public Long getAiMessageId() {
        return aiMessageId;
    }

    public void setAiMessageId(Long aiMessageId) {
        this.aiMessageId = aiMessageId;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public List<String> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(List<String> symptoms) {
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

    public String getSuggestedSpecialty() {
        return suggestedSpecialty;
    }

    public void setSuggestedSpecialty(String suggestedSpecialty) {
        this.suggestedSpecialty = suggestedSpecialty;
    }

    public boolean isEmergency() {
        return isEmergency;
    }

    public void setEmergency(boolean emergency) {
        isEmergency = emergency;
    }

    public boolean isAssessmentComplete() {
        return isAssessmentComplete;
    }

    public void setAssessmentComplete(boolean assessmentComplete) {
        isAssessmentComplete = assessmentComplete;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }
}
