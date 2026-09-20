package com.healthai.ai.adaptive;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class StructuredSymptomState {

    private Set<String> symptoms = new LinkedHashSet<>();
    private String duration;
    private String severity;
    private Set<String> additionalSymptoms = new LinkedHashSet<>();
    private Integer age;
    private String gender;
    private String suggestedSpecialty = "General Physician";
    private boolean emergency = false;
    private boolean assessmentComplete = false;
    private String stage = "SYMPTOM_IDENTIFICATION"; // SYMPTOM_IDENTIFICATION, DURATION, SEVERITY, ASSOCIATED, COMPLETE

    public StructuredSymptomState() {
    }

    public StructuredSymptomState(Integer age, String gender) {
        this.age = age;
        this.gender = gender;
    }

    public void addSymptom(String symptom) {
        if (symptom != null && !symptom.isBlank()) {
            this.symptoms.add(symptom);
        }
    }

    public void addAdditionalSymptom(String symptom) {
        if (symptom != null && !symptom.isBlank()) {
            this.additionalSymptoms.add(symptom);
            this.symptoms.add(symptom);
        }
    }

    public List<String> getSymptoms() {
        return new ArrayList<>(symptoms);
    }

    public void setSymptoms(List<String> symptoms) {
        this.symptoms = new LinkedHashSet<>(symptoms != null ? symptoms : List.of());
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

    public List<String> getAdditionalSymptoms() {
        return new ArrayList<>(additionalSymptoms);
    }

    public void setAdditionalSymptoms(List<String> additionalSymptoms) {
        this.additionalSymptoms = new LinkedHashSet<>(additionalSymptoms != null ? additionalSymptoms : List.of());
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

    public String getSuggestedSpecialty() {
        return suggestedSpecialty;
    }

    public void setSuggestedSpecialty(String suggestedSpecialty) {
        this.suggestedSpecialty = suggestedSpecialty;
    }

    public boolean isEmergency() {
        return emergency;
    }

    public void setEmergency(boolean emergency) {
        this.emergency = emergency;
    }

    public boolean isAssessmentComplete() {
        return assessmentComplete;
    }

    public void setAssessmentComplete(boolean assessmentComplete) {
        this.assessmentComplete = assessmentComplete;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }
}
