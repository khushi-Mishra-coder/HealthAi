package com.healthai.prediction.dto;

import java.util.List;

public class AssessmentRequest {

    private Long sessionId;
    private List<String> symptoms;
    private Integer age;
    private String gender;
    private String duration;
    private String severity;
    private List<String> existingConditions;
    private List<String> currentMedications;
    private List<String> allergies;
    private String lifestyleFactors;
    private Boolean saveToHistory;

    public AssessmentRequest() {}

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public List<String> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(List<String> symptoms) {
        this.symptoms = symptoms;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public List<String> getExistingConditions() {
        return existingConditions;
    }

    public void setExistingConditions(List<String> existingConditions) {
        this.existingConditions = existingConditions;
    }

    public List<String> getCurrentMedications() {
        return currentMedications;
    }

    public void setCurrentMedications(List<String> currentMedications) {
        this.currentMedications = currentMedications;
    }

    public List<String> getAllergies() {
        return allergies;
    }

    public void setAllergies(List<String> allergies) {
        this.allergies = allergies;
    }

    public String getLifestyleFactors() {
        return lifestyleFactors;
    }

    public void setLifestyleFactors(String lifestyleFactors) {
        this.lifestyleFactors = lifestyleFactors;
    }

    public Boolean getSaveToHistory() {
        return saveToHistory;
    }

    public void setSaveToHistory(Boolean saveToHistory) {
        this.saveToHistory = saveToHistory;
    }
}
