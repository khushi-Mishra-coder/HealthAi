package com.healthai.ai;

import java.util.ArrayList;
import java.util.List;

public class AiStructuredResponse {

    private String reply;
    private List<String> symptoms = new ArrayList<>();
    private String duration;
    private String severity;
    private String suggestedSpecialty = "General Physician";
    private boolean isEmergency = false;
    private boolean isAssessmentComplete = false;
    private String summary;

    public AiStructuredResponse() {
    }

    public AiStructuredResponse(String reply, List<String> symptoms, String duration, String severity,
                                String suggestedSpecialty, boolean isEmergency, boolean isAssessmentComplete, String summary) {
        this.reply = reply;
        this.symptoms = symptoms != null ? symptoms : new ArrayList<>();
        this.duration = duration;
        this.severity = severity;
        this.suggestedSpecialty = suggestedSpecialty != null ? suggestedSpecialty : "General Physician";
        this.isEmergency = isEmergency;
        this.isAssessmentComplete = isAssessmentComplete;
        this.summary = summary;
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

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
